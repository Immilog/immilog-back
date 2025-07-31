package com.backend.immilog.interaction.presentation.payload;

import com.backend.immilog.interaction.application.result.InteractionResult;

public record InteractionResponse(
        int status,
        String message,
        Object data
) {
    public static InteractionResponse success(InteractionResult data) {
        return new InteractionResponse(200, "success", data);
    }

    public static InteractionResponse success(String message) {
        return new InteractionResponse(200, message, null);
    }
}