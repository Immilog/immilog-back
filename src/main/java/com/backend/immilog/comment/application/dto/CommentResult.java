package com.backend.immilog.comment.application.dto;

import com.backend.immilog.comment.domain.model.ReferenceType;
import com.backend.immilog.comment.presentation.payload.CommentResponse;
import com.backend.immilog.shared.enums.ContentStatus;
import com.backend.immilog.shared.enums.Country;

import java.time.LocalDateTime;

public record CommentResult(
        String id,
        String userId,
        String nickname,
        String userProfileUrl,
        Country country,
        String region,
        String content,
        String postId,
        String parentId,
        ReferenceType referenceType,
        int replyCount,
        Integer likeCount,
        ContentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public CommentResponse.CommentInformation toInfraDTO() {
        return new CommentResponse.CommentInformation(
                this.id,
                this.userId,
                this.nickname,
                this.userProfileUrl,
                this.country,
                this.region,
                this.content,
                this.postId,
                this.parentId,
                this.referenceType,
                this.replyCount,
                this.likeCount,
                new java.util.ArrayList<>(), // 빈 likeUsers 리스트
                new java.util.ArrayList<>(), // 빈 bookmarkUsers 리스트
                this.status,
                this.createdAt,
                this.updatedAt,
                new java.util.ArrayList<>() // 빈 replies 리스트
        );
    }
}