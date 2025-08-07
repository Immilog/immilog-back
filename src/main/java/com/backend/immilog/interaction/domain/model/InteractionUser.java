package com.backend.immilog.interaction.domain.model;

import com.backend.immilog.shared.enums.ContentType;

import java.time.LocalDateTime;

public record InteractionUser(
        String id,
        String userId,
        String postId,
        ContentType contentType,
        InteractionType interactionType,
        InteractionStatus interactionStatus,
        LocalDateTime createdAt
) {
    public static InteractionUser of(
            String userId,
            String postId,
            ContentType contentType,
            InteractionType interactionType
    ) {
        return new InteractionUser(
                null,
                userId,
                postId,
                contentType,
                interactionType,
                InteractionStatus.ACTIVE,
                LocalDateTime.now()
        );
    }

    public InteractionUser toggleStatus() {
        return new InteractionUser(
                this.id,
                this.userId,
                this.postId,
                this.contentType,
                this.interactionType,
                this.interactionStatus.isActive() ? InteractionStatus.INACTIVE : InteractionStatus.ACTIVE,
                LocalDateTime.now()
        );
    }
}