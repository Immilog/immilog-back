package com.backend.immilog.interaction.exception;

public enum InteractionErrorCode {
    INTERACTION_NOT_FOUND("Interaction not found"),
    INTERACTION_ALREADY_EXISTS("Interaction already exists"),
    INTERACTION_DELETE_FAILED("Interaction delete failed"),
    INTERACTION_CREATE_FAILED("Interaction create failed");

    private final String message;

    InteractionErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}