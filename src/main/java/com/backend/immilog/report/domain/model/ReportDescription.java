package com.backend.immilog.report.domain.model;

import com.backend.immilog.report.domain.enums.ReportReason;

/**
 * 신고 설명을 나타내는 Value Object
 */
public record ReportDescription(String value) {
    private static final int MAX_LENGTH = 1000;
    
    public static ReportDescription of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Report description cannot be null or empty");
        }
        
        String trimmed = value.trim();
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Report description cannot exceed " + MAX_LENGTH + " characters");
        }
        
        return new ReportDescription(trimmed);
    }
    
    public static ReportDescription fromReason(ReportReason reason) {
        return new ReportDescription(reason.getDescription());
    }
}