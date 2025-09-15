package com.backend.immilog.report.application.service;

import com.backend.immilog.report.domain.model.Report;
import com.backend.immilog.report.domain.model.ReportId;
import com.backend.immilog.report.domain.model.ReportTarget;
import com.backend.immilog.report.domain.repository.ReportRepository;
import com.backend.immilog.report.domain.service.ReportCreationService;
import com.backend.immilog.report.domain.enums.ReportReason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportProcessingService")
class ReportProcessingServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportCreationService reportCreationService;

    private ReportProcessingService service;

    @BeforeEach
    void setUp() {
        service = new ReportProcessingService(reportRepository, reportCreationService);
    }

    @Nested
    @DisplayName("신고 처리")
    class ProcessReport {

        @Test
        @DisplayName("신고 검토 시작")
        void processReport() {
            var reportId = new ReportId("report123");
            var report = createMockReport();
            var processedReport = createMockReport();

            when(reportRepository.getById(reportId)).thenReturn(report);
            when(reportCreationService.processReport(report)).thenReturn(processedReport);

            service.processReport(reportId);

            verify(reportRepository).getById(reportId);
            verify(reportCreationService).processReport(report);
            verify(reportRepository).save(processedReport);
        }

        @Test
        @DisplayName("신고 처리 완료")
        void resolveReport() {
            var reportId = new ReportId("report123");
            var report = createMockReport();
            var resolvedReport = createMockReport();

            when(reportRepository.getById(reportId)).thenReturn(report);
            when(reportCreationService.resolveReport(report)).thenReturn(resolvedReport);

            service.resolveReport(reportId);

            verify(reportRepository).getById(reportId);
            verify(reportCreationService).resolveReport(report);
            verify(reportRepository).save(resolvedReport);
        }

        @Test
        @DisplayName("신고 반려")
        void rejectReport() {
            var reportId = new ReportId("report123");
            var report = createMockReport();
            var rejectedReport = createMockReport();

            when(reportRepository.getById(reportId)).thenReturn(report);
            when(reportCreationService.rejectReport(report)).thenReturn(rejectedReport);

            service.rejectReport(reportId);

            verify(reportRepository).getById(reportId);
            verify(reportCreationService).rejectReport(report);
            verify(reportRepository).save(rejectedReport);
        }
    }

    private Report createMockReport() {
        return Report.create(
                ReportTarget.user("user123"),
                "reporter456",
                ReportReason.SPAM,
                "테스트 신고"
        );
    }
}