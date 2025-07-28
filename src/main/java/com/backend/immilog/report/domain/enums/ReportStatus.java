package com.backend.immilog.report.domain.enums;

/**
 * 신고 처리 상태를 나타내는 enum
 */
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
