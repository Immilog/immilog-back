package com.backend.immilog.report.infrastructure.jpa;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.report.domain.enums.ReportStatus;
import com.backend.immilog.report.domain.enums.ReportTargetType;
import com.backend.immilog.report.domain.model.Report;
import com.backend.immilog.report.domain.model.ReportDescription;
import com.backend.immilog.report.domain.model.ReportId;
import com.backend.immilog.report.domain.model.ReportTarget;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@DynamicUpdate
@Entity
@Table(name = "report")
public class ReportJpaEntity {

    @Id
    @Column(name = "report_id")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type")
    private ReportTargetType targetType;

    @Column(name = "target_id")
    private String targetId;

    @Column(name = "reporter_id")
    private String reporterId;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    private ReportReason reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReportStatus status;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = NanoIdUtils.randomNanoId();
        }
    }

    protected ReportJpaEntity() {}

    private ReportJpaEntity(
            String id,
            ReportTargetType targetType,
            String targetId,
            String reporterId,
            String description,
            ReportReason reason,
            ReportStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime resolvedAt
    ) {
        this.id = id;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reporterId = reporterId;
        this.description = description;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.resolvedAt = resolvedAt;
    }

    public static ReportJpaEntity from(Report report) {
        return new ReportJpaEntity(
                report.getIdValue(),
                report.getTargetType(),
                report.getTargetId(),
                report.getReporterId(),
                report.getDescriptionValue(),
                report.getReason(),
                report.getStatus(),
                report.getCreatedAt(),
                report.getUpdatedAt(),
                report.getResolvedAt()
        );
    }

    public Report toDomain() {
        ReportId reportId = this.id != null ? ReportId.of(this.id) : null;
        ReportTarget target = ReportTarget.of(this.targetType, this.targetId);
        ReportDescription description = ReportDescription.of(this.description);

        return Report.restore(
                reportId,
                target,
                this.reporterId,
                description,
                this.reason,
                this.status,
                this.createdAt,
                this.updatedAt,
                this.resolvedAt
        );
    }

    public String getId() {return id;}

    public ReportTargetType getTargetType() {return targetType;}

    public String getTargetId() {return targetId;}

    public String getReporterId() {return reporterId;}

    public String getDescription() {return description;}

    public ReportReason getReason() {return reason;}

    public ReportStatus getStatus() {return status;}

    public LocalDateTime getCreatedAt() {return createdAt;}

    public LocalDateTime getUpdatedAt() {return updatedAt;}

    public LocalDateTime getResolvedAt() {return resolvedAt;}
}