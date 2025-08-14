package com.backend.immilog.report.application.service;

import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.report.domain.enums.ReportStatus;
import com.backend.immilog.report.domain.enums.ReportTargetType;
import com.backend.immilog.report.domain.model.Report;
import com.backend.immilog.report.domain.model.ReportId;
import com.backend.immilog.report.domain.model.ReportTarget;
import com.backend.immilog.report.domain.repository.ReportRepository;
import com.backend.immilog.report.exception.ReportException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReportQueryServiceTest {

    private final ReportRepository mockReportRepository = mock(ReportRepository.class);

    private ReportQueryService reportQueryService;

    @BeforeEach
    void setUp() {
        reportQueryService = new ReportQueryService(mockReportRepository);
    }

    @Test
    @DisplayName("ID로 신고 조회 - 존재하는 경우")
    void findByIdWhenExists() {
        //given
        String reportId = "reportId";
        Report expectedReport = createTestReportWithId();
        
        when(mockReportRepository.findById(reportId)).thenReturn(Optional.of(expectedReport));

        //when
        Optional<Report> result = reportQueryService.findById(reportId);

        //then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedReport);
    }

    @Test
    @DisplayName("ID로 신고 조회 - 존재하지 않는 경우")
    void findByIdWhenNotExists() {
        //given
        String reportId = "nonExistentReportId";
        
        when(mockReportRepository.findById(reportId)).thenReturn(Optional.empty());

        //when
        Optional<Report> result = reportQueryService.findById(reportId);

        //then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("ReportId로 신고 조회 - 존재하는 경우")
    void getByIdWhenExists() {
        //given
        ReportId reportId = ReportId.of("reportId");
        Report expectedReport = createTestReportWithId();
        
        when(mockReportRepository.findById(reportId)).thenReturn(Optional.of(expectedReport));

        //when
        Report result = reportQueryService.getById(reportId);

        //then
        assertThat(result).isEqualTo(expectedReport);
    }

    @Test
    @DisplayName("ReportId로 신고 조회 - 존재하지 않는 경우")
    void getByIdWhenNotExists() {
        //given
        ReportId reportId = ReportId.of("nonExistentReportId");
        
        when(mockReportRepository.findById(reportId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> reportQueryService.getById(reportId))
                .isInstanceOf(ReportException.class);
    }

    @Test
    @DisplayName("대상과 신고자로 신고 존재 여부 확인 - 존재하는 경우")
    void existsByTargetAndReporterWhenExists() {
        //given
        ReportTargetType targetType = ReportTargetType.USER;
        String targetId = "targetUserId";
        String reporterId = "reporterId";
        ReportTarget target = ReportTarget.of(targetType, targetId);
        
        when(mockReportRepository.existsByTargetAndReporterId(target, reporterId)).thenReturn(true);

        //when
        boolean result = reportQueryService.existsByTargetAndReporter(targetType, targetId, reporterId);

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("대상과 신고자로 신고 존재 여부 확인 - 존재하지 않는 경우")
    void existsByTargetAndReporterWhenNotExists() {
        //given
        ReportTargetType targetType = ReportTargetType.USER;
        String targetId = "targetUserId";
        String reporterId = "reporterId";
        ReportTarget target = ReportTarget.of(targetType, targetId);
        
        when(mockReportRepository.existsByTargetAndReporterId(target, reporterId)).thenReturn(false);

        //when
        boolean result = reportQueryService.existsByTargetAndReporter(targetType, targetId, reporterId);

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("대상으로 신고 목록 조회")
    void findByTarget() {
        //given
        ReportTargetType targetType = ReportTargetType.POST;
        String targetId = "postId";
        ReportTarget target = ReportTarget.of(targetType, targetId);
        List<Report> expectedReports = List.of(createTestReport(), createTestReport());
        
        when(mockReportRepository.findByTarget(target)).thenReturn(expectedReports);

        //when
        List<Report> result = reportQueryService.findByTarget(targetType, targetId);

        //then
        assertThat(result).isEqualTo(expectedReports);
    }

    @Test
    @DisplayName("신고자로 신고 목록 조회")
    void findByReporter() {
        //given
        String reporterId = "reporterId";
        List<Report> expectedReports = List.of(createTestReport(), createTestReport());
        
        when(mockReportRepository.findByReporterId(reporterId)).thenReturn(expectedReports);

        //when
        List<Report> result = reportQueryService.findByReporter(reporterId);

        //then
        assertThat(result).isEqualTo(expectedReports);
    }

    @Test
    @DisplayName("상태로 신고 목록 조회")
    void findByStatus() {
        //given
        ReportStatus status = ReportStatus.PENDING;
        List<Report> expectedReports = List.of(createTestReport(), createTestReport());
        
        when(mockReportRepository.findByStatus(status)).thenReturn(expectedReports);

        //when
        List<Report> result = reportQueryService.findByStatus(status);

        //then
        assertThat(result).isEqualTo(expectedReports);
    }

    @Test
    @DisplayName("대상별 신고 건수 조회")
    void countByTarget() {
        //given
        ReportTargetType targetType = ReportTargetType.USER;
        String targetId = "userId";
        ReportTarget target = ReportTarget.of(targetType, targetId);
        long expectedCount = 5L;
        
        when(mockReportRepository.countByTarget(target)).thenReturn(expectedCount);

        //when
        long result = reportQueryService.countByTarget(targetType, targetId);

        //then
        assertThat(result).isEqualTo(expectedCount);
    }

    @Test
    @DisplayName("신고자별 신고 건수 조회")
    void countByReporter() {
        //given
        String reporterId = "reporterId";
        long expectedCount = 3L;
        
        when(mockReportRepository.countByReporterId(reporterId)).thenReturn(expectedCount);

        //when
        long result = reportQueryService.countByReporter(reporterId);

        //then
        assertThat(result).isEqualTo(expectedCount);
    }

    @Test
    @DisplayName("대기 중인 신고 목록 조회")
    void findPendingReports() {
        //given
        List<Report> expectedReports = List.of(createTestReport(), createTestReport());
        
        when(mockReportRepository.findPendingReports()).thenReturn(expectedReports);

        //when
        List<Report> result = reportQueryService.findPendingReports();

        //then
        assertThat(result).isEqualTo(expectedReports);
    }

    @Test
    @DisplayName("검토 중인 신고 목록 조회")
    void findReportsUnderReview() {
        //given
        List<Report> expectedReports = List.of(createTestReportWithStatus(ReportStatus.UNDER_REVIEW));
        
        when(mockReportRepository.findReportsUnderReview()).thenReturn(expectedReports);

        //when
        List<Report> result = reportQueryService.findReportsUnderReview();

        //then
        assertThat(result).isEqualTo(expectedReports);
    }

    @Test
    @DisplayName("빈 결과 조회")
    void findEmptyResults() {
        //given
        String reporterId = "reporterId";
        List<Report> emptyList = List.of();
        
        when(mockReportRepository.findByReporterId(reporterId)).thenReturn(emptyList);

        //when
        List<Report> result = reportQueryService.findByReporter(reporterId);

        //then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("카운트 결과가 0인 경우")
    void countReturnsZero() {
        //given
        ReportTargetType targetType = ReportTargetType.COMMENT;
        String targetId = "commentId";
        ReportTarget target = ReportTarget.of(targetType, targetId);
        
        when(mockReportRepository.countByTarget(target)).thenReturn(0L);

        //when
        long result = reportQueryService.countByTarget(targetType, targetId);

        //then
        assertThat(result).isZero();
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
                ReportStatus.PENDING,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now(),
                null
        );
    }

    private Report createTestReportWithStatus(ReportStatus status) {
        return Report.restore(
                ReportId.of("reportId"),
                ReportTarget.user("targetUserId"),
                "reporterId",
                com.backend.immilog.report.domain.model.ReportDescription.of("부적절한 내용"),
                ReportReason.INAPPROPRIATE_CONTENT,
                status,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now(),
                status == ReportStatus.RESOLVED || status == ReportStatus.REJECTED ? java.time.LocalDateTime.now() : null
        );
    }
}