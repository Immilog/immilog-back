package com.backend.immilog.report.application.service;

import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.report.domain.enums.ReportTargetType;
import com.backend.immilog.report.domain.model.ReportId;
import com.backend.immilog.report.domain.model.ReportTarget;
import com.backend.immilog.report.domain.repository.ReportRepository;
import com.backend.immilog.report.domain.service.ReportCreationService;
import com.backend.immilog.report.exception.ReportErrorCode;
import com.backend.immilog.report.exception.ReportException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReportCreationApplicationService {

    private final ReportRepository reportRepository;
    private final ReportCreationService reportCreationService;

    public ReportCreationApplicationService(
            ReportRepository reportRepository,
            ReportCreationService reportCreationService
    ) {
        this.reportRepository = reportRepository;
        this.reportCreationService = reportCreationService;
    }

    public ReportId reportUser(
            String targetUserId,
            String reporterId,
            ReportReason reason,
            String customDescription
    ) {
        if (reportRepository.existsByTargetAndReporterId(ReportTarget.user(targetUserId), reporterId)) {
            throw new ReportException(ReportErrorCode.ALREADY_REPORTED);
        }

        var report = reportCreationService.createUserReport(targetUserId, reporterId, reason, customDescription);
        var savedReport = reportRepository.save(report);
        return savedReport.getId();
    }

    public ReportId reportPost(
            String postId,
            String reporterId,
            ReportReason reason,
            String customDescription
    ) {
        if (reportRepository.existsByTargetAndReporterId(ReportTarget.post(postId), reporterId)) {
            throw new ReportException(ReportErrorCode.ALREADY_REPORTED);
        }

        var report = reportCreationService.createPostReport(postId, reporterId, reason, customDescription);
        var savedReport = reportRepository.save(report);
        return savedReport.getId();
    }

    public ReportId reportComment(
            String commentId,
            String reporterId,
            ReportReason reason,
            String customDescription
    ) {
        if (reportRepository.existsByTargetAndReporterId(ReportTarget.comment(commentId), reporterId)) {
            throw new ReportException(ReportErrorCode.ALREADY_REPORTED);
        }

        var report = reportCreationService.createCommentReport(commentId, reporterId, reason, customDescription);
        var savedReport = reportRepository.save(report);
        return savedReport.getId();
    }
}