package com.backend.immilog.post.application.usecase;

import com.backend.immilog.post.application.dto.PostResult;
import com.backend.immilog.post.application.mapper.PostResultAssembler;
import com.backend.immilog.post.application.services.PostQueryService;
import com.backend.immilog.post.domain.events.PostEvent;
import com.backend.immilog.post.domain.model.post.Categories;
import com.backend.immilog.post.domain.model.post.SortingMethods;
import com.backend.immilog.shared.domain.event.DomainEvents;
import com.backend.immilog.shared.enums.ContentType;
import com.backend.immilog.shared.enums.Country;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

public interface PostFetchUseCase {
    Page<PostResult> getPosts(
            Country country,
            SortingMethods sortingMethod,
            String isPublic,
            Categories category,
            Integer page
    );

    PostResult getPostDetail(String postId);

    List<PostResult> getBookmarkedPosts(
            String userId,
            ContentType contentType
    );

    Page<PostResult> searchKeyword(
            String keyword,
            Integer page
    );

    Page<PostResult> getUserPosts(
            String userId,
            Integer page
    );

    List<PostResult> getMostViewedPosts();

    List<PostResult> getHotPosts();

    @Slf4j
    @Service
    class PostFetcher implements PostFetchUseCase {
        private final PostQueryService postQueryService;
        private final PostResultAssembler postResultAssembler;
        private final EventResultStorageService eventResultStorageService;

        public PostFetcher(
                PostQueryService postQueryService,
                PostResultAssembler postResultAssembler,
                EventResultStorageService eventResultStorageService
        ) {
            this.postQueryService = postQueryService;
            this.postResultAssembler = postResultAssembler;
            this.eventResultStorageService = eventResultStorageService;
        }

        public Page<PostResult> getPosts(
                Country country,
                SortingMethods sortingMethod,
                String isPublic,
                Categories category,
                Integer page
        ) {
            final var pageable = PageRequest.of(Objects.requireNonNullElse(page, 0), 10);
            return postQueryService.getPosts(country, sortingMethod, isPublic, category, pageable);
        }

        public PostResult getPostDetail(String postId) {
            return postQueryService.getPostDetail(postId);
        }

        public List<PostResult> getBookmarkedPosts(
                String userId,
                ContentType contentType
        ) {
            // 이벤트를 통해 북마크된 게시물 ID 목록 요청
            String requestId = eventResultStorageService.generateRequestId("bookmark");
            DomainEvents.raise(new PostEvent.BookmarkPostsRequested(requestId, userId, contentType.name()));

            // Redis에서 이벤트 처리 결과 조회
            final var postIdList = getBookmarkedPostIdsFromRedis(requestId);
            return postQueryService.getPostsByPostIdList(postIdList);
        }


        public Page<PostResult> searchKeyword(
                String keyword,
                Integer page
        ) {
            final var pageable = PageRequest.of(page, 10);
            final var posts = postQueryService.getPostsByKeyword(keyword, pageable);
            return new PageImpl<>(
                    posts.getContent().stream().map(post -> postResultAssembler.assembleKeywords(post, keyword)).toList(),
                    pageable,
                    posts.getTotalElements()
            );
        }

        public Page<PostResult> getUserPosts(
                String userId,
                Integer page
        ) {
            final var pageable = PageRequest.of(Objects.requireNonNullElse(page, 0), 10);
            return postQueryService.getPostsByUserId(userId, pageable);
        }

        public List<PostResult> getMostViewedPosts() {
            return postQueryService.getPostsFromRedis("most_viewed_posts");
        }

        public List<PostResult> getHotPosts() {
            return postQueryService.getPostsFromRedis("hot_posts");
        }

        private List<String> getBookmarkedPostIdsFromRedis(String requestId) {
            // 이벤트 처리 완료까지 잠시 대기 (실제로는 더 정교한 동기화 필요)
            try {
                Thread.sleep(100); // 100ms 대기
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Interrupted while waiting for bookmark event processing", e);
            }
            
            return eventResultStorageService.getBookmarkData(requestId);
        }
    }
}
