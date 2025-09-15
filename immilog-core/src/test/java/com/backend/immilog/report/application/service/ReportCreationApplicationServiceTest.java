package com.backend.immilog.report.application.service;

import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.report.domain.model.Report;
import com.backend.immilog.report.domain.model.ReportId;
import com.backend.immilog.report.domain.model.ReportTarget;
import com.backend.immilog.report.domain.repository.ReportRepository;
import com.backend.immilog.report.domain.service.ReportCreationService;
import com.backend.immilog.report.exception.ReportErrorCode;
import com.backend.immilog.report.exception.ReportException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportCreationApplicationService")
class ReportCreationApplicationServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportCreationService reportCreationService;

    private ReportCreationApplicationService service;

    @BeforeEach
    void setUp() {
        service = new ReportCreationApplicationService(reportRepository, reportCreationService);
    }

    @Nested
    @DisplayName("사용자 신고")
    class ReportUser {

        @Test
        @DisplayName("사용자 신고 성공")
        void reportUserSuccess() {
            var targetUserId = "user123";
            var reporterId = "reporter456";
            var reason = ReportReason.SPAM;
            var description = "스팸 신고";
            var report = createMockReport();
            var reportWithId = createMockReportWithId();

            when(reportRepository.existsByTargetAndReporterId(ReportTarget.user(targetUserId), reporterId))
                    .thenReturn(false);
            when(reportCreationService.createUserReport(targetUserId, reporterId, reason, description))
                    .thenReturn(report);
            when(reportRepository.save(report)).thenReturn(reportWithId);

            var result = service.reportUser(targetUserId, reporterId, reason, description);

            assertThat(result).isEqualTo(reportWithId.getId());
            verify(reportRepository).existsByTargetAndReporterId(ReportTarget.user(targetUserId), reporterId);
            verify(reportCreationService).createUserReport(targetUserId, reporterId, reason, description);
            verify(reportRepository).save(report);
        }

        @Test
        @DisplayName("이미 신고한 사용자 재신고 시 예외")
        void alreadyReportedUser() {
            var targetUserId = "user123";
            var reporterId = "reporter456";
            var reason = ReportReason.SPAM;
            var description = "스팸 신고";

            when(reportRepository.existsByTargetAndReporterId(ReportTarget.user(targetUserId), reporterId))
                    .thenReturn(true);

            assertThatThrownBy(() -> service.reportUser(targetUserId, reporterId, reason, description))
                    .isInstanceOf(ReportException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ReportErrorCode.ALREADY_REPORTED);

            verify(reportRepository).existsByTargetAndReporterId(ReportTarget.user(targetUserId), reporterId);
            verifyNoInteractions(reportCreationService);
        }
    }

    @Nested
    @DisplayName("게시물 신고")
    class ReportPost {

        @Test
        @DisplayName("게시물 신고 성공")
        void reportPostSuccess() {
            var postId = "post123";
            var reporterId = "reporter456";
            var reason = ReportReason.INAPPROPRIATE_CONTENT;
            var description = "부적절한 내용";
            var report = createMockReport();
            var reportWithId = createMockReportWithId();

            when(reportRepository.existsByTargetAndReporterId(ReportTarget.post(postId), reporterId))
                    .thenReturn(false);
            when(reportCreationService.createPostReport(postId, reporterId, reason, description))
                    .thenReturn(report);
            when(reportRepository.save(report)).thenReturn(reportWithId);

            var result = service.reportPost(postId, reporterId, reason, description);

            assertThat(result).isEqualTo(reportWithId.getId());
            verify(reportRepository).existsByTargetAndReporterId(ReportTarget.post(postId), reporterId);
            verify(reportCreationService).createPostReport(postId, reporterId, reason, description);
            verify(reportRepository).save(report);
        }

        @Test
        @DisplayName("이미 신고한 게시물 재신고 시 예외")
        void alreadyReportedPost() {
            var postId = "post123";
            var reporterId = "reporter456";
            var reason = ReportReason.INAPPROPRIATE_CONTENT;
            var description = "부적절한 내용";

            when(reportRepository.existsByTargetAndReporterId(ReportTarget.post(postId), reporterId))
                    .thenReturn(true);

            assertThatThrownBy(() -> service.reportPost(postId, reporterId, reason, description))
                    .isInstanceOf(ReportException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ReportErrorCode.ALREADY_REPORTED);

            verify(reportRepository).existsByTargetAndReporterId(ReportTarget.post(postId), reporterId);
            verifyNoInteractions(reportCreationService);
        }
    }

    @Nested
    @DisplayName("댓글 신고")
    class ReportComment {

        @Test
        @DisplayName("댓글 신고 성공")
        void reportCommentSuccess() {
            var commentId = "comment123";
            var reporterId = "reporter456";
            var reason = ReportReason.HATE_SPEECH;
            var description = "혐오 발언";
            var report = createMockReport();
            var reportWithId = createMockReportWithId();

            when(reportRepository.existsByTargetAndReporterId(ReportTarget.comment(commentId), reporterId))
                    .thenReturn(false);
            when(reportCreationService.createCommentReport(commentId, reporterId, reason, description))
                    .thenReturn(report);
            when(reportRepository.save(report)).thenReturn(reportWithId);

            var result = service.reportComment(commentId, reporterId, reason, description);

            assertThat(result).isEqualTo(reportWithId.getId());
            verify(reportRepository).existsByTargetAndReporterId(ReportTarget.comment(commentId), reporterId);
            verify(reportCreationService).createCommentReport(commentId, reporterId, reason, description);
            verify(reportRepository).save(report);
        }

        @Test
        @DisplayName("이미 신고한 댓글 재신고 시 예외")
        void alreadyReportedComment() {
            var commentId = "comment123";
            var reporterId = "reporter456";
            var reason = ReportReason.HATE_SPEECH;
            var description = "혐오 발언";

            when(reportRepository.existsByTargetAndReporterId(ReportTarget.comment(commentId), reporterId))
                    .thenReturn(true);

            assertThatThrownBy(() -> service.reportComment(commentId, reporterId, reason, description))
                    .isInstanceOf(ReportException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ReportErrorCode.ALREADY_REPORTED);

            verify(reportRepository).existsByTargetAndReporterId(ReportTarget.comment(commentId), reporterId);
            verifyNoInteractions(reportCreationService);
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

    private Report createMockReportWithId() {
        return Report.restore(
                new ReportId("report123"),
                ReportTarget.user("user123"),
                "reporter456",
                null,
                ReportReason.SPAM,
                null,
                null,
                null,
                null
        );
    }
}