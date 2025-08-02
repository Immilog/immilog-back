package com.backend.immilog.interaction.application.result;

import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.shared.enums.ContentType;

import java.time.LocalDateTime;

public record InteractionResult(
        String id,
        String userId,
        String postId,
        ContentType contentType,
        InteractionType interactionType,
        LocalDateTime createdAt
) {
    public static InteractionResult from(InteractionUser interactionUser) {
        return new InteractionResult(
                interactionUser.id(),
                interactionUser.userId(),
                interactionUser.postId(),
                interactionUser.contentType(),
                interactionUser.interactionType(),
                interactionUser.createdAt()
        );
    }
}