package com.backend.immilog.jobboard.domain.model;

import java.util.regex.Pattern;

public record ContactEmail(String value) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public ContactEmail {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact email cannot be null or empty");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    public static ContactEmail of(String value) {
        return new ContactEmail(value.trim().toLowerCase());
    }

    @Override
    public String toString() {
        return value;
    }
}