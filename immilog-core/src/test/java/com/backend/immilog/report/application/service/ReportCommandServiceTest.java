package com.backend.immilog.report.application.service;

import com.backend.immilog.report.domain.model.Report;
import com.backend.immilog.report.domain.model.ReportId;
import com.backend.immilog.report.domain.model.ReportTarget;
import com.backend.immilog.report.domain.repository.ReportRepository;
import com.backend.immilog.report.domain.enums.ReportReason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReportCommandServiceTest {

    private final ReportRepository mockReportRepository = mock(ReportRepository.class);

    private ReportCommandService reportCommandService;

    @BeforeEach
    void setUp() {
        reportCommandService = new ReportCommandService(mockReportRepository);
    }

    @Test
    @DisplayName("신고 저장 - 정상 케이스")
    void saveReportSuccessfully() {
        //given
        Report report = createTestReport();
        Report savedReport = createTestReportWithId();
        
        when(mockReportRepository.save(report)).thenReturn(savedReport);

        //when
        Report result = reportCommandService.save(report);

        //then
        assertThat(result).isEqualTo(savedReport);
        verify(mockReportRepository).save(report);
    }

    @Test
    @DisplayName("신고 삭제 - 정상 케이스")
    void deleteReportSuccessfully() {
        //given
        Report report = createTestReportWithId();

        //when
        reportCommandService.delete(report);

        //then
        verify(mockReportRepository).delete(report);
    }

    @Test
    @DisplayName("ID로 신고 삭제 - 정상 케이스")
    void deleteReportByIdSuccessfully() {
        //given
        String reportId = "reportId";
        Report report = createTestReportWithId();
        
        when(mockReportRepository.findById(reportId)).thenReturn(Optional.of(report));

        //when
        reportCommandService.deleteById(reportId);

        //then
        verify(mockReportRepository).findById(reportId);
        verify(mockReportRepository).deleteById(report.getId());
    }

    @Test
    @DisplayName("ID로 신고 삭제 실패 - 존재하지 않는 신고")
    void deleteReportByIdFailWhenNotFound() {
        //given
        String reportId = "nonExistentReportId";
        
        when(mockReportRepository.findById(reportId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> reportCommandService.deleteById(reportId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Report not found");
    }

    @Test
    @DisplayName("null인 신고 저장")
    void saveNullReport() {
        //given
        Report report = null;
        
        when(mockReportRepository.save(report)).thenReturn(null);

        //when
        Report result = reportCommandService.save(report);

        //then
        assertThat(result).isNull();
        verify(mockReportRepository).save(report);
    }

    @Test
    @DisplayName("null인 신고 삭제")
    void deleteNullReport() {
        //given
        Report report = null;

        //when
        reportCommandService.delete(report);

        //then
        verify(mockReportRepository).delete(report);
    }

    private Report createTestReport() {
        return Report.create(
                ReportTarget.user("targetUserId"),
                "reporterId",
                ReportReason.INAPPROPRIATE_CONTENT,
                null
        );
    }

    private Report createTestReportWithId() {
        return Report.restore(
                ReportId.of("reportId"),
                ReportTarget.user("targetUserId"),
                "reporterId",
                com.backend.immilog.report.domain.model.ReportDescription.of("부적절한 내용"),
                ReportReason.INAPPROPRIATE_CONTENT,
                com.backend.immilog.report.domain.enums.ReportStatus.PENDING,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now(),
                null
        );
    }
}