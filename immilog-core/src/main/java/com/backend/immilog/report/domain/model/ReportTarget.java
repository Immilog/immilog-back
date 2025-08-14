package com.backend.immilog.report.domain.model;

import com.backend.immilog.report.domain.enums.ReportTargetType;

public record ReportTarget(
        ReportTargetType type,
        String targetId
) {
    public static ReportTarget of(
            ReportTargetType type,
            String targetId
    ) {
        if (type == null) {
            throw new IllegalArgumentException("ReportTargetType cannot be null");
        }
        if (targetId == null || targetId.isBlank()) {
            throw new IllegalArgumentException("Target ID must be not null or blank");
        }
        return new ReportTarget(type, targetId);
    }

    public static ReportTarget user(String userId) {
        return of(ReportTargetType.USER, userId);
    }

    public static ReportTarget post(String postId) {
        return of(ReportTargetType.POST, postId);
    }

    public static ReportTarget comment(String commentId) {
        return of(ReportTargetType.COMMENT, commentId);
    }
}