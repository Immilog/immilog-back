package com.backend.immilog.user.domain.model;

import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;

public record UserId(String value) {
    public UserId {
        if (value == null || value.isBlank()) {
            throw new UserException(UserErrorCode.ENTITY_TO_DOMAIN_ERROR);
        }
    }

    public static UserId of(String value) {
        return new UserId(value);
    }

    public boolean equals(UserId other) {
        return other != null && this.value.equals(other.value);
    }
}