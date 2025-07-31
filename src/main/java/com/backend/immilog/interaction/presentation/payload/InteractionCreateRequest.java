package com.backend.immilog.interaction.presentation.payload;

import com.backend.immilog.interaction.application.command.InteractionCreateCommand;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.post.domain.model.post.PostType;

public record InteractionCreateRequest(
        String postId,
        PostType postType,
        InteractionType interactionType
) {
    public InteractionCreateCommand toCommand(String userId) {
        return new InteractionCreateCommand(
                userId,
                postId,
                postType,
                interactionType
        );
    }
}