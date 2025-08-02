package com.backend.immilog.interaction.domain.model;

import com.backend.immilog.shared.enums.ContentType;

import java.time.LocalDateTime;

public record InteractionUser(
        String id,
        String userId,
        String postId,
        ContentType contentType,
        InteractionType interactionType,
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
                LocalDateTime.now()
        );
    }
}