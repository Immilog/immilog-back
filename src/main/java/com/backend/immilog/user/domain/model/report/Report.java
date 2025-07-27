package com.backend.immilog.user.domain.model.report;

import com.backend.immilog.user.domain.model.user.UserId;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;

import java.time.LocalDateTime;

public class Report {
    private final ReportId reportId;
    private final Long reportedUserId;
    private final Long reporterUserId;
    private final String description;
    private final ReportReason reason;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Report(
            ReportId reportId,
            Long reportedUserId,
            Long reporterUserId,
            String description,
            ReportReason reason,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.reportId = reportId;
        this.reportedUserId = reportedUserId;
        this.reporterUserId = reporterUserId;
        this.description = description;
        this.reason = reason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Report create(
            Long reportedUserId,
            Long reporterUserId,
            String description,
            ReportReason reason
    ) {
        validateReportCreation(reportedUserId, reporterUserId, description, reason);

        return new Report(
                null,
                reportedUserId,
                reporterUserId,
                description,
                reason,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static Report restore(
            ReportId reportId,
            Long reportedUserId,
            Long reporterUserId,
            String description,
            ReportReason reason,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Report(
                reportId,
                reportedUserId,
                reporterUserId,
                description,
                reason,
                createdAt,
                updatedAt
        );
    }

    private static void validateReportCreation(
            Long reportedUserId,
            Long reporterUserId,
            String description,
            ReportReason reason
    ) {
        if (reportedUserId == null || reporterUserId == null) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }

        if (reportedUserId.equals(reporterUserId)) {
            throw new UserException(UserErrorCode.CANNOT_REPORT_MYSELF);
        }

        if (description == null || description.trim().isEmpty()) {
            throw new UserException(UserErrorCode.ENTITY_TO_DOMAIN_ERROR);
        }

        if (reason == null) {
            throw new UserException(UserErrorCode.ENTITY_TO_DOMAIN_ERROR);
        }
    }

    public boolean isReportedUser(UserId userId) {
        return this.reportedUserId.equals(userId);
    }

    public boolean isReporter(UserId userId) {
        return this.reporterUserId.equals(userId);
    }

    public ReportId getReportId() {return reportId;}

    public Long getReportedUserId() {return reportedUserId;}

    public Long getReporterUserId() {return reporterUserId;}

    public String getDescription() {return description;}

    public ReportReason getReason() {return reason;}

    public LocalDateTime getCreatedAt() {return createdAt;}

    public LocalDateTime getUpdatedAt() {return updatedAt;}
}