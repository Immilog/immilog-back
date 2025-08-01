package com.backend.immilog.report.application.service;

import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.report.domain.enums.ReportStatus;
import com.backend.immilog.report.domain.enums.ReportTargetType;
import com.backend.immilog.report.domain.model.Report;
import com.backend.immilog.report.domain.model.ReportId;
import com.backend.immilog.report.domain.model.ReportTarget;
import com.backend.immilog.report.domain.service.ReportCreationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReportServiceTest {

    private final ReportCommandService mockReportCommandService = mock(ReportCommandService.class);
    private final ReportQueryService mockReportQueryService = mock(ReportQueryService.class);
    private final ReportCreationService mockReportCreationService = mock(ReportCreationService.class);

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportService(
                mockReportCommandService,
                mockReportQueryService,
                mockReportCreationService
        );
    }

    @Test
    @DisplayName("사용자 신고 - 정상 케이스")
    void reportUserSuccessfully() {
        //given
        String targetUserId = "targetUserId";
        String reporterId = "reporterId";
        ReportReason reason = ReportReason.INAPPROPRIATE_CONTENT;
        String customDescription = null;
        
        Report createdReport = createTestReport();
        Report savedReport = createTestReportWithId();
        
        when(mockReportQueryService.existsByTargetAndReporter(ReportTargetType.USER, targetUserId, reporterId))
                .thenReturn(false);
        when(mockReportCreationService.createUserReport(targetUserId, reporterId, reason, customDescription))
                .thenReturn(createdReport);
        when(mockReportCommandService.save(createdReport))
                .thenReturn(savedReport);

        //when
        ReportId result = reportService.report(targetUserId, reporterId, reason, customDescription);

        //then
        assertThat(result).isEqualTo(savedReport.getId());
    }

    @Test
    @DisplayName("사용자 신고 실패 - 이미 신고한 경우")
    void reportUserFailWhenAlreadyReported() {
        //given
        String targetUserId = "targetUserId";
        String reporterId = "reporterId";
        ReportReason reason = ReportReason.INAPPROPRIATE_CONTENT;
        
        when(mockReportQueryService.existsByTargetAndReporter(ReportTargetType.USER, targetUserId, reporterId))
                .thenReturn(true);

        //when & then
        assertThatThrownBy(() -> reportService.report(targetUserId, reporterId, reason, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Already reported");
    }

    @Test
    @DisplayName("게시글 신고 - 정상 케이스")
    void reportPostSuccessfully() {
        //given
        String postId = "postId";
        String reporterId = "reporterId";
        ReportReason reason = ReportReason.SPAM;
        String customDescription = null;
        
        Report createdReport = createTestReport();
        Report savedReport = createTestReportWithId();
        
        when(mockReportQueryService.existsByTargetAndReporter(ReportTargetType.POST, postId, reporterId))
                .thenReturn(false);
        when(mockReportCreationService.createPostReport(postId, reporterId, reason, customDescription))
                .thenReturn(createdReport);
        when(mockReportCommandService.save(createdReport))
                .thenReturn(savedReport);

        //when
        ReportId result = reportService.reportPost(postId, reporterId, reason, customDescription);

        //then
        assertThat(result).isEqualTo(savedReport.getId());
    }

    @Test
    @DisplayName("게시글 신고 실패 - 이미 신고한 경우")
    void reportPostFailWhenAlreadyReported() {
        //given
        String postId = "postId";
        String reporterId = "reporterId";
        ReportReason reason = ReportReason.SPAM;
        
        when(mockReportQueryService.existsByTargetAndReporter(ReportTargetType.POST, postId, reporterId))
                .thenReturn(true);

        //when & then
        assertThatThrownBy(() -> reportService.reportPost(postId, reporterId, reason, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Already reported");
    }

    @Test
    @DisplayName("댓글 신고 - 정상 케이스")
    void reportCommentSuccessfully() {
        //given
        String commentId = "commentId";
        String reporterId = "reporterId";
        ReportReason reason = ReportReason.HATE_SPEECH;
        String customDescription = null;
        
        Report createdReport = createTestReport();
        Report savedReport = createTestReportWithId();
        
        when(mockReportQueryService.existsByTargetAndReporter(ReportTargetType.COMMENT, commentId, reporterId))
                .thenReturn(false);
        when(mockReportCreationService.createCommentReport(commentId, reporterId, reason, customDescription))
                .thenReturn(createdReport);
        when(mockReportCommandService.save(createdReport))
                .thenReturn(savedReport);

        //when
        ReportId result = reportService.reportComment(commentId, reporterId, reason, customDescription);

        //then
        assertThat(result).isEqualTo(savedReport.getId());
    }

    @Test
    @DisplayName("댓글 신고 실패 - 이미 신고한 경우")
    void reportCommentFailWhenAlreadyReported() {
        //given
        String commentId = "commentId";
        String reporterId = "reporterId";
        ReportReason reason = ReportReason.HATE_SPEECH;
        
        when(mockReportQueryService.existsByTargetAndReporter(ReportTargetType.COMMENT, commentId, reporterId))
                .thenReturn(true);

        //when & then
        assertThatThrownBy(() -> reportService.reportComment(commentId, reporterId, reason, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Already reported");
    }

    @Test
    @DisplayName("신고 처리 - 정상 케이스")
    void processReportSuccessfully() {
        //given
        ReportId reportId = ReportId.of("reportId");
        Report report = createTestReport();
        Report processedReport = createTestReportWithStatus(ReportStatus.UNDER_REVIEW);
        
        when(mockReportQueryService.getById(reportId)).thenReturn(report);
        when(mockReportCreationService.processReport(report)).thenReturn(processedReport);
        when(mockReportCommandService.save(processedReport)).thenReturn(processedReport);

        //when
        reportService.processReport(reportId);

        //then
        // 메서드 호출 검증은 Mockito.verify로 할 수 있지만 여기서는 생략
    }

    @Test
    @DisplayName("신고 해결 - 정상 케이스")
    void resolveReportSuccessfully() {
        //given
        ReportId reportId = ReportId.of("reportId");
        Report report = createTestReportWithStatus(ReportStatus.UNDER_REVIEW);
        Report resolvedReport = createTestReportWithStatus(ReportStatus.RESOLVED);
        
        when(mockReportQueryService.getById(reportId)).thenReturn(report);
        when(mockReportCreationService.resolveReport(report)).thenReturn(resolvedReport);
        when(mockReportCommandService.save(resolvedReport)).thenReturn(resolvedReport);

        //when
        reportService.resolveReport(reportId);

        //then
        // 메서드 호출 검증은 Mockito.verify로 할 수 있지만 여기서는 생략
    }

    @Test
    @DisplayName("신고 반려 - 정상 케이스")
    void rejectReportSuccessfully() {
        //given
        ReportId reportId = ReportId.of("reportId");
        Report report = createTestReportWithStatus(ReportStatus.UNDER_REVIEW);
        Report rejectedReport = createTestReportWithStatus(ReportStatus.REJECTED);
        
        when(mockReportQueryService.getById(reportId)).thenReturn(report);
        when(mockReportCreationService.rejectReport(report)).thenReturn(rejectedReport);
        when(mockReportCommandService.save(rejectedReport)).thenReturn(rejectedReport);

        //when
        reportService.rejectReport(reportId);
    }

    @Test
    @DisplayName("사용자별 신고 건수 조회")
    void getReportCountByUser() {
        //given
        String userId = "userId";
        long expectedCount = 5L;
        
        when(mockReportQueryService.countByTarget(ReportTargetType.USER, userId))
                .thenReturn(expectedCount);

        //when
        long result = reportService.getReportCountByUser(userId);

        //then
        assertThat(result).isEqualTo(expectedCount);
    }

    @Test
    @DisplayName("신고자별 신고 건수 조회")
    void getReportCountByReporter() {
        //given
        String reporterId = "reporterId";
        long expectedCount = 3L;
        
        when(mockReportQueryService.countByReporter(reporterId))
                .thenReturn(expectedCount);

        //when
        long result = reportService.getReportCountByReporter(reporterId);

        //then
        assertThat(result).isEqualTo(expectedCount);
    }

    @Test
    @DisplayName("사용자에 대한 신고 목록 조회")
    void getReportsByUser() {
        //given
        String userId = "userId";
        List<Report> expectedReports = List.of(createTestReport(), createTestReport());
        
        when(mockReportQueryService.findByTarget(ReportTargetType.USER, userId))
                .thenReturn(expectedReports);

        //when
        List<Report> result = reportService.getReportsByUser(userId);

        //then
        assertThat(result).isEqualTo(expectedReports);
    }

    @Test
    @DisplayName("신고자의 신고 목록 조회")
    void getReportsByReporter() {
        //given
        String reporterId = "reporterId";
        List<Report> expectedReports = List.of(createTestReport(), createTestReport());
        
        when(mockReportQueryService.findByReporter(reporterId))
                .thenReturn(expectedReports);

        //when
        List<Report> result = reportService.getReportsByReporter(reporterId);

        //then
        assertThat(result).isEqualTo(expectedReports);
    }

    @Test
    @DisplayName("대기 중인 신고 목록 조회")
    void getPendingReports() {
        //given
        List<Report> expectedReports = List.of(createTestReport(), createTestReport());
        
        when(mockReportQueryService.findPendingReports())
                .thenReturn(expectedReports);

        //when
        List<Report> result = reportService.getPendingReports();

        //then
        assertThat(result).isEqualTo(expectedReports);
    }

    @Test
    @DisplayName("검토 중인 신고 목록 조회")
    void getReportsUnderReview() {
        //given
        List<Report> expectedReports = List.of(createTestReportWithStatus(ReportStatus.UNDER_REVIEW));
        
        when(mockReportQueryService.findReportsUnderReview())
                .thenReturn(expectedReports);

        //when
        List<Report> result = reportService.getReportsUnderReview();

        //then
        assertThat(result).isEqualTo(expectedReports);
    }

    @Test
    @DisplayName("ID로 신고 조회")
    void getReportById() {
        //given
        ReportId reportId = ReportId.of("reportId");
        Report expectedReport = createTestReportWithId();
        
        when(mockReportQueryService.getById(reportId))
                .thenReturn(expectedReport);

        //when
        Report result = reportService.getReportById(reportId);

        //then
        assertThat(result).isEqualTo(expectedReport);
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