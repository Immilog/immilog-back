package com.backend.immilog.shared.domain.model;

public record InteractionData(
    String id,
    String postId,
    String userId,
    String interactionStatus,
    String interactionType,
    String contentType
) {
}