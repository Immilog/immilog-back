package com.backend.immilog.report.domain.service;

import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.report.domain.enums.ReportTargetType;
import com.backend.immilog.report.domain.model.Report;
import com.backend.immilog.report.domain.model.ReportTarget;
import com.backend.immilog.report.exception.ReportErrorCode;
import com.backend.immilog.report.exception.ReportException;
import org.springframework.stereotype.Service;

@Service
public class ReportCreationService {

    public ReportCreationService() {
        // Domain Service는 순수 비즈니스 로직만 담당
    }

    public Report createReport(
            ReportTargetType targetType,
            Long targetId,
            Long reporterId,
            ReportReason reason,
            String customDescription
    ) {
        ReportTarget target = ReportTarget.of(targetType, targetId);
        validateBusinessRules(target, reporterId, reason);
        return Report.create(target, reporterId, reason, customDescription);
    }
    
    public Report createUserReport(
            Long targetUserId,
            Long reporterId,
            ReportReason reason,
            String customDescription
    ) {
        return createReport(ReportTargetType.USER, targetUserId, reporterId, reason, customDescription);
    }
    
    public Report createPostReport(
            Long postId,
            Long reporterId,
            ReportReason reason,
            String customDescription
    ) {
        return createReport(ReportTargetType.POST, postId, reporterId, reason, customDescription);
    }
    
    public Report createCommentReport(
            Long commentId,
            Long reporterId,
            ReportReason reason,
            String customDescription
    ) {
        return createReport(ReportTargetType.COMMENT, commentId, reporterId, reason, customDescription);
    }

    // 통계 관련 기능은 Application Service나 Query Service에서 처리

    /**
     * 신고 생성 시 비즈니스 규칙 검증
     * - 중복 신고 여부는 Application Service에서 체크
     * - 여기서는 도메인 규칙만 검증
     */
    private void validateBusinessRules(
            ReportTarget target,
            Long reporterId,
            ReportReason reason
    ) {
        // 자기 자신을 신고할 수 없음 (이미 Report.create에서 검증하지만 명시적으로)
        if (target.type() == ReportTargetType.USER && target.targetId().equals(reporterId)) {
            throw new ReportException(ReportErrorCode.CANNOT_REPORT_YOURSELF);
        }
        
        // 기타 도메인 비즈니스 규칙들
        if (reason == null) {
            throw new ReportException(ReportErrorCode.INVALID_REPORT_REASON);
        }
    }
    
    /**
     * 신고 처리 관련 비즈니스 로직
     */
    public Report processReport(Report report) {
        validateReportProcessing(report);
        return report.startReview();
    }
    
    public Report resolveReport(Report report) {
        validateReportResolution(report);
        return report.resolve();
    }
    
    public Report rejectReport(Report report) {
        validateReportResolution(report);
        return report.reject();
    }
    
    private void validateReportProcessing(Report report) {
        if (!report.isPending()) {
            throw new ReportException(ReportErrorCode.INVALID_STATUS_TRANSITION);
        }
    }
    
    private void validateReportResolution(Report report) {
        if (!report.isUnderReview()) {
            throw new ReportException(ReportErrorCode.INVALID_STATUS_TRANSITION);
        }
    }
}