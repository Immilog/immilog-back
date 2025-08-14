package com.backend.immilog.jobboard.domain.model;

public record JobRequirements(String value) {
    public JobRequirements {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Job requirements cannot be null or empty");
        }
        if (value.length() > 3000) {
            throw new IllegalArgumentException("Job requirements cannot exceed 3000 characters");
        }
    }

    public static JobRequirements of(String value) {
        return new JobRequirements(value.trim());
    }

    @Override
    public String toString() {
        return value;
    }
}