package com.backend.immilog.interaction.application.command;

import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.post.domain.model.post.PostType;

public record InteractionCreateCommand(
        String userId,
        String postId,
        PostType postType,
        InteractionType interactionType
) {
}