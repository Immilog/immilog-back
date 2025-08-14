package com.backend.immilog.comment.domain.event;

import com.backend.immilog.shared.domain.event.DomainEvent;

import java.time.LocalDateTime;

public class CommentCreatedEvent implements DomainEvent {
    private String commentId;
    private String postId;
    private String userId;
    private LocalDateTime occurredAt;

    public CommentCreatedEvent() {
        this.occurredAt = LocalDateTime.now();
    }

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