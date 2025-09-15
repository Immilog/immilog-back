package com.backend.immilog.report.domain.model;

import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.report.domain.enums.ReportStatus;
import com.backend.immilog.report.domain.enums.ReportTargetType;
import com.backend.immilog.report.exception.ReportErrorCode;
import com.backend.immilog.report.exception.ReportException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Report 도메인 모델")
class ReportTest {

    @Nested
    @DisplayName("신고 생성")
    class CreateReport {

        @Test
        @DisplayName("사용자 신고 생성 성공")
        void createUserReportSuccess() {
            var target = ReportTarget.user("user123");
            var reporterId = "reporter456";
            var reason = ReportReason.SPAM;
            var customDescription = "스팸 광고입니다";

            var report = Report.create(target, reporterId, reason, customDescription);

            assertThat(report.getTarget()).isEqualTo(target);
            assertThat(report.getReporterId()).isEqualTo(reporterId);
            assertThat(report.getReason()).isEqualTo(reason);
            assertThat(report.getStatus()).isEqualTo(ReportStatus.PENDING);
            assertThat(report.getCreatedAt()).isNotNull();
            assertThat(report.getUpdatedAt()).isNotNull();
            assertThat(report.getResolvedAt()).isNull();
        }

        @Test
        @DisplayName("게시물 신고 생성 성공")
        void createPostReportSuccess() {
            var target = ReportTarget.post("post123");
            var reporterId = "reporter456";
            var reason = ReportReason.INAPPROPRIATE_CONTENT;
            String customDescription = null;

            var report = Report.create(target, reporterId, reason, customDescription);

            assertThat(report.getTarget()).isEqualTo(target);
            assertThat(report.getReporterId()).isEqualTo(reporterId);
            assertThat(report.getReason()).isEqualTo(reason);
            assertThat(report.getDescriptionValue()).isEqualTo(reason.getDescription());
        }

        @Test
        @DisplayName("댓글 신고 생성 성공")
        void createCommentReportSuccess() {
            var target = ReportTarget.comment("comment123");
            var reporterId = "reporter456";
            var reason = ReportReason.HATE_SPEECH;
            String customDescription = null;

            var report = Report.create(target, reporterId, reason, customDescription);

            assertThat(report.getTarget()).isEqualTo(target);
            assertThat(report.getTargetType()).isEqualTo(ReportTargetType.COMMENT);
            assertThat(report.getTargetId()).isEqualTo("comment123");
        }

        @Test
        @DisplayName("기타 사유로 신고 생성 - 커스텀 설명 제공")
        void createReportWithOtherReason() {
            var target = ReportTarget.user("user123");
            var reporterId = "reporter456";
            var reason = ReportReason.OTHER;
            var customDescription = "특별한 이유가 있습니다";

            var report = Report.create(target, reporterId, reason, customDescription);

            assertThat(report.getReason()).isEqualTo(ReportReason.OTHER);
            assertThat(report.getDescriptionValue()).isEqualTo(customDescription);
        }

        @Test
        @DisplayName("자기 자신을 신고할 수 없음")
        void cannotReportYourself() {
            var target = ReportTarget.user("user123");
            var reporterId = "user123";
            var reason = ReportReason.SPAM;
            String customDescription = null;

            assertThatThrownBy(() -> Report.create(target, reporterId, reason, customDescription))
                    .isInstanceOf(ReportException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ReportErrorCode.CANNOT_REPORT_YOURSELF);
        }

        @Test
        @DisplayName("신고 대상이 null인 경우 예외")
        void invalidReportTarget() {
            var reporterId = "reporter456";
            var reason = ReportReason.SPAM;
            String customDescription = null;

            assertThatThrownBy(() -> Report.create(null, reporterId, reason, customDescription))
                    .isInstanceOf(ReportException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ReportErrorCode.INVALID_REPORT_TARGET);
        }

