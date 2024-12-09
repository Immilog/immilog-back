package com.backend.immilog.user.infrastructure.jpa.entity;

import com.backend.immilog.global.model.BaseDateEntity;
import com.backend.immilog.user.domain.model.Report;
import com.backend.immilog.user.domain.enums.ReportReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Entity
public class ReportEntity extends BaseDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private Long reportedUserSeq;
    private Long reporterUserSeq;
    private String description;

    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    public static ReportEntity from(
            Report report
    ) {
        return ReportEntity.builder()
                .reportedUserSeq(report.reportedUserSeq())
                .reporterUserSeq(report.reporterUserSeq())
                .description(report.description())
                .reason(report.reason())
                .build();
    }

    public Report toDomain() {
        return Report.builder()
                .seq(seq)
                .reportedUserSeq(reportedUserSeq)
                .reporterUserSeq(reporterUserSeq)
                .description(description)
                .reason(reason)
                .build();
    }
}
