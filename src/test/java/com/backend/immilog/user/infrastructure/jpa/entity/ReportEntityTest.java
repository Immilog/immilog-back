package com.backend.immilog.user.infrastructure.jpa.entity;

import com.backend.immilog.user.domain.model.Report;
import com.backend.immilog.user.domain.enums.ReportReason;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ReportEntity 테스트")
class ReportEntityTest {
    @Test
    @DisplayName("ReportEntity -> Report")
    void reportEntityFromReport_validReport() {
        Report report = Report.builder()
                .reportedUserSeq(1L)
                .reporterUserSeq(2L)
                .description("Test description")
                .reason(ReportReason.SPAM)
                .build();

        ReportEntity reportEntity = ReportEntity.from(report);

        assertThat(reportEntity.getReportedUserSeq()).isEqualTo(report.reportedUserSeq());
        assertThat(reportEntity.getReporterUserSeq()).isEqualTo(report.reporterUserSeq());
        assertThat(reportEntity.getDescription()).isEqualTo(report.description());
        assertThat(reportEntity.getReason()).isEqualTo(report.reason());
    }

    @Test
    @DisplayName("ReportEntity -> toDomain")
    void reportEntityToDomain_validReportEntity() {
        ReportEntity reportEntity = ReportEntity.builder()
                .seq(1L)
                .reportedUserSeq(1L)
                .reporterUserSeq(2L)
                .description("Test description")
                .reason(ReportReason.SPAM)
                .build();

        Report report = reportEntity.toDomain();

        assertThat(report.seq()).isEqualTo(reportEntity.getSeq());
        assertThat(report.reportedUserSeq()).isEqualTo(reportEntity.getReportedUserSeq());
        assertThat(report.reporterUserSeq()).isEqualTo(reportEntity.getReporterUserSeq());
        assertThat(report.description()).isEqualTo(reportEntity.getDescription());
        assertThat(report.reason()).isEqualTo(reportEntity.getReason());
    }

    @Test
    @DisplayName("ReportEntity -> Report - null")
    void reportEntityFromReport_nullReport() {
        Report report = null;

        assertThatThrownBy(() -> ReportEntity.from(report))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("ReportEntity -> toDomain")
    void reportEntityToDomain_nullFields() {
        ReportEntity reportEntity = ReportEntity.builder().build();

        Report report = reportEntity.toDomain();

        assertThat(report.seq()).isNull();
        assertThat(report.reportedUserSeq()).isNull();
        assertThat(report.reporterUserSeq()).isNull();
        assertThat(report.description()).isNull();
        assertThat(report.reason()).isNull();
    }
}