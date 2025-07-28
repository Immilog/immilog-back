package com.backend.immilog.user.domain.service;

import com.backend.immilog.user.domain.model.report.Report;
import com.backend.immilog.user.domain.model.report.ReportReason;
import com.backend.immilog.user.domain.model.user.UserId;
import com.backend.immilog.user.domain.repositories.ReportRepository;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.springframework.stereotype.Service;

@Service
public class ReportDomainService {

    private final ReportRepository reportRepository;

    public ReportDomainService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public Report createReport(
            Long reportedUserId,
            Long reporterUserId,
            String description,
            ReportReason reason
    ) {
        this.validateDuplicateReport(reportedUserId, reporterUserId);
        return Report.create(reportedUserId, reporterUserId, description, reason);
    }

    public long getReportCountByUserId(UserId userId) {
        return reportRepository.countByReportedUserId(userId);
    }

    private void validateDuplicateReport(
            Long reportedUserId,
            Long reporterUserId
    ) {
        if (reportRepository.existsByReportedUserIdAndReporterUserId(reportedUserId, reporterUserId)) {
            throw new UserException(UserErrorCode.ALREADY_REPORTED);
        }
    }
}