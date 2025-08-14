package com.backend.immilog.interaction.domain.model;

public enum InteractionType {
    LIKE,
    BOOKMARK;

    public boolean isLike() {
        return this == LIKE;
    }
}