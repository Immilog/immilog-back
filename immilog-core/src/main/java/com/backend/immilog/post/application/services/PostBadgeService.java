package com.backend.immilog.post.application.services;

import com.backend.immilog.post.application.dto.PostResult;
import com.backend.immilog.post.domain.model.post.Badge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class PostBadgeService {
    private final PostCommandService postCommandService;
    private final PostQueryService postQueryService;

    public PostBadgeService(
            PostCommandService postCommandService,
            PostQueryService postQueryService
    ) {
        this.postCommandService = postCommandService;
        this.postQueryService = postQueryService;
    }

    @Transactional
    public void updatePostBadges(
            List<PostResult> hotPosts,
            List<PostResult> mostViewedPosts
    ) {
        log.info("[BADGE UPDATE] Starting badge update for {} hot posts and {} most viewed posts",
                hotPosts.size(), mostViewedPosts.size());

        try {
            // 기존 뱃지가 없는 HOT 게시물만 업데이트
            var newHotPosts = filterPostsWithoutBadge(hotPosts);
            updateBadgesForPosts(newHotPosts, Badge.HOT);

            // 기존 뱃지가 없는 MOST_VIEWED 게시물만 업데이트
            var newMostViewedPosts = filterPostsWithoutBadge(mostViewedPosts);
            updateBadgesForPosts(newMostViewedPosts, Badge.MOST_VIEWED);

            log.info("[BADGE UPDATE] Successfully updated {} new HOT badges and {} new MOST_VIEWED badges",
                    newHotPosts.size(), newMostViewedPosts.size());
        } catch (Exception e) {
            log.error("[BADGE UPDATE] Failed to update badges", e);
            throw e;
        }
    }

    private List<PostResult> filterPostsWithoutBadge(List<PostResult> posts) {
        return posts.stream()
                .filter(postResult -> {
                    var post = postQueryService.getPostByIdOptional(postResult.postId());
                    if (post.isPresent()) {
                        var currentBadge = post.get().badge();
                        // 뱃지가 없거나 null인 경우만 필터링
                        boolean hasNoBadge = currentBadge == null;
                        if (!hasNoBadge) {
                            log.debug("[BADGE UPDATE] Post {} already has badge {}, skipping",
                                    postResult.postId(), currentBadge);
                        }
                        return hasNoBadge;
                    }
                    return false; // 게시물이 존재하지 않으면 제외
                })
                .toList();
    }

    private void clearExistingBadges() {
        log.info("[BADGE UPDATE] Clearing existing HOT and MOST_VIEWED badges");

        // HOT 뱃지 제거
        var hotPosts = postQueryService.findByBadge(Badge.HOT);
        hotPosts.forEach(post -> {
            var updatedPost = post.updateBadge(null);
            postCommandService.save(updatedPost);
        });

        // MOST_VIEWED 뱃지 제거
        var mostViewedPosts = postQueryService.findByBadge(Badge.MOST_VIEWED);
        mostViewedPosts.forEach(post -> {
            var updatedPost = post.updateBadge(null);
            postCommandService.save(updatedPost);
        });
    }

    private void updateBadgesForPosts(
            List<PostResult> posts,
            Badge badge
    ) {
        log.info("[BADGE UPDATE] Updating {} posts with {} badge", posts.size(), badge);

        posts.forEach(postResult -> {
            try {
                var post = postQueryService.getPostByIdOptional(postResult.postId());
                if (post.isPresent()) {
                    var updatedPost = post.get().updateBadge(badge);
                    postCommandService.save(updatedPost);
                    log.debug("[BADGE UPDATE] Updated post {} with badge {}", postResult.postId(), badge);
                } else {
                    log.warn("[BADGE UPDATE] Post not found: {}", postResult.postId());
                }
            } catch (Exception e) {
                log.error("[BADGE UPDATE] Failed to update badge for post: {}", postResult.postId(), e);
            }
        });
    }

    @Transactional
    public void updateWeeklyBestBadges() {
        log.info("[BADGE UPDATE] Starting WEEKLY_BEST badge update");

        try {
            // 기존 WEEKLY_BEST 뱃지 제거
            var weeklyBestPosts = postQueryService.findByBadge(Badge.WEEKLY_BEST);
            weeklyBestPosts.forEach(post -> {
                var updatedPost = post.updateBadge(null);
                postCommandService.save(updatedPost);
            });

            // 주간 베스트 게시물 계산 (조회수 + 댓글수 종합 점수)
            var from = LocalDateTime.now().minusWeeks(1);
            var to = LocalDateTime.now();

            // 주간 베스트 게시물 계산 (조회수 + 댓글수 종합 점수)
            var weeklyBestResults = getWeeklyBestPosts(from, to);
            updateBadgesForPosts(weeklyBestResults, Badge.WEEKLY_BEST);

            log.info("[BADGE UPDATE] WEEKLY_BEST badge update completed");
        } catch (Exception e) {
            log.error("[BADGE UPDATE] Failed to update WEEKLY_BEST badges", e);
        }
    }

    /**
     * 주간 베스트 게시물을 계산합니다.
     * 점수 = (조회수 × 1.0) + (댓글수 × 3.0) + (좋아요수 × 2.0)
     * 
     * @param from 시작 날짜
     * @param to 종료 날짜  
     * @return 주간 베스트 게시물 리스트
     */
    private List<PostResult> getWeeklyBestPosts(LocalDateTime from, LocalDateTime to) {
        log.info("[WEEKLY BEST] Calculating weekly best posts from {} to {}", from, to);
        
        return postQueryService.getWeeklyBestPosts(from, to);
    }
}