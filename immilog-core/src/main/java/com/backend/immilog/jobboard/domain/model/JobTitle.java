package com.backend.immilog.jobboard.domain.model;

public record JobTitle(String value) {
    public JobTitle {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Job title cannot be null or empty");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Job title cannot exceed 100 characters");
        }
    }

    public static JobTitle of(String value) {
        return new JobTitle(value.trim());
    }

    @Override
    public String toString() {
        return value;
    }
}