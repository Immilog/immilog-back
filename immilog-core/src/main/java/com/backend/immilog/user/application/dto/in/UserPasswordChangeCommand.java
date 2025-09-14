package com.backend.immilog.user.application.dto.in;

public record UserPasswordChangeCommand(
        String existingPassword,
        String newPassword
) {
}