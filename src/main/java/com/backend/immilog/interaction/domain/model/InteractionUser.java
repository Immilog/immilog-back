package com.backend.immilog.interaction.domain.model;

import com.backend.immilog.post.domain.model.post.PostType;

import java.time.LocalDateTime;

public record InteractionUser(
        String id,
        String userId,
        String postId,
        PostType postType,
        InteractionType interactionType,
        LocalDateTime createdAt
) {
    public static InteractionUser of(
            String userId,
            String postId,
            PostType postType,
            InteractionType interactionType
    ) {
        return new InteractionUser(
                null,
                userId,
                postId,
                postType,
                interactionType,
                LocalDateTime.now()
        );
    }
}