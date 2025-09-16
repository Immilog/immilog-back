package com.backend.immilog.report.domain.service;

import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.report.domain.enums.ReportStatus;
import com.backend.immilog.report.domain.enums.ReportTargetType;
import com.backend.immilog.report.domain.model.Report;
import com.backend.immilog.report.domain.model.ReportTarget;
import com.backend.immilog.report.exception.ReportErrorCode;
import com.backend.immilog.report.exception.ReportException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ReportCreationService")
class ReportCreationServiceTest {

    private ReportCreationService service;

    @BeforeEach
    void setUp() {
        service = new ReportCreationService();
    }

    @Nested
    @DisplayName("신고 생성")
    class CreateReport {

        @Test
        @DisplayName("일반 신고 생성")
        void createReport() {
            var targetType = ReportTargetType.USER;
            var targetId = "user123";
            var reporterId = "reporter456";
            var reason = ReportReason.SPAM;
            var customDescription = "스팸 신고";

            var report = service.createReport(targetType, targetId, reporterId, reason, customDescription);

            assertThat(report.getTargetType()).isEqualTo(targetType);
            assertThat(report.getTargetId()).isEqualTo(targetId);
            assertThat(report.getReporterId()).isEqualTo(reporterId);
            assertThat(report.getReason()).isEqualTo(reason);
            assertThat(report.getStatus()).isEqualTo(ReportStatus.PENDING);
        }

        @Test
        @DisplayName("사용자 신고 생성")
        void createUserReport() {
            var targetUserId = "user123";
            var reporterId = "reporter456";
            var reason = ReportReason.HARASSMENT;
            var customDescription = "괴롭힘 신고";

            var report = service.createUserReport(targetUserId, reporterId, reason, customDescription);

            assertThat(report.getTargetType()).isEqualTo(ReportTargetType.USER);
            assertThat(report.getTargetId()).isEqualTo(targetUserId);
            assertThat(report.getReporterId()).isEqualTo(reporterId);
            assertThat(report.getReason()).isEqualTo(reason);
        }

        @Test
        @DisplayName("게시물 신고 생성")
        void createPostReport() {
            var postId = "post123";
            var reporterId = "reporter456";
            var reason = ReportReason.INAPPROPRIATE_CONTENT;
            String customDescription = null;

            var report = service.createPostReport(postId, reporterId, reason, customDescription);

            assertThat(report.getTargetType()).isEqualTo(ReportTargetType.POST);
            assertThat(report.getTargetId()).isEqualTo(postId);
            assertThat(report.getReporterId()).isEqualTo(reporterId);
            assertThat(report.getReason()).isEqualTo(reason);
        }

        @Test
        @DisplayName("댓글 신고 생성")
        void createCommentReport() {
            var commentId = "comment123";
            var reporterId = "reporter456";
            var reason = ReportReason.HATE_SPEECH;
            var customDescription = "혐오 발언";

            var report = service.createCommentReport(commentId, reporterId, reason, customDescription);

            assertThat(report.getTargetType()).isEqualTo(ReportTargetType.COMMENT);
            assertThat(report.getTargetId()).isEqualTo(commentId);
            assertThat(report.getReporterId()).isEqualTo(reporterId);
            assertThat(report.getReason()).isEqualTo(reason);
        }

        @Test
        @DisplayName("자기 자신 신고 시 예외")
        void cannotReportYourself() {
            var targetType = ReportTargetType.USER;
            var targetId = "user123";
            var reporterId = "user123";
            var reason = ReportReason.SPAM;
            String customDescription = null;

            assertThatThrownBy(() -> service.createReport(targetType, targetId, reporterId, reason, customDescription))
                    .isInstanceOf(ReportException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ReportErrorCode.CANNOT_REPORT_YOURSELF);
        }

        @Test
        @DisplayName("신고 사유가 null인 경우 예외")
        void invalidReportReason() {
            var targetType = ReportTargetType.USER;
            var targetId = "user123";
            var reporterId = "reporter456";
            String customDescription = null;

            assertThatThrownBy(() -> service.createReport(targetType, targetId, reporterId, null, customDescription))
                    .isInstanceOf(ReportException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ReportErrorCode.INVALID_REPORT_REASON);
        }
    }

    @Nested
    @DisplayName("신고 처리")
    class ProcessReport {

        @Test
        @DisplayName("신고 검토 시작")
        void processReport() {
            var report = createPendingReport();

            var processedReport = service.processReport(report);

            assertThat(processedReport.getStatus()).isEqualTo(ReportStatus.UNDER_REVIEW);
        }

        @Test
        @DisplayName("대기중이 아닌 신고는 검토 시작 불가")
        void cannotProcessNonPendingReport() {
            var report = createPendingReport();
            report.startReview();

            assertThatThrownBy(() -> service.processReport(report))
                    .isInstanceOf(ReportException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ReportErrorCode.INVALID_STATUS_TRANSITION);
        }

        @Test
        @DisplayName("신고 처리 완료")
        void resolveReport() {
            var report = createPendingReport();
            report.startReview();

            var resolvedReport = service.resolveReport(report);

            assertThat(resolvedReport.getStatus()).isEqualTo(ReportStatus.RESOLVED);
            assertThat(resolvedReport.getResolvedAt()).isNotNull();
        }

        @Test
        @DisplayName("검토중이 아닌 신고는 처리 완료 불가")
        void cannotResolveNonUnderReviewReport() {
            var report = createPendingReport();

            assertThatThrownBy(() -> service.resolveReport(report))
                    .isInstanceOf(ReportException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ReportErrorCode.INVALID_STATUS_TRANSITION);
        }

        @Test
        @DisplayName("신고 반려")
        void rejectReport() {
            var report = createPendingReport();
            report.startReview();

            var rejectedReport = service.rejectReport(report);

            assertThat(rejectedReport.getStatus()).isEqualTo(ReportStatus.REJECTED);
            assertThat(rejectedReport.getResolvedAt()).isNotNull();
        }

        @Test
        @DisplayName("검토중이 아닌 신고는 반려 불가")
        void cannotRejectNonUnderReviewReport() {
            var report = createPendingReport();

            assertThatThrownBy(() -> service.rejectReport(report))
                    .isInstanceOf(ReportException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ReportErrorCode.INVALID_STATUS_TRANSITION);
        }
    }

    private Report createPendingReport() {
        var target = ReportTarget.user("user123");
        return Report.create(target, "reporter456", ReportReason.SPAM, null);
    }
}