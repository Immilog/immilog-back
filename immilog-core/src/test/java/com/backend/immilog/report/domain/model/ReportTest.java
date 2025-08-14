package com.backend.immilog.report.domain.model;

import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.report.domain.enums.ReportStatus;
import com.backend.immilog.report.domain.enums.ReportTargetType;
import com.backend.immilog.report.exception.ReportException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class ReportTest {

    @Test
    @DisplayName("신고 생성 - 정상 케이스")
    void createReportSuccessfully() {
        //given
        ReportTarget target = ReportTarget.user("targetUserId");
        String reporterId = "reporterId";
        ReportReason reason = ReportReason.INAPPROPRIATE_CONTENT;
        String customDescription = null;

        //when
        Report report = Report.create(target, reporterId, reason, customDescription);

        //then
        assertThat(report.getTarget()).isEqualTo(target);
        assertThat(report.getReporterId()).isEqualTo(reporterId);
        assertThat(report.getReason()).isEqualTo(reason);
        assertThat(report.getStatus()).isEqualTo(ReportStatus.PENDING);
        assertThat(report.getCreatedAt()).isNotNull();
        assertThat(report.getUpdatedAt()).isNotNull();
        assertThat(report.getResolvedAt()).isNull();
        assertThat(report.getDescription().value()).isEqualTo(reason.getDescription());
    }

    @Test
    @DisplayName("신고 생성 - 기타 사유로 생성")
    void createReportWithOtherReason() {
        //given
        ReportTarget target = ReportTarget.post("postId");
        String reporterId = "reporterId";
        ReportReason reason = ReportReason.OTHER;
        String customDescription = "기타 신고 사유";

        //when
        Report report = Report.create(target, reporterId, reason, customDescription);

        //then
        assertThat(report.getReason()).isEqualTo(ReportReason.OTHER);
        assertThat(report.getDescription().value()).isEqualTo(customDescription);
    }

    @Test
    @DisplayName("신고 생성 실패 - 자기 자신을 신고")
    void createReportFailWhenReportingSelf() {
        //given
        String userId = "sameUserId";
        ReportTarget target = ReportTarget.user(userId);
        String reporterId = userId;
        ReportReason reason = ReportReason.INAPPROPRIATE_CONTENT;

        //when & then
        assertThatThrownBy(() -> Report.create(target, reporterId, reason, null))
                .isInstanceOf(ReportException.class);
    }

    @Test
    @DisplayName("신고 생성 실패 - null 대상")
    void createReportFailWhenTargetIsNull() {
        //given
        ReportTarget target = null;
        String reporterId = "reporterId";
        ReportReason reason = ReportReason.INAPPROPRIATE_CONTENT;

        //when & then
        assertThatThrownBy(() -> Report.create(target, reporterId, reason, null))
                .isInstanceOf(ReportException.class);
    }

    @Test
    @DisplayName("신고 생성 실패 - null 신고자")
    void createReportFailWhenReporterIsNull() {
        //given
        ReportTarget target = ReportTarget.user("targetUserId");
        String reporterId = null;
        ReportReason reason = ReportReason.INAPPROPRIATE_CONTENT;

        //when & then
        assertThatThrownBy(() -> Report.create(target, reporterId, reason, null))
                .isInstanceOf(ReportException.class);
    }

    @Test
    @DisplayName("신고 생성 실패 - 빈 문자열 신고자")
    void createReportFailWhenReporterIsBlank() {
        //given
        ReportTarget target = ReportTarget.user("targetUserId");
        String reporterId = "";
        ReportReason reason = ReportReason.INAPPROPRIATE_CONTENT;

        //when & then
        assertThatThrownBy(() -> Report.create(target, reporterId, reason, null))
                .isInstanceOf(ReportException.class);
    }

    @Test
    @DisplayName("신고 생성 실패 - null 사유")
    void createReportFailWhenReasonIsNull() {
        //given
        ReportTarget target = ReportTarget.user("targetUserId");
        String reporterId = "reporterId";
        ReportReason reason = null;

        //when & then
        assertThatThrownBy(() -> Report.create(target, reporterId, reason, null))
                .isInstanceOf(ReportException.class);
    }

    @Test
    @DisplayName("신고 설명 업데이트 - 대기 상태에서 성공")
    void updateDescriptionWhenPending() {
        //given
        Report report = createTestReport();
        ReportDescription newDescription = ReportDescription.of("새로운 설명");

        //when
        report.updateDescription(newDescription);

        //then
        assertThat(report.getDescription()).isEqualTo(newDescription);
        assertThat(report.getUpdatedAt()).isAfter(report.getCreatedAt());
    }

    @Test
    @DisplayName("신고 설명 업데이트 실패 - 처리된 신고")
    void updateDescriptionFailWhenProcessed() {
        //given
        Report report = createTestReport();
        report.startReview();
        ReportDescription newDescription = ReportDescription.of("새로운 설명");

        //when & then
        assertThatThrownBy(() -> report.updateDescription(newDescription))
                .isInstanceOf(ReportException.class);
    }

    @Test
    @DisplayName("신고 검토 시작 - 대기 상태에서 성공")
    void startReviewWhenPending() throws InterruptedException {
        //given
        Report report = createTestReport();
        Thread.sleep(1);

        //when
        report.startReview();

        //then
        assertThat(report.getStatus()).isEqualTo(ReportStatus.UNDER_REVIEW);
        assertThat(report.getUpdatedAt()).isAfter(report.getCreatedAt());
    }

    @Test
    @DisplayName("신고 검토 시작 실패 - 대기 상태가 아님")
    void startReviewFailWhenNotPending() {
        //given
        Report report = createTestReport();
        report.startReview();

        //when & then
        assertThatThrownBy(report::startReview)
                .isInstanceOf(ReportException.class);
    }

    @Test
    @DisplayName("신고 해결 - 검토 중 상태에서 성공")
    void resolveWhenUnderReview() {
        //given
        Report report = createTestReport();
        report.startReview();

        //when
        report.resolve();

        //then
        assertThat(report.getStatus()).isEqualTo(ReportStatus.RESOLVED);
        assertThat(report.getResolvedAt()).isNotNull();
        assertThat(report.getUpdatedAt()).isAfter(report.getCreatedAt());
    }

    @Test
    @DisplayName("신고 해결 실패 - 검토 중 상태가 아님")
    void resolveFailWhenNotUnderReview() {
        //given
        Report report = createTestReport();

        //when & then
        assertThatThrownBy(report::resolve)
                .isInstanceOf(ReportException.class);
    }

    @Test
    @DisplayName("신고 반려 - 검토 중 상태에서 성공")
    void rejectWhenUnderReview() {
        //given
        Report report = createTestReport();
        report.startReview();

        //when
        report.reject();

        //then
        assertThat(report.getStatus()).isEqualTo(ReportStatus.REJECTED);
        assertThat(report.getResolvedAt()).isNotNull();
        assertThat(report.getUpdatedAt()).isAfter(report.getCreatedAt());
    }

    @Test
    @DisplayName("신고 반려 실패 - 검토 중 상태가 아님")
    void rejectFailWhenNotUnderReview() {
        //given
        Report report = createTestReport();

        //when & then
        assertThatThrownBy(report::reject)
                .isInstanceOf(ReportException.class);
    }

    @Test
    @DisplayName("상태 확인 - 대기 중")
    void isPending() {
        //given
        Report report = createTestReport();

        //when & then
        assertThat(report.isPending()).isTrue();
        assertThat(report.isUnderReview()).isFalse();
        assertThat(report.isResolved()).isFalse();
        assertThat(report.isRejected()).isFalse();
    }

    @Test
    @DisplayName("상태 확인 - 검토 중")
    void isUnderReview() {
        //given
        Report report = createTestReport();
        report.startReview();

        //when & then
        assertThat(report.isPending()).isFalse();
        assertThat(report.isUnderReview()).isTrue();
        assertThat(report.isResolved()).isFalse();
        assertThat(report.isRejected()).isFalse();
    }

    @Test
    @DisplayName("상태 확인 - 해결됨")
    void isResolved() {
        //given
        Report report = createTestReport();
        report.startReview();
        report.resolve();

        //when & then
        assertThat(report.isPending()).isFalse();
        assertThat(report.isUnderReview()).isFalse();
        assertThat(report.isResolved()).isTrue();
        assertThat(report.isRejected()).isFalse();
    }

    @Test
    @DisplayName("상태 확인 - 반려됨")
    void isRejected() {
        //given
        Report report = createTestReport();
        report.startReview();
        report.reject();

        //when & then
        assertThat(report.isPending()).isFalse();
        assertThat(report.isUnderReview()).isFalse();
        assertThat(report.isResolved()).isFalse();
        assertThat(report.isRejected()).isTrue();
    }

    @Test
    @DisplayName("신고자 확인 - 맞음")
    void isReporterTrue() {
        //given
        String reporterId = "reporterId";
        Report report = createTestReportWithReporter(reporterId);

        //when & then
        assertThat(report.isReporter(reporterId)).isTrue();
    }

    @Test
    @DisplayName("신고자 확인 - 틀림")
    void isReporterFalse() {
        //given
        String reporterId = "reporterId";
        String otherUserId = "otherUserId";
        Report report = createTestReportWithReporter(reporterId);

        //when & then
        assertThat(report.isReporter(otherUserId)).isFalse();
    }

    @Test
    @DisplayName("신고 대상 사용자 확인 - 맞음")
    void isTargetUserTrue() {
        //given
        String targetUserId = "targetUserId";
        Report report = Report.create(
                ReportTarget.user(targetUserId),
                "reporterId",
                ReportReason.INAPPROPRIATE_CONTENT,
                null
        );

        //when & then
        assertThat(report.isTargetUser(targetUserId)).isTrue();
    }

    @Test
    @DisplayName("신고 대상 사용자 확인 - 틀림")
    void isTargetUserFalse() {
        //given
        String targetUserId = "targetUserId";
        String otherUserId = "otherUserId";
        Report report = Report.create(
                ReportTarget.user(targetUserId),
                "reporterId",
                ReportReason.INAPPROPRIATE_CONTENT,
                null
        );

        //when & then
        assertThat(report.isTargetUser(otherUserId)).isFalse();
    }

    @Test
    @DisplayName("신고 대상 사용자 확인 - 게시글 신고인 경우")
    void isTargetUserFalseWhenTargetIsPost() {
        //given
        String userId = "userId";
        Report report = Report.create(
                ReportTarget.post("postId"),
                "reporterId",
                ReportReason.INAPPROPRIATE_CONTENT,
                null
        );

        //when & then
        assertThat(report.isTargetUser(userId)).isFalse();
    }

    @Test
    @DisplayName("신고 복원 - 모든 필드 포함")
    void restoreReport() {
        //given
        ReportId id = ReportId.of("reportId");
        ReportTarget target = ReportTarget.user("targetUserId");
        String reporterId = "reporterId";
        ReportDescription description = ReportDescription.of("설명");
        ReportReason reason = ReportReason.SPAM;
        ReportStatus status = ReportStatus.RESOLVED;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now().minusHours(1);
        LocalDateTime resolvedAt = LocalDateTime.now();

        //when
        Report report = Report.restore(
                id, target, reporterId, description, reason, status,
                createdAt, updatedAt, resolvedAt
        );

        //then
        assertThat(report.getId()).isEqualTo(id);
        assertThat(report.getTarget()).isEqualTo(target);
        assertThat(report.getReporterId()).isEqualTo(reporterId);
        assertThat(report.getDescription()).isEqualTo(description);
        assertThat(report.getReason()).isEqualTo(reason);
        assertThat(report.getStatus()).isEqualTo(status);
        assertThat(report.getCreatedAt()).isEqualTo(createdAt);
        assertThat(report.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(report.getResolvedAt()).isEqualTo(resolvedAt);
    }

    @Test
    @DisplayName("게터 메서드 테스트")
    void getterMethods() {
        //given
        Report report = createTestReport();
        
        //when & then
        assertThat(report.getIdValue()).isNull();
        assertThat(report.getDescriptionValue()).isEqualTo(ReportReason.INAPPROPRIATE_CONTENT.getDescription());
        assertThat(report.getTargetType()).isEqualTo(ReportTargetType.USER);
        assertThat(report.getTargetId()).isEqualTo("targetUserId");
    }

    @Test
    @DisplayName("ID가 있는 신고의 게터 메서드 테스트")
    void getterMethodsWithId() {
        //given
        ReportId id = ReportId.of("reportId");
        Report report = Report.restore(
                id,
                ReportTarget.user("targetUserId"),
                "reporterId",
                ReportDescription.of("설명"),
                ReportReason.SPAM,
                ReportStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
        
        //when & then
        assertThat(report.getIdValue()).isEqualTo("reportId");
    }

    private Report createTestReport() {
        return Report.create(
                ReportTarget.user("targetUserId"),
                "reporterId",
                ReportReason.INAPPROPRIATE_CONTENT,
                null
        );
    }

    private Report createTestReportWithReporter(String reporterId) {
        return Report.create(
                ReportTarget.user("targetUserId"),
                reporterId,
                ReportReason.INAPPROPRIATE_CONTENT,
                null
        );
    }
}