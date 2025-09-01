package com.backend.immilog.post.application.usecase;

import com.backend.immilog.post.application.dto.PopularPostMenuResponse;
import com.backend.immilog.post.application.dto.PostResult;
import com.backend.immilog.post.application.services.PostQueryService;
import com.backend.immilog.post.domain.model.post.Badge;
import com.backend.immilog.post.domain.model.post.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PopularPostMenuUseCase {
    PopularPostMenuResponse getPopularPostMenu();

    @Slf4j
    @Service
    class PopularPostMenuFetcher implements PopularPostMenuUseCase {
        private final PostQueryService postQueryService;

        public PopularPostMenuFetcher(PostQueryService postQueryService) {
            this.postQueryService = postQueryService;
        }

        @Override
        public PopularPostMenuResponse getPopularPostMenu() {
            log.info("[POPULAR MENU] Fetching popular post menu");

            try {
                // HOT과 WEEKLY_BEST 조회
                var hotPosts = getHotPosts();  
                var weeklyBest = getWeeklyBestPosts();

                log.info("[POPULAR MENU] Successfully fetched menu - Hot: {}, WeeklyBest: {}", 
                        hotPosts.size(), weeklyBest.size());

                return PopularPostMenuResponse.of(hotPosts, weeklyBest);
                
            } catch (Exception e) {
                log.error("[POPULAR MENU] Failed to fetch popular post menu", e);
                // 빈 리스트로 안전하게 반환
                return PopularPostMenuResponse.of(List.of(), List.of());
            }
        }

        /**
         * 주간 베스트 게시물 조회 (뱃지 기반)
         * 매주 월요일마다 업데이트되는 WEEKLY_BEST 뱃지 게시물들 조회
         */
        private List<PostResult> getWeeklyBestPosts() {
            try {
                // WEEKLY_BEST 뱃지가 달린 게시물들 조회 (최대 5개)
                var weeklyBestPosts = postQueryService.findByBadge(Badge.WEEKLY_BEST);
                return postQueryService.getPostsByPostIdList(
                    weeklyBestPosts.stream()
                        .map(Post::id)
                        .limit(5) // 최대 5개로 제한
                        .toList()
                );
                
            } catch (Exception e) {
                log.error("[POPULAR MENU] Failed to fetch weekly best posts", e);
                return List.of();
            }
        }

        /**
         * 핫 게시물 조회 (Redis 캐시에서 최대 5개)
         */
        private List<PostResult> getHotPosts() {
            try {
                var hotPosts = postQueryService.getPostsFromRedis("hot_posts");
                return hotPosts.stream()
                    .limit(5) // 최대 5개로 제한
                    .toList();
                
            } catch (Exception e) {
                log.error("[POPULAR MENU] Failed to fetch hot posts", e);
                return List.of();
            }
        }
    }
}