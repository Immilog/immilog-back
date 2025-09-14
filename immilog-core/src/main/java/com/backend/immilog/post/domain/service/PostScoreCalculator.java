package com.backend.immilog.post.domain.service;

import com.backend.immilog.post.application.dto.out.PostResult;
import org.springframework.stereotype.Service;

@Service
public class PostScoreCalculator {
    private static final double VIEW_WEIGHT = 1.0;
    private static final double COMMENT_WEIGHT = 3.0;
    private static final double LIKE_WEIGHT = 2.0;

    public double calculate(PostResult post) {
        double score = 0.0;

        if (post.viewCount() != null) {
            score += post.viewCount() * VIEW_WEIGHT;
        }
        if (post.commentCount() != null) {
            score += post.commentCount() * COMMENT_WEIGHT;
        }
        if (post.likeCount() != null) {
            score += post.likeCount() * LIKE_WEIGHT;
        }

        return score;
    }
}