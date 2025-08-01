package com.backend.immilog.comment.domain.event;

import com.backend.immilog.shared.domain.event.DomainEvent;

import java.time.LocalDateTime;

public class CommentCreatedEvent implements DomainEvent {
    private final String commentId;
    private final String postId;
    private final String userId;
    private final LocalDateTime occurredAt;

    public CommentCreatedEvent(String commentId, String postId, String userId) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.occurredAt = LocalDateTime.now();
    }

    public String getCommentId() {
        return commentId;
    }

    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public LocalDateTime occurredAt() {
        return occurredAt;
    }
}