package com.backend.immilog.post.application.usecase;

import com.backend.immilog.post.application.dto.PostResult;
import com.backend.immilog.post.application.mapper.PostResultAssembler;
import com.backend.immilog.post.application.services.PostInteractionDataService;
import com.backend.immilog.post.application.services.PostQueryService;
import com.backend.immilog.post.domain.events.PostEvent;
import com.backend.immilog.post.domain.model.post.Categories;
import com.backend.immilog.post.domain.model.post.SortingMethods;
import com.backend.immilog.shared.domain.event.DomainEvents;
import com.backend.immilog.shared.enums.ContentType;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

public interface FetchPostUseCase {
    Page<PostResult> getPosts(
            String countryId,
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


    @Slf4j
    @Service
    class FetcherPost implements FetchPostUseCase {
        private final PostQueryService postQueryService;
        private final PostResultAssembler postResultAssembler;
        private final EventResultStorageService eventResultStorageService;
        private final PostInteractionDataService postInteractionDataService;

        public FetcherPost(
                PostQueryService postQueryService,
                PostResultAssembler postResultAssembler,
                EventResultStorageService eventResultStorageService,
                PostInteractionDataService postInteractionDataService
        ) {
            this.postQueryService = postQueryService;
            this.postResultAssembler = postResultAssembler;
            this.eventResultStorageService = eventResultStorageService;
            this.postInteractionDataService = postInteractionDataService;
        }

        public Page<PostResult> getPosts(
                String countryId,
                SortingMethods sortingMethod,
                String isPublic,
                Categories category,
                Integer page
        ) {
            final var pageable = PageRequest.of(Objects.requireNonNullElse(page, 0), 10);
            return postQueryService.getPosts(countryId, sortingMethod, isPublic, category, pageable);
        }

        public PostResult getPostDetail(String postId) {
            return postQueryService.getPostDetail(postId);
        }

        public List<PostResult> getBookmarkedPosts(
                String userId,
                ContentType contentType
        ) {
            // 이벤트를 통해 북마크된 게시물 ID 목록 요청 (CompletableFuture로 동기화)
            String requestId = eventResultStorageService.generateRequestId("bookmark");
            log.debug("Requesting bookmark data for userId: {} with requestId: {}", userId, requestId);
            
            // Future 등록
            eventResultStorageService.registerEventProcessing(requestId);
            
            // 이벤트 발행
            DomainEvents.raise(new PostEvent.BookmarkPostsRequested(requestId, userId, contentType.name()));

            // 이벤트 처리 완료 대기 (최대 2초)
            final var postIdList = eventResultStorageService.waitForBookmarkData(requestId, java.time.Duration.ofSeconds(2));
            log.debug("Retrieved {} bookmarked post IDs via event for user: {}", postIdList.size(), userId);
            
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


    }
}
