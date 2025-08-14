package com.backend.immilog.report.domain.enums;

public enum ReportTargetType {
    USER("사용자"),
    POST("게시글"),
    COMMENT("댓글"),
    NOTICE("공지사항"),
    JOB_BOARD("구인구직");

    private final String description;

    ReportTargetType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
