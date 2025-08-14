package com.backend.immilog.report.domain.service;

import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.report.domain.enums.ReportStatus;
import com.backend.immilog.report.domain.enums.ReportTargetType;
import com.backend.immilog.report.domain.model.Report;
import com.backend.immilog.report.domain.model.ReportTarget;
import com.backend.immilog.report.exception.ReportException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ReportCreationServiceTest {

    private final ReportCreationService reportCreationService = new ReportCreationService();

    @Test
    @DisplayName("신고 생성 - 정상 케이스")
    void createReportSuccessfully() {
        //given
        ReportTargetType targetType = ReportTargetType.USER;
        String targetId = "targetUserId";
        String reporterId = "reporterId";
        ReportReason reason = ReportReason.INAPPROPRIATE_CONTENT;
        String customDescription = null;

        //when
        Report report = reportCreationService.createReport(targetType, targetId, reporterId, reason, customDescription);

        //then
        assertThat(report.getTargetType()).isEqualTo(targetType);
        assertThat(report.getTargetId()).isEqualTo(targetId);
        assertThat(report.getReporterId()).isEqualTo(reporterId);
        assertThat(report.getReason()).isEqualTo(reason);
        assertThat(report.getStatus()).isEqualTo(ReportStatus.PENDING);
    }

    @Test
    @DisplayName("사용자 신고 생성 - 정상 케이스")
    void createUserReportSuccessfully() {
        //given
        String targetUserId = "targetUserId";
        String reporterId = "reporterId";
        ReportReason reason = ReportReason.HATE_SPEECH;
        String customDescription = null;

        //when
        Report report = reportCreationService.createUserReport(targetUserId, reporterId, reason, customDescription);

        //then
        assertThat(report.getTargetType()).isEqualTo(ReportTargetType.USER);
        assertThat(report.getTargetId()).isEqualTo(targetUserId);
        assertThat(report.getReporterId()).isEqualTo(reporterId);
        assertThat(report.getReason()).isEqualTo(reason);
    }

    @Test
    @DisplayName("게시글 신고 생성 - 정상 케이스")
    void createPostReportSuccessfully() {
        //given
        String postId = "postId";
        String reporterId = "reporterId";
        ReportReason reason = ReportReason.SPAM;
        String customDescription = null;

        //when
        Report report = reportCreationService.createPostReport(postId, reporterId, reason, customDescription);

        //then
        assertThat(report.getTargetType()).isEqualTo(ReportTargetType.POST);
        assertThat(report.getTargetId()).isEqualTo(postId);
        assertThat(report.getReporterId()).isEqualTo(reporterId);
        assertThat(report.getReason()).isEqualTo(reason);
    }

    @Test
    @DisplayName("댓글 신고 생성 - 정상 케이스")
    void createCommentReportSuccessfully() {
        //given
        String commentId = "commentId";
        String reporterId = "reporterId";
        ReportReason reason = ReportReason.VIOLENCE;
        String customDescription = null;

        //when
        Report report = reportCreationService.createCommentReport(commentId, reporterId, reason, customDescription);

        //then
        assertThat(report.getTargetType()).isEqualTo(ReportTargetType.COMMENT);
        assertThat(report.getTargetId()).isEqualTo(commentId);
        assertThat(report.getReporterId()).isEqualTo(reporterId);
        assertThat(report.getReason()).isEqualTo(reason);
    }

    @Test
    @DisplayName("기타 사유로 신고 생성")
    void createReportWithOtherReason() {
        //given
        ReportTargetType targetType = ReportTargetType.POST;
        String targetId = "postId";
        String reporterId = "reporterId";
        ReportReason reason = ReportReason.OTHER;
        String customDescription = "기타 신고 사유";

        //when
        Report report = reportCreationService.createReport(targetType, targetId, reporterId, reason, customDescription);

        //then
        assertThat(report.getReason()).isEqualTo(ReportReason.OTHER);
        assertThat(report.getDescriptionValue()).isEqualTo(customDescription);
    }

    @Test
    @DisplayName("신고 생성 실패 - 자기 자신을 신고")
    void createReportFailWhenReportingSelf() {
        //given
        String sameUserId = "sameUserId";
        ReportTargetType targetType = ReportTargetType.USER;
        String targetId = sameUserId;
        String reporterId = sameUserId;
        ReportReason reason = ReportReason.INAPPROPRIATE_CONTENT;

        //when & then
        assertThatThrownBy(() -> reportCreationService.createReport(targetType, targetId, reporterId, reason, null))
                .isInstanceOf(ReportException.class);
    }

    @Test
    @DisplayName("신고 생성 실패 - null 사유")
    void createReportFailWhenReasonIsNull() {
        //given
        ReportTargetType targetType = ReportTargetType.USER;
        String targetId = "targetUserId";
        String reporterId = "reporterId";
        ReportReason reason = null;

        //when & then
        assertThatThrownBy(() -> reportCreationService.createReport(targetType, targetId, reporterId, reason, null))
                .isInstanceOf(ReportException.class);
    }

    @Test
    @DisplayName("신고 처리 - 정상 케이스")
    void processReportSuccessfully() {
        //given
        Report report = createTestReport();

        //when
        Report processedReport = reportCreationService.processReport(report);

        //then
        assertThat(processedReport.getStatus()).isEqualTo(ReportStatus.UNDER_REVIEW);
    }

    @Test
    @DisplayName("신고 처리 실패 - 대기 상태가 아님")
    void processReportFailWhenNotPending() {
        //given
        Report report = createTestReport();
        report.startReview();

        //when & then
        assertThatThrownBy(() -> reportCreationService.processReport(report))
                .isInstanceOf(ReportException.class);
    }

    @Test
    @DisplayName("신고 해결 - 정상 케이스")
    void resolveReportSuccessfully() {
        //given
        Report report = createTestReport();
        report.startReview();

        //when
        Report resolvedReport = reportCreationService.resolveReport(report);

        //then
        assertThat(resolvedReport.getStatus()).isEqualTo(ReportStatus.RESOLVED);
        assertThat(resolvedReport.getResolvedAt()).isNotNull();
    }

    @Test
    @DisplayName("신고 해결 실패 - 검토 중 상태가 아님")
    void resolveReportFailWhenNotUnderReview() {
        //given
        Report report = createTestReport();

        //when & then
        assertThatThrownBy(() -> reportCreationService.resolveReport(report))
                .isInstanceOf(ReportException.class);
    }

    @Test
    @DisplayName("신고 반려 - 정상 케이스")
    void rejectReportSuccessfully() {
        //given
        Report report = createTestReport();
        report.startReview();

        //when
        Report rejectedReport = reportCreationService.rejectReport(report);

        //then
        assertThat(rejectedReport.getStatus()).isEqualTo(ReportStatus.REJECTED);
        assertThat(rejectedReport.getResolvedAt()).isNotNull();
    }

    @Test
    @DisplayName("신고 반려 실패 - 검토 중 상태가 아님")
    void rejectReportFailWhenNotUnderReview() {
        //given
        Report report = createTestReport();

        //when & then
        assertThatThrownBy(() -> reportCreationService.rejectReport(report))
                .isInstanceOf(ReportException.class);
    }

    @Test
    @DisplayName("사용자 신고에서 자기 자신 신고 검증")
    void validateCannotReportYourselfInUserReport() {
        //given
        String sameUserId = "sameUserId";

        //when & then
        assertThatThrownBy(() -> reportCreationService.createUserReport(sameUserId, sameUserId, ReportReason.SPAM, null))
                .isInstanceOf(ReportException.class);
    }

    @Test
    @DisplayName("게시글 신고에서는 자기 자신 신고 제약 없음")
    void allowReportingOwnPostInPostReport() {
        //given
        String userId = "userId";
        String postId = "postId";

        //when
        Report report = reportCreationService.createPostReport(postId, userId, ReportReason.SPAM, null);

        //then
        assertThat(report).isNotNull();
        assertThat(report.getTargetType()).isEqualTo(ReportTargetType.POST);
    }

    @Test
    @DisplayName("댓글 신고에서는 자기 자신 신고 제약 없음")
    void allowReportingOwnCommentInCommentReport() {
        //given
        String userId = "userId";
        String commentId = "commentId";

        //when
        Report report = reportCreationService.createCommentReport(commentId, userId, ReportReason.SPAM, null);

        //then
        assertThat(report).isNotNull();
        assertThat(report.getTargetType()).isEqualTo(ReportTargetType.COMMENT);
    }

    private Report createTestReport() {
        return Report.create(
                ReportTarget.user("targetUserId"),
                "reporterId",
                ReportReason.INAPPROPRIATE_CONTENT,
                null
        );
    }
}