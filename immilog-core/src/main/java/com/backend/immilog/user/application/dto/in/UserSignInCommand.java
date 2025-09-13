package com.backend.immilog.user.application.dto.in;

public record UserSignInCommand(
        String email,
        String password,
        Double latitude,
        Double longitude
) {
}
