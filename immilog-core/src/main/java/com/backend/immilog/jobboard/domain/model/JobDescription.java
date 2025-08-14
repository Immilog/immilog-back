package com.backend.immilog.jobboard.domain.model;

public record JobDescription(String value) {
    public JobDescription {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Job description cannot be null or empty");
        }
        if (value.length() > 5000) {
            throw new IllegalArgumentException("Job description cannot exceed 5000 characters");
        }
    }

    public static JobDescription of(String value) {
        return new JobDescription(value.trim());
    }

    @Override
    public String toString() {
        return value;
    }
}