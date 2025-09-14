package com.backend.immilog.post.domain.service;

import com.backend.immilog.post.application.dto.out.PostResult;
import com.backend.immilog.post.domain.repositories.PostDomainRepository;
import com.backend.immilog.post.application.services.query.PostQueryService;
import com.backend.immilog.post.domain.model.post.Badge;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeManagementService {
    private final PostDomainRepository postDomainRepository;
    private final PostQueryService postQueryService;

    public void clearBadge(Badge badge) {
        log.info("[BADGE CLEAR] Clearing {} badges", badge);

        var posts = postQueryService.findByBadge(badge);
        posts.forEach(post -> {
            var updatedPost = post.updateBadge(null);
            postDomainRepository.save(updatedPost);
        });
    }

    public void applyBadge(
            List<PostResult> posts,
            Badge badge
    ) {
        log.info("[BADGE APPLY] Updating {} posts with {} badge", posts.size(), badge);

        posts.forEach(postResult -> {
            try {
                var post = postQueryService.getPostByIdOptional(postResult.postId());
                if (post.isPresent()) {
                    var updatedPost = post.get().updateBadge(badge);
                    postDomainRepository.save(updatedPost);
                    log.debug("[BADGE APPLY] Updated post {} with badge {}", postResult.postId(), badge);
                } else {
                    log.warn("[BADGE APPLY] Post not found: {}", postResult.postId());
                }
            } catch (Exception e) {
                log.error("[BADGE APPLY] Failed to update badge for post: {}", postResult.postId(), e);
            }
        });
    }

    public List<PostResult> filterPostsWithoutBadge(List<PostResult> posts) {
        return posts.stream()
                .filter(postResult -> {
                    var post = postQueryService.getPostByIdOptional(postResult.postId());
                    if (post.isPresent()) {
                        var currentBadge = post.get().badge();
                        boolean hasNoBadge = currentBadge == null;
                        if (!hasNoBadge) {
                            log.debug("[BADGE FILTER] Post {} already has badge {}, skipping",
                                    postResult.postId(), currentBadge);
                        }
                        return hasNoBadge;
                    }
                    return false;
                })
                .toList();
    }
}