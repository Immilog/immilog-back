package com.backend.immilog.report.domain.model;

import com.backend.immilog.report.domain.enums.ReportTargetType;

/**
 * 신고 대상을 나타내는 Value Object
 * 다양한 타입의 엔티티에 대한 신고를 지원
 */
public record ReportTarget(
    ReportTargetType type,
    Long targetId
) {
    public static ReportTarget of(ReportTargetType type, Long targetId) {
        if (type == null) {
            throw new IllegalArgumentException("ReportTargetType cannot be null");
        }
        if (targetId == null || targetId <= 0) {
            throw new IllegalArgumentException("Target ID must be positive");
        }
        return new ReportTarget(type, targetId);
    }
    
    public static ReportTarget user(Long userId) {
        return of(ReportTargetType.USER, userId);
    }
    
    public static ReportTarget post(Long postId) {
        return of(ReportTargetType.POST, postId);
    }
    
    public static ReportTarget comment(Long commentId) {
        return of(ReportTargetType.COMMENT, commentId);
    }
}