        @Test
        @DisplayName("신고자가 null인 경우 예외")
        void invalidReporter() {
            var target = ReportTarget.user("user123");
            var reason = ReportReason.SPAM;
            String customDescription = null;

            assertThatThrownBy(() -> Report.create(target, null, reason, customDescription))
                    .isInstanceOf(ReportException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ReportErrorCode.INVALID_REPORTER);

            assertThatThrownBy(() -> Report.create(target, "", reason, customDescription))
                    .isInstanceOf(ReportException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ReportErrorCode.INVALID_REPORTER);
        }

        @Test
        @DisplayName("신고 사유가 null인 경우 예외")
        void invalidReportReason() {
            var target = ReportTarget.user("user123");
            var reporterId = "reporter456";
            String customDescription = null;

            assertThatThrownBy(() -> Report.create(target, reporterId, null, customDescription))
                    .isInstanceOf(ReportException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ReportErrorCode.INVALID_REPORT_REASON);
        }
    }

    @Nested
    @DisplayName("신고 상태 변경")
    class ChangeReportStatus {

        @Test
        @DisplayName("대기중 -> 검토중 상태 변경")
        void startReview() {
            var report = createPendingReport();

            var updatedReport = report.startReview();

            assertThat(updatedReport.getStatus()).isEqualTo(ReportStatus.UNDER_REVIEW);
            assertThat(updatedReport.getUpdatedAt()).isNotNull();
            assertThat(updatedReport.getResolvedAt()).isNull();
        }

