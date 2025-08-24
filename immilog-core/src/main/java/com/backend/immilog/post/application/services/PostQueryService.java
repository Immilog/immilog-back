package com.backend.immilog.post.application.services;

import com.backend.immilog.comment.application.services.CommentQueryService;
import com.backend.immilog.post.application.dto.PostResult;
import com.backend.immilog.post.application.mapper.PostResultAssembler;
import com.backend.immilog.post.domain.events.PostEvent;
import com.backend.immilog.post.domain.model.post.Badge;
import com.backend.immilog.post.domain.model.post.Categories;
import com.backend.immilog.post.domain.model.post.Post;
import com.backend.immilog.post.domain.model.post.SortingMethods;
import com.backend.immilog.post.domain.repositories.PostDomainRepository;
import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.shared.aop.annotation.PerformanceMonitor;
import com.backend.immilog.shared.domain.event.DomainEvents;
import com.backend.immilog.shared.domain.model.Resource;
import com.backend.immilog.shared.enums.ContentType;
import com.backend.immilog.shared.infrastructure.DataRepository;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import com.backend.immilog.user.application.services.query.UserQueryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class PostQueryService {
    private final ObjectMapper objectMapper;
    private final PostDomainRepository postDomainRepository;
    private final DataRepository redisDataRepository;
    private final PostResourceQueryService postResourceQueryService;
    private final PostResultAssembler postResultAssembler;
    private final EventResultStorageService eventResultStorageService;
    private final CommentQueryService commentQueryService;
    private final UserQueryService userQueryService;

    public PostQueryService(
            ObjectMapper objectMapper,
            PostDomainRepository postDomainRepository,
            DataRepository redisDataRepository,
            PostResourceQueryService postResourceQueryService,
            PostResultAssembler postResultAssembler,
            EventResultStorageService eventResultStorageService,
            CommentQueryService commentQueryService,
            UserQueryService userQueryService
    ) {
        this.objectMapper = objectMapper;
        this.postDomainRepository = postDomainRepository;
        this.redisDataRepository = redisDataRepository;
        this.postResourceQueryService = postResourceQueryService;
        this.postResultAssembler = postResultAssembler;
        this.eventResultStorageService = eventResultStorageService;
        this.commentQueryService = commentQueryService;
        this.userQueryService = userQueryService;
    }

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
        var postIdList = posts.stream().map(Post::id).toList();
        var postResults = posts.map(this::convertToPostResult);
        return this.assemblePostResult(postIdList, postResults);
    }

    @Transactional(readOnly = true)
    public Page<PostResult> getPostsByKeyword(
            String keyword,
            Pageable pageable
    ) {
        var posts = postDomainRepository.findPostsByKeyword(keyword, pageable);
        var postIdList = posts.stream().map(Post::id).toList();
        var postResults = posts.map(this::convertToPostResult);
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
        var postResult = posts.map(this::convertToPostResult);
        return this.assemblePostResult(List.of(postId), postResult).getContent().getFirst();
    }

    @Transactional(readOnly = true)
    public Page<PostResult> getPostsByUserId(
            String userId,
            Pageable pageable
    ) {
        var posts = postDomainRepository.findPostsByUserId(userId, pageable);
        var postResults = posts.map(this::convertToPostResult);
        return this.assemblePostResult(
                posts.stream().map(Post::id).toList(),
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
                .map(this::convertToPostResult)
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

        // 이벤트를 통해 인터랙션 데이터 요청 (CompletableFuture로 동기화)
        String requestId = eventResultStorageService.generateRequestId("interaction");
        log.debug("Requesting interaction data for {} posts with requestId: {}", resultIdList.size(), requestId);
        
        // Future 등록
        eventResultStorageService.registerEventProcessing(requestId);
        
        // 이벤트 발행
        DomainEvents.raise(new PostEvent.InteractionDataRequested(requestId, resultIdList, ContentType.POST.name()));

        // 이벤트 처리 완료 대기 (최대 2초)
        var interactionUsers = eventResultStorageService.waitForInteractionData(requestId, java.time.Duration.ofSeconds(2));
        log.debug("Retrieved {} interaction data items via event", interactionUsers.size());
        
        // 실시간 댓글 개수 조회
        var commentCounts = commentQueryService.getCommentCountsByPostIds(resultIdList);
        log.debug("Retrieved comment counts for {} posts", commentCounts.size());
        
        var postResources = postResourceQueryService.getResourcesByPostIdList(resultIdList, ContentType.POST);

        return postResults.map(postResult -> {
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

            var postResultWithNewInteractionUsers = postResultAssembler.assembleInteractionData(
                    postResult,
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
            
            // 댓글 수 실시간 적용
            long commentCount = commentCounts.getOrDefault(postResult.postId(), 0L);
            
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

    private PostResult convertToPostResult(Post post) {
        var user = userQueryService.getUserById(post.userId());
        return new PostResult(
                post.id(),
                post.userId(),
                user.getImageUrl(),
                user.getNickname(),
                post.commentCount(),
                post.viewCount(),
                0L, // likeCount는 실시간 계산으로 변경됨
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                post.isPublic(),
                post.countryId(),
                post.region(),
                post.category(),
                post.status(),
                post.createdAt().toString(),
                post.updatedAt().toString(),
                post.title(),
                post.content(),
                null
        );
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
}
