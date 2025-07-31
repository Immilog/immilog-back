package com.backend.immilog.report.domain.model;

public record ReportId(String value) {
    public static ReportId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ReportId value must be not null or empty");
        }
        return new ReportId(value);
    }
}