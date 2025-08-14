package com.backend.immilog.interaction.application.command;

import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.shared.enums.ContentType;

public record InteractionCreateCommand(
        String userId,
        String postId,
        ContentType contentType,
        InteractionType interactionType
) {
}