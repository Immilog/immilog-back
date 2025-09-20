package com.backend.immilog.report.application.service;

import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.report.domain.model.Report;
import com.backend.immilog.report.domain.model.ReportId;
import com.backend.immilog.report.domain.model.ReportTarget;
import com.backend.immilog.report.domain.repository.ReportRepository;
import com.backend.immilog.report.domain.service.ReportCreationService;
import com.backend.immilog.report.exception.ReportErrorCode;
import com.backend.immilog.report.exception.ReportException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportCreationService reportCreationService;

    public ReportService(
            ReportRepository reportRepository,
            ReportCreationService reportCreationService
    ) {
        this.reportRepository = reportRepository;
        this.reportCreationService = reportCreationService;
    }

    public ReportId report(
            String targetUserId,
            String reporterId,
            ReportReason reason,
            String customDescription
    ) {
        if (reportRepository.existsByTargetAndReporterId(ReportTarget.user(targetUserId), reporterId)) {
            throw new ReportException(ReportErrorCode.ALREADY_REPORTED);
        }
        var report = reportCreationService.createUserReport(targetUserId, reporterId, reason, customDescription);
        return reportRepository.save(report).getId();
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
        return reportRepository.save(report).getId();
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
        return reportRepository.save(report).getId();
    }

    public void processReport(ReportId reportId) {
        var report = reportRepository.getById(reportId);
        var processedReport = reportCreationService.processReport(report);
        reportRepository.save(processedReport);
    }

    public void resolveReport(ReportId reportId) {
        var report = reportRepository.getById(reportId);
        var resolvedReport = reportCreationService.resolveReport(report);
        reportRepository.save(resolvedReport);
    }

    public void rejectReport(ReportId reportId) {
        var report = reportRepository.getById(reportId);
        var rejectedReport = reportCreationService.rejectReport(report);
        reportRepository.save(rejectedReport);
    }

    @Transactional(readOnly = true)
    public long getReportCountByUser(String userId) {
        return reportRepository.countByTarget(ReportTarget.user(userId));
    }

    @Transactional(readOnly = true)
    public long getReportCountByReporter(String reporterId) {
        return reportRepository.countByReporterId(reporterId);
    }

    @Transactional(readOnly = true)
    public List<Report> getReportsByUser(String userId) {
        return reportRepository.findByTarget(ReportTarget.user(userId));
    }

    @Transactional(readOnly = true)
    public List<Report> getReportsByReporter(String reporterId) {
        return reportRepository.findByReporterId(reporterId);
    }

    @Transactional(readOnly = true)
    public List<Report> getPendingReports() {
        return reportRepository.findPendingReports();
    }

    @Transactional(readOnly = true)
    public List<Report> getReportsUnderReview() {
        return reportRepository.findReportsUnderReview();
    }

    @Transactional(readOnly = true)
    public Report getReportById(ReportId reportId) {
        return reportRepository.getById(reportId);
    }
}