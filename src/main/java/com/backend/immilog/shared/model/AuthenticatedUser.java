package com.backend.immilog.shared.model;

public record AuthenticatedUser(
        String userId,
        String email,
        String nickname
) {
    public static AuthenticatedUser of(String userId, String email, String nickname) {
        return new AuthenticatedUser(userId, email, nickname);
    }
}