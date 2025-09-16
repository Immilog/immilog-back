package com.backend.immilog.report.domain.model;

import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.report.domain.enums.ReportStatus;
import com.backend.immilog.report.domain.enums.ReportTargetType;
import com.backend.immilog.report.exception.ReportErrorCode;
import com.backend.immilog.report.exception.ReportException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Report {
    private final ReportId id;
    private final ReportTarget target;
    private final String reporterId;
    private ReportDescription description;
    private final ReportReason reason;
    private ReportStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    @Builder(access = lombok.AccessLevel.PRIVATE)
    private Report(
            ReportId id,
            ReportTarget target,
            String reporterId,
            ReportDescription description,
            ReportReason reason,
            ReportStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime resolvedAt
    ) {
        this.id = id;
        this.target = target;
        this.reporterId = reporterId;
        this.description = description;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.resolvedAt = resolvedAt;
    }

    public static Report create(
            ReportTarget target,
            String reporterId,
            ReportReason reason,
            String customDescription
    ) {
        validateCreationInputs(target, reporterId, reason);

        ReportDescription description = reason == ReportReason.OTHER
                ? ReportDescription.of(customDescription)
                : ReportDescription.fromReason(reason);

        LocalDateTime now = LocalDateTime.now();
        return Report.builder()
                .target(target)
                .reporterId(reporterId)
                .description(description)
                .reason(reason)
                .status(ReportStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static Report restore(
            ReportId id,
            ReportTarget target,
            String reporterId,
            ReportDescription description,
            ReportReason reason,
            ReportStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime resolvedAt
    ) {
        return Report.builder()
                .id(id)
                .target(target)
                .reporterId(reporterId)
                .description(description)
                .reason(reason)
                .status(status)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .resolvedAt(resolvedAt)
                .build();
    }

    public Report updateDescription(ReportDescription newDescription) {
        if (isPending()) {
            this.description = newDescription;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new ReportException(ReportErrorCode.CANNOT_UPDATE_PROCESSED_REPORT);
        }
        return this;
    }

    public Report startReview() {
        if (!isPending()) {
            throw new ReportException(ReportErrorCode.INVALID_STATUS_TRANSITION);
        }
        this.status = ReportStatus.UNDER_REVIEW;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public Report resolve() {
        if (!isUnderReview()) {
            throw new ReportException(ReportErrorCode.INVALID_STATUS_TRANSITION);
        }
        this.status = ReportStatus.RESOLVED;
        this.updatedAt = LocalDateTime.now();
        this.resolvedAt = LocalDateTime.now();
        return this;
    }

    public Report reject() {
        if (!isUnderReview()) {
            throw new ReportException(ReportErrorCode.INVALID_STATUS_TRANSITION);
        }
        this.status = ReportStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
        this.resolvedAt = LocalDateTime.now();
        return this;
    }

    public boolean isPending() {
        return status == ReportStatus.PENDING;
    }

    public boolean isUnderReview() {
        return status == ReportStatus.UNDER_REVIEW;
    }

    public boolean isResolved() {
        return status == ReportStatus.RESOLVED;
    }

    public boolean isRejected() {
        return status == ReportStatus.REJECTED;
    }

    public boolean isReporter(String userId) {
        return reporterId.equals(userId);
    }

    public boolean isTargetUser(String userId) {
        return target.type() == ReportTargetType.USER && target.targetId().equals(userId);
    }

    private static void validateCreationInputs(
            ReportTarget target,
            String reporterId,
            ReportReason reason
    ) {
        if (target == null) {
            throw new ReportException(ReportErrorCode.INVALID_REPORT_TARGET);
        }
        if (reporterId == null || reporterId.isBlank()) {
            throw new ReportException(ReportErrorCode.INVALID_REPORTER);
        }
        if (reason == null) {
            throw new ReportException(ReportErrorCode.INVALID_REPORT_REASON);
        }

        if (target.type() == ReportTargetType.USER && target.targetId().equals(reporterId)) {
            throw new ReportException(ReportErrorCode.CANNOT_REPORT_YOURSELF);
        }
    }

    public String getIdValue() {return id != null ? id.value() : null;}

    public String getDescriptionValue() {return description.value();}

    public ReportTargetType getTargetType() {return target.type();}

    public String getTargetId() {return target.targetId();}
}