package com.backend.immilog.report.application.service;

import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.report.domain.enums.ReportTargetType;
import com.backend.immilog.report.domain.model.Report;
import com.backend.immilog.report.domain.model.ReportId;
import com.backend.immilog.report.domain.model.ReportTarget;
import com.backend.immilog.report.domain.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReportService {

    private final ReportCreationApplicationService reportCreationApplicationService;
    private final ReportProcessingService reportProcessingService;
    private final ReportRepository reportRepository;

    public ReportService(
            ReportCreationApplicationService reportCreationApplicationService,
            ReportProcessingService reportProcessingService,
            ReportRepository reportRepository
    ) {
        this.reportCreationApplicationService = reportCreationApplicationService;
        this.reportProcessingService = reportProcessingService;
        this.reportRepository = reportRepository;
    }

    public ReportId report(
            String targetUserId,
            String reporterId,
            ReportReason reason,
            String customDescription
    ) {
        return reportCreationApplicationService.reportUser(targetUserId, reporterId, reason, customDescription);
    }

    public ReportId reportPost(
            String postId,
            String reporterId,
            ReportReason reason,
            String customDescription
    ) {
        return reportCreationApplicationService.reportPost(postId, reporterId, reason, customDescription);
    }

    public ReportId reportComment(
            String commentId,
            String reporterId,
            ReportReason reason,
            String customDescription
    ) {
        return reportCreationApplicationService.reportComment(commentId, reporterId, reason, customDescription);
    }

    public void processReport(ReportId reportId) {
        reportProcessingService.processReport(reportId);
    }

    public void resolveReport(ReportId reportId) {
        reportProcessingService.resolveReport(reportId);
    }

    public void rejectReport(ReportId reportId) {
        reportProcessingService.rejectReport(reportId);
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