package com.backend.immilog.user.domain.repositories;

import com.backend.immilog.user.domain.model.report.Report;
import com.backend.immilog.user.domain.model.report.ReportId;
import com.backend.immilog.user.domain.model.user.UserId;

import java.util.List;
import java.util.Optional;

public interface ReportRepository {

    Optional<Report> findById(ReportId reportId);

    Report save(Report report);

    boolean existsByReportedUserIdAndReporterUserId(
            Long reportedUserId,
            Long reporterUserId
    );

    long countByReportedUserId(UserId reportedUserId);

    List<Report> findByReportedUserId(UserId reportedUserId);

    List<Report> findByReporterUserId(UserId reporterUserId);
}