        @Test
        @DisplayName("검토중 -> 처리완료 상태 변경")
        void resolveReport() {
            var report = createPendingReport();
            report.startReview();

            var resolvedReport = report.resolve();

            assertThat(resolvedReport.getStatus()).isEqualTo(ReportStatus.RESOLVED);
            assertThat(resolvedReport.getResolvedAt()).isNotNull();
            assertThat(resolvedReport.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("검토중 -> 반려 상태 변경")
        void rejectReport() {
            var report = createPendingReport();
            report.startReview();

            var rejectedReport = report.reject();

            assertThat(rejectedReport.getStatus()).isEqualTo(ReportStatus.REJECTED);
            assertThat(rejectedReport.getResolvedAt()).isNotNull();
            assertThat(rejectedReport.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("대기중이 아닌 상태에서 검토 시작 시 예외")
        void invalidStartReview() {
            var report = createPendingReport();
            report.startReview();

            assertThatThrownBy(() -> report.startReview())
                    .isInstanceOf(ReportException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ReportErrorCode.INVALID_STATUS_TRANSITION);
        }

        @Test
        @DisplayName("검토중이 아닌 상태에서 처리완료 시 예외")
        void invalidResolve() {
            var report = createPendingReport();

            assertThatThrownBy(() -> report.resolve())
                    .isInstanceOf(ReportException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ReportErrorCode.INVALID_STATUS_TRANSITION);
        }

        @Test
        @DisplayName("검토중이 아닌 상태에서 반려 시 예외")
        void invalidReject() {
            var report = createPendingReport();

            assertThatThrownBy(() -> report.reject())
                    .isInstanceOf(ReportException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ReportErrorCode.INVALID_STATUS_TRANSITION);
        }
    }

    @Nested
    @DisplayName("신고 설명 수정")
    class UpdateDescription {

        @Test
        @DisplayName("대기중 상태에서 설명 수정 성공")
        void updateDescriptionWhenPending() {
            var report = createPendingReport();
            var newDescription = ReportDescription.of("수정된 설명입니다");

            var updatedReport = report.updateDescription(newDescription);

            assertThat(updatedReport.getDescription()).isEqualTo(newDescription);
            assertThat(updatedReport.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("처리된 신고는 설명 수정 불가")
        void cannotUpdateProcessedReport() {
            var report = createPendingReport();
            report.startReview();
            var newDescription = ReportDescription.of("수정된 설명입니다");

            assertThatThrownBy(() -> report.updateDescription(newDescription))
                    .isInstanceOf(ReportException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ReportErrorCode.CANNOT_UPDATE_PROCESSED_REPORT);
        }
    }

    @Nested
    @DisplayName("신고 상태 확인")
    class CheckReportStatus {

        @Test
        @DisplayName("대기중 상태 확인")
        void isPending() {
            var report = createPendingReport();

            assertThat(report.isPending()).isTrue();
            assertThat(report.isUnderReview()).isFalse();
            assertThat(report.isResolved()).isFalse();
            assertThat(report.isRejected()).isFalse();
        }

        @Test
        @DisplayName("검토중 상태 확인")
        void isUnderReview() {
            var report = createPendingReport();
            report.startReview();

            assertThat(report.isPending()).isFalse();
            assertThat(report.isUnderReview()).isTrue();
            assertThat(report.isResolved()).isFalse();
            assertThat(report.isRejected()).isFalse();
        }

        @Test
        @DisplayName("처리완료 상태 확인")
        void isResolved() {
            var report = createPendingReport();
            report.startReview();
            report.resolve();

            assertThat(report.isPending()).isFalse();
            assertThat(report.isUnderReview()).isFalse();
            assertThat(report.isResolved()).isTrue();
            assertThat(report.isRejected()).isFalse();
        }

        @Test
        @DisplayName("반려 상태 확인")
        void isRejected() {
            var report = createPendingReport();
            report.startReview();
            report.reject();

            assertThat(report.isPending()).isFalse();
            assertThat(report.isUnderReview()).isFalse();
            assertThat(report.isResolved()).isFalse();
            assertThat(report.isRejected()).isTrue();
        }
    }

    @Nested
    @DisplayName("신고자/대상 확인")
    class CheckReporter {

        @Test
        @DisplayName("신고자 확인")
        void isReporter() {
            var report = createPendingReport();

            assertThat(report.isReporter("reporter456")).isTrue();
            assertThat(report.isReporter("other123")).isFalse();
        }

        @Test
        @DisplayName("신고 대상 사용자 확인")
        void isTargetUser() {
            var report = createPendingReport();

            assertThat(report.isTargetUser("user123")).isTrue();
            assertThat(report.isTargetUser("other456")).isFalse();
        }

        @Test
        @DisplayName("게시물 신고는 대상 사용자가 아님")
        void postReportIsNotTargetUser() {
            var target = ReportTarget.post("post123");
            var report = Report.create(target, "reporter456", ReportReason.SPAM, null);

            assertThat(report.isTargetUser("post123")).isFalse();
        }
    }

    @Nested
    @DisplayName("getter 메서드")
    class GetterMethods {

        @Test
        @DisplayName("모든 getter 메서드 확인")
        void allGetterMethods() {
            var report = createPendingReport();

            assertThat(report.getIdValue()).isNull();
            assertThat(report.getDescriptionValue()).isNotNull();
            assertThat(report.getTargetType()).isEqualTo(ReportTargetType.USER);
            assertThat(report.getTargetId()).isEqualTo("user123");
            assertThat(report.getTarget()).isNotNull();
            assertThat(report.getReporterId()).isEqualTo("reporter456");
            assertThat(report.getReason()).isEqualTo(ReportReason.SPAM);
            assertThat(report.getStatus()).isEqualTo(ReportStatus.PENDING);
            assertThat(report.getCreatedAt()).isNotNull();
            assertThat(report.getUpdatedAt()).isNotNull();
            assertThat(report.getResolvedAt()).isNull();
        }

        @Test
        @DisplayName("복원된 신고의 ID 값 확인")
        void restoredReportIdValue() {
            var reportId = new ReportId("report123");
            var target = ReportTarget.user("user123");
            var description = ReportDescription.of("설명");
            var report = Report.restore(
                    reportId, target, "reporter456", description,
                    ReportReason.SPAM, ReportStatus.PENDING,
                    null, null, null
            );

            assertThat(report.getIdValue()).isEqualTo("report123");
        }
    }

    private Report createPendingReport() {
        var target = ReportTarget.user("user123");
        return Report.create(target, "reporter456", ReportReason.SPAM, null);
    }
}