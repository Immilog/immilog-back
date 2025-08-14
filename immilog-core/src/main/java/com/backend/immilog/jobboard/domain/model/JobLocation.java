package com.backend.immilog.jobboard.domain.model;

public record JobLocation(String value) {
    public JobLocation {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Job location cannot be null or empty");
        }
        if (value.length() > 200) {
            throw new IllegalArgumentException("Job location cannot exceed 200 characters");
        }
    }

    public static JobLocation of(String value) {
        return new JobLocation(value.trim());
    }

    @Override
    public String toString() {
        return value;
    }
}