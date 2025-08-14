package com.backend.immilog.report.application.service;

import com.backend.immilog.report.domain.enums.ReportStatus;
import com.backend.immilog.report.domain.enums.ReportTargetType;
import com.backend.immilog.report.domain.model.Report;
import com.backend.immilog.report.domain.model.ReportId;
import com.backend.immilog.report.domain.model.ReportTarget;
import com.backend.immilog.report.domain.repository.ReportRepository;
import com.backend.immilog.report.exception.ReportErrorCode;
import com.backend.immilog.report.exception.ReportException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReportQueryService {
    private final ReportRepository reportRepository;

    public ReportQueryService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Report> findById(String reportId) {
        return reportRepository.findById(reportId);
    }

    @Transactional(readOnly = true)
    public Report getById(ReportId reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportException(ReportErrorCode.REPORT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public boolean existsByTargetAndReporter(
            ReportTargetType targetType,
            String targetId,
            String reporterId
    ) {
        ReportTarget target = ReportTarget.of(targetType, targetId);
        return reportRepository.existsByTargetAndReporterId(target, reporterId);
    }

    @Transactional(readOnly = true)
    public List<Report> findByTarget(
            ReportTargetType targetType,
            String targetId
    ) {
        ReportTarget target = ReportTarget.of(targetType, targetId);
        return reportRepository.findByTarget(target);
    }

    @Transactional(readOnly = true)
    public List<Report> findByReporter(String reporterId) {
        return reportRepository.findByReporterId(reporterId);
    }

    @Transactional(readOnly = true)
    public List<Report> findByStatus(ReportStatus status) {
        return reportRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public long countByTarget(
            ReportTargetType targetType,
            String targetId
    ) {
        ReportTarget target = ReportTarget.of(targetType, targetId);
        return reportRepository.countByTarget(target);
    }

    @Transactional(readOnly = true)
    public long countByReporter(String reporterId) {
        return reportRepository.countByReporterId(reporterId);
    }

    @Transactional(readOnly = true)
    public List<Report> findPendingReports() {
        return reportRepository.findPendingReports();
    }

    @Transactional(readOnly = true)
    public List<Report> findReportsUnderReview() {
        return reportRepository.findReportsUnderReview();
    }
}
