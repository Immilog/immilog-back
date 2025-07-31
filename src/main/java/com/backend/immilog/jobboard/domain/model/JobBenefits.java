package com.backend.immilog.jobboard.domain.model;

public record JobBenefits(String value) {
    public JobBenefits {
        if (value != null && value.length() > 2000) {
            throw new IllegalArgumentException("Job benefits cannot exceed 2000 characters");
        }
    }

    public static JobBenefits of(String value) {
        return new JobBenefits(value != null ? value.trim() : null);
    }

    public static JobBenefits empty() {
        return new JobBenefits(null);
    }

    public boolean isEmpty() {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public String toString() {
        return value != null ? value : "";
    }
}