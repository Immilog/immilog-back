package com.backend.immilog.interaction.domain.model;

public enum InteractionStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }
}