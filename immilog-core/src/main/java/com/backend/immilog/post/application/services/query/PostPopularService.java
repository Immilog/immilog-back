package com.backend.immilog.post.application.services.query;

import com.backend.immilog.post.application.dto.out.PopularPostMenuResponse;
import com.backend.immilog.post.application.dto.out.PostResult;
import com.backend.immilog.post.domain.model.post.Badge;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostPopularService {
    private final PostQueryService postQueryService;

    public PopularPostMenuResponse getPopularPostMenu() {
        log.info("[POPULAR MENU] Fetching popular post menu");

        try {
            var hotPosts = getHotPosts();
            var weeklyBest = getWeeklyBestPosts();

            log.info("[POPULAR MENU] Successfully fetched menu - Hot: {}, WeeklyBest: {}",
                    hotPosts.size(), weeklyBest.size());

            return new PopularPostMenuResponse(hotPosts, weeklyBest);

        } catch (Exception e) {
            log.error("[POPULAR MENU] Failed to fetch popular post menu", e);
            return new PopularPostMenuResponse(List.of(), List.of());
        }
    }

    private List<PostResult> getWeeklyBestPosts() {
        try {
            var weeklyBestPosts = postQueryService.findByBadge(Badge.WEEKLY_BEST);
            return postQueryService.getPostsByPostIdList(
                    weeklyBestPosts.stream()
                            .sorted((a, b) -> b.createdAt().compareTo(a.createdAt()))
                            .map(post -> post.id().value())
                            .limit(5)
                            .toList()
            );

        } catch (Exception e) {
            log.error("[POPULAR MENU] Failed to fetch weekly best posts", e);
            return List.of();
        }
    }

    private List<PostResult> getHotPosts() {
        try {
            var hotPostsFromCache = postQueryService.getPostsFromRedis("hot_posts");

            if (hotPostsFromCache.isEmpty()) {
                return List.of();
            }

            var postIds = hotPostsFromCache.stream()
                    .sorted((a, b) -> b.createdAt().compareTo(a.createdAt()))
                    .map(PostResult::postId)
                    .limit(5)
                    .toList();

            return postQueryService.getPostsByPostIdList(postIds);

        } catch (Exception e) {
            log.error("[POPULAR MENU] Failed to fetch hot posts", e);
            return List.of();
        }
    }
}