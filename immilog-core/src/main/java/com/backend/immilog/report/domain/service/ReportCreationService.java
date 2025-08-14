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

    public Report createReport(
            ReportTargetType targetType,
            String targetId,
            String reporterId,
            ReportReason reason,
            String customDescription
    ) {
        ReportTarget target = ReportTarget.of(targetType, targetId);
        validateBusinessRules(target, reporterId, reason);
        return Report.create(target, reporterId, reason, customDescription);
    }

    public Report createUserReport(
            String targetUserId,
            String reporterId,
            ReportReason reason,
            String customDescription
    ) {
        return createReport(ReportTargetType.USER, targetUserId, reporterId, reason, customDescription);
    }

    public Report createPostReport(
            String postId,
            String reporterId,
            ReportReason reason,
            String customDescription
    ) {
        return createReport(ReportTargetType.POST, postId, reporterId, reason, customDescription);
    }

    public Report createCommentReport(
            String commentId,
            String reporterId,
            ReportReason reason,
            String customDescription
    ) {
        return createReport(ReportTargetType.COMMENT, commentId, reporterId, reason, customDescription);
    }

    private void validateBusinessRules(
            ReportTarget target,
            String reporterId,
            ReportReason reason
    ) {
        if (target.type() == ReportTargetType.USER && target.targetId().equals(reporterId)) {
            throw new ReportException(ReportErrorCode.CANNOT_REPORT_YOURSELF);
        }

        if (reason == null) {
            throw new ReportException(ReportErrorCode.INVALID_REPORT_REASON);
        }
    }

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