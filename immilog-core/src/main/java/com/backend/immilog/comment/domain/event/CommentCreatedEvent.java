package com.backend.immilog.comment.domain.event;

import com.backend.immilog.shared.domain.event.DomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;

public class CommentCreatedEvent implements DomainEvent {
    @Getter
    private String commentId;
    @Getter
    private String postId;
    @Getter
    private String userId;
    private final LocalDateTime occurredAt;

    public CommentCreatedEvent() {
        this.occurredAt = LocalDateTime.now();
    }

    public CommentCreatedEvent(
            String commentId,
            String postId,
            String userId
    ) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.occurredAt = LocalDateTime.now();
    }

    @Override
    public LocalDateTime occurredAt() {
        return occurredAt;
    }
}