package com.backend.immilog.user.domain.model.user;

import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;

public record UserId(Long value) {
    public UserId {
        if (value == null || value <= 0) {
            throw new UserException(UserErrorCode.ENTITY_TO_DOMAIN_ERROR);
        }
    }

    public static UserId of(Long value) {
        return new UserId(value);
    }

    public boolean equals(UserId other) {
        return other != null && this.value.equals(other.value);
    }
}