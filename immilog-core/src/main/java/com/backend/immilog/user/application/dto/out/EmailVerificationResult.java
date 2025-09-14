package com.backend.immilog.user.application.dto.out;

public record EmailVerificationResult(
        String message,
        boolean isLoginAvailable
) {
}
