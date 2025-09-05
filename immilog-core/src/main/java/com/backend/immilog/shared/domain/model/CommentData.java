package com.backend.immilog.shared.domain.model;

public record CommentData(
    String commentId,
    String postId,
    String userId,
    String content,
    int replyCount,
    String status
) {
}