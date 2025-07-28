package com.backend.immilog.user.application.services;

import com.backend.immilog.user.domain.model.report.Report;
import com.backend.immilog.user.domain.model.report.ReportReason;
import com.backend.immilog.user.domain.model.user.UserId;
import com.backend.immilog.user.domain.repositories.ReportRepository;
import com.backend.immilog.user.domain.service.ReportCreationService;
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

    public void reportUser(
            Long reportedUserId,
            Long reporterUserId,
            String description,
            ReportReason reason
    ) {
        var report = reportCreationService.createReport(reportedUserId, reporterUserId, description, reason);
        reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public long getReportCount(UserId userId) {
        return reportCreationService.getReportCountByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Report> getReportsByUserId(UserId userId) {
        return reportRepository.findByReportedUserId(userId);
    }
}