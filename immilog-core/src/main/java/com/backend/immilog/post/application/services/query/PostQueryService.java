package com.backend.immilog.post.application.services.query;

import com.backend.immilog.post.application.dto.out.PostResult;
import com.backend.immilog.post.application.mapper.PostResultAssembler;
import com.backend.immilog.post.application.mapper.PostResultConverter;
import com.backend.immilog.post.application.services.PostCommentDataService;
import com.backend.immilog.post.domain.events.PostEvent;
import com.backend.immilog.post.domain.model.post.Badge;
import com.backend.immilog.post.domain.model.post.Categories;
import com.backend.immilog.post.domain.model.post.Post;
import com.backend.immilog.post.domain.model.post.SortingMethods;
import com.backend.immilog.post.domain.repositories.PostDomainRepository;
import com.backend.immilog.post.domain.service.PostScoreCalculator;
import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.shared.aop.annotation.PerformanceMonitor;
import com.backend.immilog.shared.domain.event.DomainEvents;
import com.backend.immilog.shared.domain.model.Resource;
import com.backend.immilog.shared.enums.ContentType;
import com.backend.immilog.shared.infrastructure.DataRepository;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostQueryService {
    private final ObjectMapper objectMapper;
    private final PostDomainRepository postDomainRepository;
    private final DataRepository redisDataRepository;
    private final PostResourceQueryService postResourceQueryService;
    private final PostResultAssembler postResultAssembler;
    private final EventResultStorageService eventResultStorageService;
    private final PostCommentDataService postCommentDataService;
    private final PostResultConverter postResultConverter;
    private final PostScoreCalculator postScoreCalculator;

    @Transactional(readOnly = true)
    public Post getPostById(String postId) {
        return postDomainRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
    }

    public Optional<Post> getPostByIdOptional(String postId) {
        return postDomainRepository.findById(postId);
    }

    @PerformanceMonitor
    @Transactional(readOnly = true)
    public Page<PostResult> getPosts(
            String countryId,
            SortingMethods sortingMethod,
            String isPublic,
            Categories category,
            Pageable pageable
    ) {
        var posts = postDomainRepository.findPosts(
                countryId,
                sortingMethod,
                isPublic,
                category,
                pageable
        );
        var postIdList = posts.stream().map(post -> post.id().value()).toList();
        var postResults = posts.map(postResultConverter::convertToPostResult);
        return this.assemblePostResult(postIdList, postResults);
    }

    @Transactional(readOnly = true)
    public Page<PostResult> getPostsByKeyword(
            String keyword,
            Pageable pageable
    ) {
        var posts = postDomainRepository.findPostsByKeyword(keyword, pageable);
        var postIdList = posts.stream().map(post -> post.id().value()).toList();
        var postResults = posts.map(postResultConverter::convertToPostResult);
        var updatedPostResultsPage = new PageImpl<>(
                postResults.getContent().stream()
                        .map(post -> postResultAssembler.assembleKeywords(post, keyword))
                        .toList(),
                pageable,
                postResults.getTotalElements()
        );
        return this.assemblePostResult(postIdList, updatedPostResultsPage);
    }

    @Transactional(readOnly = true)
    public PostResult getPostDetail(String postId) {
        var post = postDomainRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
        var posts = new PageImpl<>(List.of(post));
        var postResult = posts.map(postResultConverter::convertToPostResult);
        return this.assemblePostResult(List.of(postId), postResult).getContent().getFirst();
    }

    @Transactional(readOnly = true)
    public Page<PostResult> getPostsByUserId(
            String userId,
            Pageable pageable
    ) {
        var posts = postDomainRepository.findPostsByUserId(userId, pageable);
        var postResults = posts.map(postResultConverter::convertToPostResult);
        return this.assemblePostResult(
                posts.stream().map(post -> post.id().value()).toList(),
                postResults
        );
    }

    public List<PostResult> getPostsFromRedis(String key) {
        var jsonData = redisDataRepository.findByKey(key);
        if (jsonData == null) {
            log.info("No data found with key {}", key);
            return List.of();
        }
        try {
            return objectMapper.readValue(jsonData, new TypeReference<List<PostResult>>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to parse json data", e);
        } catch (Exception e) {
            log.error("Failed to get popular posts with key {}", key, e);
        }
        return List.of();
    }

    public List<PostResult> getPostsByPostIdList(List<String> postIdList) {
        var postResults = postDomainRepository.findPostsByIdList(postIdList)
                .stream()
                .map(postResultConverter::convertToPostResult)
                .toList();
        return this.assemblePostResult(postIdList, new PageImpl<>(postResults)).toList();
    }

    private Page<PostResult> assemblePostResult(
            List<String> resultIdList,
            Page<PostResult> postResults
    ) {
        var orderMap = IntStream.range(0, resultIdList.size())
                .boxed()
                .collect(Collectors.toMap(resultIdList::get, i -> i, (existing, replacement) -> existing));

        // 유저 정보 요청을 위해 고유한 userId 목록 추출
        var userIds = postResults.getContent().stream()
                .map(PostResult::userId)
                .distinct()
                .toList();
        
        // 이벤트를 통해 유저 데이터 요청
        String userRequestId = eventResultStorageService.generateRequestId("user");
        log.info("Requesting user data for {} users with requestId: {}, userIds: {}", userIds.size(), userRequestId, userIds);
        eventResultStorageService.registerEventProcessing(userRequestId);
        DomainEvents.raise(new PostEvent.UserDataRequested(userRequestId, userIds));
        var userData = eventResultStorageService.waitForUserData(userRequestId, java.time.Duration.ofSeconds(2));
        log.info("Retrieved {} user data items via event for requestId: {}", userData.size(), userRequestId);
        
        // 이벤트를 통해 인터랙션 데이터 요청 (CompletableFuture로 동기화)
        String interactionRequestId = eventResultStorageService.generateRequestId("interaction");
        log.info("Requesting interaction data for {} posts with requestId: {}, postIds: {}", resultIdList.size(), interactionRequestId, resultIdList);
        
        // Future 등록
        eventResultStorageService.registerEventProcessing(interactionRequestId);
        
        // 이벤트 발행
        DomainEvents.raise(new PostEvent.InteractionDataRequested(interactionRequestId, resultIdList, ContentType.POST.name()));

        // 이벤트 처리 완료 대기 (최대 2초)
        var interactionUsers = eventResultStorageService.waitForInteractionData(interactionRequestId, java.time.Duration.ofSeconds(2));
        log.info("Retrieved {} interaction data items via event for requestId: {}", interactionUsers.size(), interactionRequestId);
        
        // 실시간 댓글 개수 조회
        // 이벤트 기반으로 댓글 데이터 요청
        var commentData = postCommentDataService.getCommentData(resultIdList);
        log.info("Retrieved comment data for {} posts via events", commentData.size());
        
        var postResources = postResourceQueryService.getResourcesByPostIdList(resultIdList, ContentType.POST);

        return postResults.map(postResult -> {
            // 유저 데이터 찾기
            var user = userData.stream()
                    .filter(u -> u.userId().equals(postResult.userId()))
                    .findFirst()
                    .orElse(null);
            
            var resources = postResources.stream()
                    .filter(postResource -> postResource.postId().equals(postResult.postId()))
                    .map(pr -> new Resource(
                            pr.id(),
                            pr.postId(),
                            pr.contentType(),
                            com.backend.immilog.shared.domain.model.ResourceType.valueOf(pr.resourceType().name()),
                            pr.content()
                    ))
                    .sorted(Comparator.comparingInt(pr -> orderMap.getOrDefault(pr.entityId(), Integer.MAX_VALUE)))
                    .toList();

            var interactionDataList = interactionUsers.stream()
                    .filter(interactionData -> interactionData.postId().equals(postResult.postId()))
                    .sorted(Comparator.comparingInt(id -> orderMap.getOrDefault(id.postId(), Integer.MAX_VALUE)))
                    .toList();

            var postResultWithUserData = postResultAssembler.assembleUserData(
                    postResult,
                    user
            );
            var postResultWithNewInteractionUsers = postResultAssembler.assembleInteractionData(
                    postResultWithUserData,
                    interactionDataList
            );
            var postResultWithNewResources = postResultAssembler.assembleResources(
                    postResultWithNewInteractionUsers,
                    resources
            );
            // 좋아요 수 실시간 계산 (ACTIVE 상태의 LIKE만 카운트)
            long likeCount = interactionDataList.stream()
                    .filter(interaction -> "LIKE".equals(interaction.interactionType()) &&
                            "ACTIVE".equals(interaction.interactionStatus())).count();

            // 댓글 수 실시간 적용 (이벤트 기반)
            long commentCount = commentData.stream()
                    .filter(comment -> comment.postId().equals(postResult.postId()))
                    .count();
            
            var postResultWithLikeCount = postResultAssembler.assembleLikeCount(
                    postResultWithNewResources,
                    likeCount
            );
            
            return postResultAssembler.assembleCommentCount(
                    postResultWithLikeCount,
                    commentCount
            );
        });
    }



    public List<Post> findByBadge(Badge badge) {
        log.info("[POST QUERY] Finding posts with badge: {}", badge);

        if (badge == null) {
            log.warn("[POST QUERY] Badge is null, returning empty list");
            return List.of();
        }

        var posts = postDomainRepository.findByBadge(badge);
        if (posts.isEmpty()) {
            log.info("[POST QUERY] No posts found with badge: {}", badge);
        } else {
            log.info("[POST QUERY] Found {} posts with badge: {}", posts.size(), badge);
        }

        return posts;
    }

    /**
     * 주간 베스트 게시물을 조회합니다.
     * 점수 = (조회수 × 1.0) + (댓글수 × 3.0) + (좋아요수 × 2.0) 기준으로 정렬하여 상위 10개를 반환합니다.
     * 
     * @param from 시작 날짜
     * @param to 종료 날짜
     * @return 주간 베스트 게시물 리스트
     */
    public List<PostResult> getWeeklyBestPosts(LocalDateTime from, LocalDateTime to) {
        log.info("[WEEKLY BEST] Querying weekly best posts from {} to {}", from, to);
        
        // 기간 내 모든 공개 게시물 조회 (조회수 또는 댓글수 최소 조건 충족)
        var posts = postDomainRepository.findPostsInPeriod(from, to);
        
        // 게시물 ID 리스트 추출하여 실시간 데이터 어셈블링
        var postIdList = posts.stream()
                .filter(post -> "Y".equals(post.isPublicValue())) // 공개 게시물만
                .filter(post -> 
                    (post.viewCount() != null && post.viewCount() >= 10) ||  // 조회수 10회 이상 또는
                            (post.commentCount() != null && post.commentCountValue() >= 2) // 댓글 2개 이상
                )
                .map(post -> post.id().value())
                .toList();

        // 실시간 데이터 어셈블링으로 좋아요 수 포함
        var assembledPosts = this.assemblePostResult(
                postIdList,
            new PageImpl<>(posts.stream()
                    .filter(post -> postIdList.contains(post.id().value()))
                    .map(postResultConverter::convertToPostResult)
                .toList()));

        var weeklyBestPosts = assembledPosts.getContent().stream()
                .map(postResult -> new ScoredPostResult(postResult, postScoreCalculator.calculate(postResult)))
                .sorted((a, b) -> Double.compare(b.score(), a.score())) // 점수 내림차순 정렬
                .limit(10) // 상위 10개
                .map(ScoredPostResult::postResult)
                .toList();
        
        log.info("[WEEKLY BEST] Found {} weekly best posts", weeklyBestPosts.size());
        return weeklyBestPosts;
    }

    /**
     * 사용자가 북마크한 게시물을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param contentType 콘텐츠 타입
     * @return 북마크한 게시물 리스트
     */
    @PerformanceMonitor
    @Transactional(readOnly = true)
    public List<PostResult> getBookmarkedPosts(String userId, ContentType contentType) {
        log.info("[BOOKMARK POSTS] Querying bookmarked posts for user: {}, contentType: {}", userId, contentType);
        
        // 이벤트를 통해 북마크 데이터 요청
        String bookmarkRequestId = eventResultStorageService.generateRequestId("bookmark");
        log.info("Requesting bookmark data for user {} with requestId: {}", userId, bookmarkRequestId);
        
        eventResultStorageService.registerEventProcessing(bookmarkRequestId);
        DomainEvents.raise(new PostEvent.BookmarkPostsRequested(bookmarkRequestId, userId, contentType.name()));
        
        // 이벤트 처리 완료 대기 (최대 2초)
        var bookmarkedPostIds = eventResultStorageService.waitForBookmarkData(bookmarkRequestId, java.time.Duration.ofSeconds(2));
        log.info("Retrieved {} bookmarked post IDs via event for requestId: {}", bookmarkedPostIds.size(), bookmarkRequestId);
        
        if (bookmarkedPostIds.isEmpty()) {
            log.info("[BOOKMARK POSTS] No bookmarked posts found for user: {}", userId);
            return List.of();
        }
        
        // 북마크된 게시물 ID로 실제 게시물 조회
        var bookmarkedPosts = postDomainRepository.findPostsByIdList(bookmarkedPostIds);
        
        if (bookmarkedPosts.isEmpty()) {
            log.warn("[BOOKMARK POSTS] No posts found for bookmarked IDs: {}", bookmarkedPostIds);
            return List.of();
        }
        
        // PostResult로 변환
        var postResults = bookmarkedPosts.stream()
                .map(postResultConverter::convertToPostResult)
                .toList();
        
        // 실시간 데이터 어셈블링 (좋아요, 댓글 등)
        var assembledResults = this.assemblePostResult(bookmarkedPostIds, new PageImpl<>(postResults));
        
        log.info("[BOOKMARK POSTS] Successfully retrieved {} bookmarked posts for user: {}", assembledResults.getContent().size(), userId);
        return assembledResults.getContent();
    }

    /**
     * 점수가 포함된 게시물 결과를 위한 내부 레코드
     */
    private record ScoredPostResult(PostResult postResult, double score) {}
}
