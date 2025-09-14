package com.backend.immilog.post.application.services;

import com.backend.immilog.post.application.dto.out.PostResult;
import com.backend.immilog.post.application.services.query.PostQueryService;
import com.backend.immilog.post.domain.model.post.Badge;
import com.backend.immilog.post.domain.service.BadgeManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostBadgeService {
    private final PostQueryService postQueryService;
    private final BadgeManagementService badgeManagementService;

    @Transactional
    public void updatePostBadges(List<PostResult> hotPosts) {
        log.info("[BADGE UPDATE] Starting badge update for {} hot posts", hotPosts.size());

        try {
            var newHotPosts = badgeManagementService.filterPostsWithoutBadge(hotPosts);
            badgeManagementService.applyBadge(newHotPosts, Badge.HOT);

            log.info("[BADGE UPDATE] Successfully updated {} new HOT badges", newHotPosts.size());
        } catch (Exception e) {
            log.error("[BADGE UPDATE] Failed to update badges", e);
            throw e;
        }
    }




    @Transactional
    public void updateWeeklyBestBadges() {
        log.info("[BADGE UPDATE] Starting WEEKLY_BEST badge update");

        try {
            badgeManagementService.clearBadge(Badge.WEEKLY_BEST);

            var from = LocalDateTime.now().minusWeeks(1);
            var to = LocalDateTime.now();

            var weeklyBestResults = getWeeklyBestPosts(from, to);
            badgeManagementService.applyBadge(weeklyBestResults, Badge.WEEKLY_BEST);

            log.info("[BADGE UPDATE] WEEKLY_BEST badge update completed");
        } catch (Exception e) {
            log.error("[BADGE UPDATE] Failed to update WEEKLY_BEST badges", e);
        }
    }

    private List<PostResult> getWeeklyBestPosts(LocalDateTime from, LocalDateTime to) {
        log.info("[WEEKLY BEST] Calculating weekly best posts from {} to {}", from, to);
        
        return postQueryService.getWeeklyBestPosts(from, to);
    }
}