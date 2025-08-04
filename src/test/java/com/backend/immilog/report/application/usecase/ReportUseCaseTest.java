package com.backend.immilog.report.application.usecase;

import com.backend.immilog.report.application.dto.ReportCommand;
import com.backend.immilog.report.application.service.ReportService;
import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.report.domain.model.ReportId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReportUseCaseTest {

    private final ReportService mockReportService = mock(ReportService.class);

    private ReportUseCase.Reporter reportUseCase;

    @BeforeEach
    void setUp() {
        reportUseCase = new ReportUseCase.Reporter(mockReportService);
    }

    @Test
    @DisplayName("신고 실행 - 기본 사유")
    void executeReportWithBasicReason() {
        //given
        String targetId = "targetUserId";
        String reporterUserId = "reporterId";
        ReportReason reason = ReportReason.INAPPROPRIATE_CONTENT;
        ReportCommand command = new ReportCommand(reporterUserId, reason, null);
        
        when(mockReportService.report(eq(targetId), eq(reporterUserId), eq(reason), eq(reason.getDescription())))
                .thenReturn(ReportId.of("reportId"));

        //when
        reportUseCase.execute(targetId, command);

        //then
        verify(mockReportService).report(targetId, reporterUserId, reason, reason.getDescription());
    }

    @Test
    @DisplayName("신고 실행 - 기타 사유")
    void executeReportWithOtherReason() {
        //given
        String targetId = "targetUserId";
        String reporterUserId = "reporterId";
        ReportReason reason = ReportReason.OTHER;
        String customDescription = "기타 신고 사유입니다";
        ReportCommand command = new ReportCommand(reporterUserId, reason, customDescription);
        
        when(mockReportService.report(eq(targetId), eq(reporterUserId), eq(reason), eq(customDescription)))
                .thenReturn(ReportId.of("reportId"));

        //when
        reportUseCase.execute(targetId, command);

        //then
        verify(mockReportService).report(targetId, reporterUserId, reason, customDescription);
    }

    @Test
    @DisplayName("신고 실행 - 스팸 사유")
    void executeReportWithSpamReason() {
        //given
        String targetId = "targetUserId";
        String reporterUserId = "reporterId";
        ReportReason reason = ReportReason.SPAM;
        ReportCommand command = new ReportCommand(reporterUserId, reason, null);
        
        when(mockReportService.report(eq(targetId), eq(reporterUserId), eq(reason), eq(reason.getDescription())))
                .thenReturn(ReportId.of("reportId"));

        //when
        reportUseCase.execute(targetId, command);

        //then
        verify(mockReportService).report(targetId, reporterUserId, reason, reason.getDescription());
    }

    @Test
    @DisplayName("신고 실행 - 혐오 발언 사유")
    void executeReportWithHateSpeechReason() {
        //given
        String targetId = "targetUserId";
        String reporterUserId = "reporterId";
        ReportReason reason = ReportReason.HATE_SPEECH;
        ReportCommand command = new ReportCommand(reporterUserId, reason, null);
        
        when(mockReportService.report(eq(targetId), eq(reporterUserId), eq(reason), eq(reason.getDescription())))
                .thenReturn(ReportId.of("reportId"));

        //when
        reportUseCase.execute(targetId, command);

        //then
        verify(mockReportService).report(targetId, reporterUserId, reason, reason.getDescription());
    }

    @Test
    @DisplayName("신고 실행 - 괴롭힘 사유")
    void executeReportWithHarassmentReason() {
        //given
        String targetId = "targetUserId";
        String reporterUserId = "reporterId";
        ReportReason reason = ReportReason.HARASSMENT;
        ReportCommand command = new ReportCommand(reporterUserId, reason, null);
        
        when(mockReportService.report(eq(targetId), eq(reporterUserId), eq(reason), eq(reason.getDescription())))
                .thenReturn(ReportId.of("reportId"));

        //when
        reportUseCase.execute(targetId, command);

        //then
        verify(mockReportService).report(targetId, reporterUserId, reason, reason.getDescription());
    }

    @Test
    @DisplayName("신고 실행 - 폭력 사유")
    void executeReportWithViolenceReason() {
        //given
        String targetId = "targetUserId";
        String reporterUserId = "reporterId";
        ReportReason reason = ReportReason.VIOLENCE;
        ReportCommand command = new ReportCommand(reporterUserId, reason, null);
        
        when(mockReportService.report(eq(targetId), eq(reporterUserId), eq(reason), eq(reason.getDescription())))
                .thenReturn(ReportId.of("reportId"));

        //when
        reportUseCase.execute(targetId, command);

        //then
        verify(mockReportService).report(targetId, reporterUserId, reason, reason.getDescription());
    }

    @Test
    @DisplayName("신고 실행 - 사기 사유")
    void executeReportWithFraudReason() {
        //given
        String targetId = "targetUserId";
        String reporterUserId = "reporterId";
        ReportReason reason = ReportReason.FRAUD;
        ReportCommand command = new ReportCommand(reporterUserId, reason, null);
        
        when(mockReportService.report(eq(targetId), eq(reporterUserId), eq(reason), eq(reason.getDescription())))
                .thenReturn(ReportId.of("reportId"));

        //when
        reportUseCase.execute(targetId, command);

        //then
        verify(mockReportService).report(targetId, reporterUserId, reason, reason.getDescription());
    }

    @Test
    @DisplayName("신고 실행 - 불법 상품 사유")
    void executeReportWithIllegalProductReason() {
        //given
        String targetId = "targetUserId";
        String reporterUserId = "reporterId";
        ReportReason reason = ReportReason.ILLEGAL_PRODUCT;
        ReportCommand command = new ReportCommand(reporterUserId, reason, null);
        
        when(mockReportService.report(eq(targetId), eq(reporterUserId), eq(reason), eq(reason.getDescription())))
                .thenReturn(ReportId.of("reportId"));

        //when
        reportUseCase.execute(targetId, command);

        //then
        verify(mockReportService).report(targetId, reporterUserId, reason, reason.getDescription());
    }

    @Test
    @DisplayName("신고 실행 - 기타 사유이지만 설명이 null인 경우")
    void executeReportWithOtherReasonButNullDescription() {
        //given
        String targetId = "targetUserId";
        String reporterUserId = "reporterId";
        ReportReason reason = ReportReason.OTHER;
        ReportCommand command = new ReportCommand(reporterUserId, reason, null);
        
        when(mockReportService.report(eq(targetId), eq(reporterUserId), eq(reason), eq((String) null)))
                .thenReturn(ReportId.of("reportId"));

        //when
        reportUseCase.execute(targetId, command);

        //then
        verify(mockReportService).report(targetId, reporterUserId, reason, null);
    }

    @Test
    @DisplayName("신고 실행 - 기타 사유가 아니지만 설명이 제공된 경우")
    void executeReportWithNonOtherReasonButWithDescription() {
        //given
        String targetId = "targetUserId";
        String reporterUserId = "reporterId";
        ReportReason reason = ReportReason.SPAM;
        String customDescription = "커스텀 설명이지만 사용되지 않음";
        ReportCommand command = new ReportCommand(reporterUserId, reason, customDescription);
        
        when(mockReportService.report(eq(targetId), eq(reporterUserId), eq(reason), eq(reason.getDescription())))
                .thenReturn(ReportId.of("reportId"));

        //when
        reportUseCase.execute(targetId, command);

        //then
        verify(mockReportService).report(targetId, reporterUserId, reason, reason.getDescription());
    }
}