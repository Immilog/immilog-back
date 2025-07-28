package com.backend.immilog.report.domain.model;

public record ReportId(Long value) {
    public static ReportId of(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ReportId value must be positive");
        }
        return new ReportId(value);
    }
}