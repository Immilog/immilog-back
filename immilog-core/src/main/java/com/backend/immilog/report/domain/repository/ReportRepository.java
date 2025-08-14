package com.backend.immilog.report.domain.repository;

import com.backend.immilog.report.domain.enums.ReportStatus;
import com.backend.immilog.report.domain.model.Report;
import com.backend.immilog.report.domain.model.ReportId;
import com.backend.immilog.report.domain.model.ReportTarget;

import java.util.List;
import java.util.Optional;

public interface ReportRepository {

    Report save(Report report);

    Optional<Report> findById(ReportId reportId);

    Optional<Report> findById(String reportId);

    void delete(Report report);

    void deleteById(ReportId reportId);

    List<Report> findByTarget(ReportTarget target);

    List<Report> findByReporterId(String reporterId);

    List<Report> findByStatus(ReportStatus status);

    boolean existsByTargetAndReporterId(
            ReportTarget target,
            String reporterId
    );

    long countByTarget(ReportTarget target);

    long countByReporterId(String reporterId);

    List<Report> findPendingReports();

    List<Report> findReportsUnderReview();
}
