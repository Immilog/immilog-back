package com.backend.immilog.shared.domain.model;

import lombok.Builder;

@Builder
public record InteractionData(
    String id,
    String postId,
    String userId,
    String interactionStatus,
    String interactionType,
    String contentType
) {
}