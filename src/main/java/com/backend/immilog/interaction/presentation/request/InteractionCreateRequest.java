package com.backend.immilog.interaction.presentation.request;

import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.post.domain.model.post.PostType;

public record InteractionCreateRequest(
        String postId,
        PostType postType,
        InteractionType interactionType
) {
}