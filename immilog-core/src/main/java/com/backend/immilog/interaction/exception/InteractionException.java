package com.backend.immilog.interaction.exception;

public class InteractionException extends RuntimeException {
    private final InteractionErrorCode errorCode;

    public InteractionException(InteractionErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public InteractionErrorCode getErrorCode() {
        return errorCode;
    }
}