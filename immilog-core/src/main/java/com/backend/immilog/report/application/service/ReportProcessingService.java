package com.backend.immilog.report.application.service;

import com.backend.immilog.report.domain.model.Report;
import com.backend.immilog.report.domain.model.ReportId;
import com.backend.immilog.report.domain.repository.ReportRepository;
import com.backend.immilog.report.domain.service.ReportCreationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReportProcessingService {

    private final ReportRepository reportRepository;
    private final ReportCreationService reportCreationService;

    public ReportProcessingService(
            ReportRepository reportRepository,
            ReportCreationService reportCreationService
    ) {
        this.reportRepository = reportRepository;
        this.reportCreationService = reportCreationService;
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
}