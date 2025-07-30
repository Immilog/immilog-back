package com.backend.immilog.comment.application.dto;

import com.backend.immilog.comment.domain.model.Comment;
import com.backend.immilog.comment.domain.model.ReferenceType;
import com.backend.immilog.post.domain.model.post.PostStatus;

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
        PostStatus status,
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
}