package com.backend.immilog.user.infrastructure.jpa;

import com.backend.immilog.user.domain.model.report.Report;
import com.backend.immilog.user.domain.model.report.ReportId;
import com.backend.immilog.user.domain.model.report.ReportReason;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@DynamicUpdate
@Entity
@Table(name = "report")
public class ReportJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long id;

    @Column(name = "reported_user_seq")
    private Long reportedUserSeq;

    @Column(name = "reporter_user_seq")
    private Long reporterUserSeq;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    private ReportReason reason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected ReportJpaEntity() {}

    private ReportJpaEntity(
            Long id,
            Long reportedUserSeq,
            Long reporterUserSeq,
            String description,
            ReportReason reason,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.reportedUserSeq = reportedUserSeq;
        this.reporterUserSeq = reporterUserSeq;
        this.description = description;
        this.reason = reason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ReportJpaEntity from(Report report) {
        return new ReportJpaEntity(
                report.getReportId() != null ? report.getReportId().value() : null,
                report.getReportedUserId(),
                report.getReporterUserId(),
                report.getDescription(),
                report.getReason(),
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }

    public Report toDomain() {
        validateRequiredFields();

        ReportId reportId = this.id != null ? ReportId.of(this.id) : null;

        if (reportId == null) {
            // 신규 생성 케이스
            return Report.create(
                    this.reportedUserSeq,
                    this.reporterUserSeq,
                    this.description,
                    this.reason
            );
        } else {
            return Report.restore(
                    reportId,
                    this.reportedUserSeq,
                    this.reporterUserSeq,
                    this.description,
                    this.reason,
                    this.createdAt,
                    this.updatedAt
            );
        }
    }

    private void validateRequiredFields() {
        if (this.reportedUserSeq == null || this.reporterUserSeq == null ||
                this.description == null || this.reason == null) {
            throw new UserException(UserErrorCode.ENTITY_TO_DOMAIN_ERROR);
        }
    }

    public Long getId() {return id;}

    public Long getReportedUserSeq() {return reportedUserSeq;}

    public Long getReporterUserSeq() {return reporterUserSeq;}

    public String getDescription() {return description;}

    public ReportReason getReason() {return reason;}

    public LocalDateTime getCreatedAt() {return createdAt;}

    public LocalDateTime getUpdatedAt() {return updatedAt;}
}