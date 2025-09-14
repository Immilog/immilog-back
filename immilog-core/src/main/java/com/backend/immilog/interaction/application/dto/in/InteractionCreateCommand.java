package com.backend.immilog.interaction.application.dto.in;

import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.shared.enums.ContentType;
import lombok.Builder;

@Builder
public record InteractionCreateCommand(
        String userId,
        String postId,
        ContentType contentType,
        InteractionType interactionType
) {
}