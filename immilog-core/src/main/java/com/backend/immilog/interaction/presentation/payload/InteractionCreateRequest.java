package com.backend.immilog.interaction.presentation.payload;

import com.backend.immilog.interaction.application.command.InteractionCreateCommand;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.shared.enums.ContentType;

public record InteractionCreateRequest(
        String postId,
        ContentType contentType,
        InteractionType interactionType
) {
    public InteractionCreateCommand toCommand(String userId) {
        return new InteractionCreateCommand(
                userId,
                postId,
                contentType,
                interactionType
        );
    }
}