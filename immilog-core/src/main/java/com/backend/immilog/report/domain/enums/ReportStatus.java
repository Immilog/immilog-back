package com.backend.immilog.report.domain.enums;

public enum ReportStatus {
    PENDING("처리 대기"),
    UNDER_REVIEW("검토 중"),
    RESOLVED("처리 완료"),
    REJECTED("반려");

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
