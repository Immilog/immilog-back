package com.backend.immilog.post.application.usecase;

import com.backend.immilog.post.application.services.PostBadgeService;
import com.backend.immilog.post.application.services.PostCommandService;
import com.backend.immilog.post.domain.repositories.PopularPostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

public interface PopularPostFetchUseCase {
    void aggregatePopularPosts();

    @Slf4j
    @Service
    class PopularPostFetcher implements PopularPostFetchUseCase {
        private final PostCommandService postCommandService;
        private final PopularPostRepository popularPostRepository;
        private final PostBadgeService postBadgeService;

        public PopularPostFetcher(
                PostCommandService postCommandService,
                PopularPostRepository popularPostRepository,
                PostBadgeService postBadgeService
        ) {
            this.postCommandService = postCommandService;
            this.popularPostRepository = popularPostRepository;
            this.postBadgeService = postBadgeService;
        }

        public void aggregatePopularPosts() {
            try {
                var to = LocalDateTime.now();
                var from = to.minusDays(30); // 최근 30일로 확장
                
                var mostViewedPosts = popularPostRepository.getMostViewedPosts(from, to);
                var hotPosts = popularPostRepository.getHotPosts(from, to);
                
                log.info("[POPULAR POST AGGREGATION] Found {} most viewed posts and {} hot posts", 
                        mostViewedPosts.size(), hotPosts.size());

                int expiration = 60 * 60; // 1시간
                
                if (mostViewedPosts.isEmpty() && hotPosts.isEmpty()) {
                    log.warn("[POPULAR POST AGGREGATION FAILED] Popular posts are empty");
                    return;
                }
                
                // Redis 캐시 저장
                if (!mostViewedPosts.isEmpty()) {
                    postCommandService.saveMostViewedPosts(mostViewedPosts, expiration);
                }
                if (!hotPosts.isEmpty()) {
                    postCommandService.saveHotPosts(hotPosts, expiration);
                }
                
                // 뱃지 업데이트
                postBadgeService.updatePostBadges(hotPosts, mostViewedPosts);
                
            } catch (Exception e) {
                log.error("[POPULAR POST AGGREGATION FAILED] Failed to save popular posts", e);
            }
        }
    }
}

