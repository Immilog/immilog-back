package com.backend.immilog.post.application.services;

import com.backend.immilog.post.application.services.command.PopularPostCommandService;
import com.backend.immilog.post.domain.repositories.PopularPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopularPostAggregationService {
    private final PopularPostCommandService postCommandService;
    private final PopularPostRepository popularPostRepository;
    private final PostBadgeService postBadgeService;

    public void aggregatePopularPosts() {
        try {
            var to = LocalDateTime.now();
            var from = to.minusDays(30);

            var mostViewedPosts = popularPostRepository.getMostViewedPosts(from, to);
            var hotPosts = popularPostRepository.getHotPosts(from, to);

            log.info("[POPULAR POST AGGREGATION] Found {} most viewed posts and {} hot posts",
                    mostViewedPosts.size(), hotPosts.size());

            int expiration = 60 * 60;

            if (mostViewedPosts.isEmpty() && hotPosts.isEmpty()) {
                log.warn("[POPULAR POST AGGREGATION FAILED] Popular posts are empty");
                return;
            }

            if (!mostViewedPosts.isEmpty()) {
                postCommandService.saveMostViewedPosts(mostViewedPosts, expiration);
            }
            if (!hotPosts.isEmpty()) {
                postCommandService.saveHotPosts(hotPosts, expiration);
            }

            postBadgeService.updatePostBadges(hotPosts);

        } catch (Exception e) {
            log.error("[POPULAR POST AGGREGATION FAILED] Failed to save popular posts", e);
        }
    }
}