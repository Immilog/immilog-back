package com.backend.immilog.comment.application.dto;

import com.backend.immilog.comment.domain.model.Comment;
import com.backend.immilog.comment.domain.model.ReferenceType;
import com.backend.immilog.comment.presentation.payload.CommentResponse;
import com.backend.immilog.shared.enums.ContentStatus;

import java.time.LocalDateTime;

public record CommentResult(
        String id,
        String userId,
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
    public static CommentResult from(Comment comment) {
        return new CommentResult(
                comment.id(),
                comment.userId(),
                comment.content(),
                comment.postId() != null ? comment.postId() : null,
                comment.parentId() != null ? comment.parentId() : null,
                comment.referenceType(),
                comment.replyCount(),
                comment.likeCount(),
                comment.status(),
                comment.createdAt(),
                comment.updatedAt()
        );
    }

    public CommentResponse.CommentInformation toInfraDTO() {
        return new CommentResponse.CommentInformation(
                this.id,
                this.userId,
                this.content,
                this.postId,
                this.parentId,
                this.referenceType,
                this.replyCount,
                this.likeCount,
                this.status,
                this.createdAt,
                this.updatedAt
        );
    }
}