package com.backend.immilog.report.application.service;

import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.report.domain.enums.ReportStatus;
import com.backend.immilog.report.domain.model.Report;
import com.backend.immilog.report.domain.model.ReportId;
import com.backend.immilog.report.domain.model.ReportTarget;
import com.backend.immilog.report.domain.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService")
class ReportServiceTest {

    @Mock
    private ReportCreationApplicationService reportCreationApplicationService;

    @Mock
    private ReportProcessingService reportProcessingService;

    @Mock
    private ReportRepository reportRepository;

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportService(
                reportCreationApplicationService,
                reportProcessingService,
                reportRepository
        );
    }

    @Nested
    @DisplayName("신고 생성")
    class CreateReport {

        @Test
        @DisplayName("사용자 신고")
        void reportUser() {
            var reportId = new ReportId("report123");
            when(reportCreationApplicationService.reportUser("user123", "reporter456", ReportReason.SPAM, "설명"))
                    .thenReturn(reportId);

            var result = reportService.report("user123", "reporter456", ReportReason.SPAM, "설명");

            assertThat(result).isEqualTo(reportId);
            verify(reportCreationApplicationService).reportUser("user123", "reporter456", ReportReason.SPAM, "설명");
        }

        @Test
        @DisplayName("게시물 신고")
        void reportPost() {
            var reportId = new ReportId("report123");
            when(reportCreationApplicationService.reportPost("post123", "reporter456", ReportReason.INAPPROPRIATE_CONTENT, null))
                    .thenReturn(reportId);

            var result = reportService.reportPost("post123", "reporter456", ReportReason.INAPPROPRIATE_CONTENT, null);

            assertThat(result).isEqualTo(reportId);
            verify(reportCreationApplicationService).reportPost("post123", "reporter456", ReportReason.INAPPROPRIATE_CONTENT, null);
        }

        @Test
        @DisplayName("댓글 신고")
        void reportComment() {
            var reportId = new ReportId("report123");
            when(reportCreationApplicationService.reportComment("comment123", "reporter456", ReportReason.HATE_SPEECH, "설명"))
                    .thenReturn(reportId);

            var result = reportService.reportComment("comment123", "reporter456", ReportReason.HATE_SPEECH, "설명");

            assertThat(result).isEqualTo(reportId);
            verify(reportCreationApplicationService).reportComment("comment123", "reporter456", ReportReason.HATE_SPEECH, "설명");
        }
    }

    @Nested
    @DisplayName("신고 처리")
    class ProcessReport {

        @Test
        @DisplayName("신고 검토 시작")
        void processReport() {
            var reportId = new ReportId("report123");

            reportService.processReport(reportId);

            verify(reportProcessingService).processReport(reportId);
        }

        @Test
        @DisplayName("신고 처리 완료")
        void resolveReport() {
            var reportId = new ReportId("report123");

            reportService.resolveReport(reportId);

            verify(reportProcessingService).resolveReport(reportId);
        }

        @Test
        @DisplayName("신고 반려")
        void rejectReport() {
            var reportId = new ReportId("report123");

            reportService.rejectReport(reportId);

            verify(reportProcessingService).rejectReport(reportId);
        }
    }

    @Nested
    @DisplayName("신고 조회")
    class QueryReport {

        @Test
        @DisplayName("사용자별 신고 수 조회")
        void getReportCountByUser() {
            when(reportRepository.countByTarget(ReportTarget.user("user123"))).thenReturn(5L);

            var count = reportService.getReportCountByUser("user123");

            assertThat(count).isEqualTo(5L);
            verify(reportRepository).countByTarget(ReportTarget.user("user123"));
        }

        @Test
        @DisplayName("신고자별 신고 수 조회")
        void getReportCountByReporter() {
            when(reportRepository.countByReporterId("reporter456")).thenReturn(3L);

            var count = reportService.getReportCountByReporter("reporter456");

            assertThat(count).isEqualTo(3L);
            verify(reportRepository).countByReporterId("reporter456");
        }

        @Test
        @DisplayName("사용자에 대한 신고 목록 조회")
        void getReportsByUser() {
            var reports = List.of(createMockReport(), createMockReport());
            when(reportRepository.findByTarget(ReportTarget.user("user123"))).thenReturn(reports);

            var result = reportService.getReportsByUser("user123");

            assertThat(result).hasSize(2);
            verify(reportRepository).findByTarget(ReportTarget.user("user123"));
        }

        @Test
        @DisplayName("신고자의 신고 목록 조회")
        void getReportsByReporter() {
            var reports = List.of(createMockReport(), createMockReport());
            when(reportRepository.findByReporterId("reporter456")).thenReturn(reports);

            var result = reportService.getReportsByReporter("reporter456");

            assertThat(result).hasSize(2);
            verify(reportRepository).findByReporterId("reporter456");
        }

        @Test
        @DisplayName("대기중인 신고 목록 조회")
        void getPendingReports() {
            var reports = List.of(createMockReport(), createMockReport());
            when(reportRepository.findPendingReports()).thenReturn(reports);

            var result = reportService.getPendingReports();

            assertThat(result).hasSize(2);
            verify(reportRepository).findPendingReports();
        }

        @Test
        @DisplayName("검토중인 신고 목록 조회")
        void getReportsUnderReview() {
            var reports = List.of(createMockReport());
            when(reportRepository.findReportsUnderReview()).thenReturn(reports);

            var result = reportService.getReportsUnderReview();

            assertThat(result).hasSize(1);
            verify(reportRepository).findReportsUnderReview();
        }

        @Test
        @DisplayName("ID로 신고 조회")
        void getReportById() {
            var reportId = new ReportId("report123");
            var report = createMockReport();
            when(reportRepository.getById(reportId)).thenReturn(report);

            var result = reportService.getReportById(reportId);

            assertThat(result).isEqualTo(report);
            verify(reportRepository).getById(reportId);
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