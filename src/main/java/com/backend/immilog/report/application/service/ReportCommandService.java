package com.backend.immilog.report.application.service;

import com.backend.immilog.report.domain.model.Report;
import com.backend.immilog.report.domain.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportCommandService {
    private final ReportRepository reportRepository;

    public ReportCommandService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Transactional
    public Report save(Report report) {
        return reportRepository.save(report);
    }
    
    @Transactional
    public void delete(Report report) {
        reportRepository.delete(report);
    }
    
    @Transactional
    public void deleteById(Long reportId) {
        reportRepository.deleteById(reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"))
            .getId());
    }
}
