package com.backend.immilog.jobboard.domain.model;

import java.time.LocalDate;

public record ApplicationDeadline(LocalDate value) {
    public ApplicationDeadline {
        if (value == null) {
            throw new IllegalArgumentException("Application deadline cannot be null");
        }
        if (value.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Application deadline cannot be in the past");
        }
    }

    public static ApplicationDeadline of(LocalDate value) {
        return new ApplicationDeadline(value);
    }

    public boolean isExpired() {
        return value.isBefore(LocalDate.now());
    }

    public boolean isExpiredBy(LocalDate date) {
        return value.isBefore(date);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}