package com.backend.immilog.shared.exception;

public record ErrorResponse(
        ErrorCode errorCode,
        String message
) {
}
