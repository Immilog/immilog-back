package com.backend.immilog.user.application.services;

import com.backend.immilog.user.domain.model.report.Report;
import com.backend.immilog.user.domain.model.report.ReportReason;
import com.backend.immilog.user.domain.model.user.UserId;
import com.backend.immilog.user.domain.repositories.ReportRepository;
import com.backend.immilog.user.domain.service.ReportDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportDomainService reportDomainService;

    public ReportService(
            ReportRepository reportRepository,
            ReportDomainService reportDomainService
    ) {
        this.reportRepository = reportRepository;
        this.reportDomainService = reportDomainService;
    }

    public void reportUser(
            Long reportedUserId,
            Long reporterUserId,
            String description,
            ReportReason reason
    ) {
        var report = reportDomainService.createReport(reportedUserId, reporterUserId, description, reason);
        reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public long getReportCount(UserId userId) {
        return reportDomainService.getReportCountByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Report> getReportsByUserId(UserId userId) {
        return reportRepository.findByReportedUserId(userId);
    }
}