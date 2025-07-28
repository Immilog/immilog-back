package com.backend.immilog.user.domain.model;

import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;

public record Auth(
        String email,
        String password
) {
    public Auth {
        validateEmail(email);
        validatePassword(password);
    }

    public static Auth of(
            String email,
            String password
    ) {
        return new Auth(email, password);
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new UserException(UserErrorCode.INVALID_EMAIL_FORMAT);
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new UserException(UserErrorCode.INVALID_EMAIL_FORMAT);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD_FORMAT);
        }
    }
}