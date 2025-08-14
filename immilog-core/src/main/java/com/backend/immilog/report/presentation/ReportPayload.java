package com.backend.immilog.report.presentation;

import com.backend.immilog.report.application.dto.ReportCommand;
import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.report.domain.enums.ReportTargetType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ReportPayload() {
    @Schema(description = "사용자/게시물 신고 요청 DTO")
    public record ReportRequest(
            @Schema(description = "신고자 사용자 번호", example = "1") String reporterUserId,
            @Schema(description = "피신고자 / 피게시물번호", example = "2") String reportedTargetId,
            @Schema(description = "신고 대상 타입", example = "USER") ReportTargetType targetType,
            @Schema(description = "신고 사유", example = "SPAM") ReportReason reason,
            @Schema(description = "신고 내용", example = "신고 내용") String description
    ) {
        public ReportCommand toCommand() {
            return new ReportCommand(this.reporterUserId, this.reason, this.description);
        }
    }
}
