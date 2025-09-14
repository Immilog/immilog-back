package com.backend.immilog.interaction.presentation.payload;

import com.backend.immilog.interaction.application.dto.in.InteractionCreateCommand;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.shared.enums.ContentType;

public record InteractionCreateRequest(
        String postId,
        ContentType contentType,
        InteractionType interactionType
) {
    public InteractionCreateCommand toCommand(String userId) {
        return InteractionCreateCommand.builder()
                .userId(userId)
                .postId(postId)
                .contentType(contentType)
                .interactionType(interactionType)
                .build();
    }
}