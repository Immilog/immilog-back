package com.backend.immilog.jobboard.domain.model;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public record JobBoardId(String value) {
    public JobBoardId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("JobBoard ID cannot be null or empty");
        }
    }

    public static JobBoardId generate() {
        return new JobBoardId(NanoIdUtils.randomNanoId());
    }

    public static JobBoardId of(String value) {
        return new JobBoardId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}