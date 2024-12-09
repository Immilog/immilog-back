package com.backend.immilog.user.presentation.request;

import com.backend.immilog.user.application.command.UserReportCommand;
import com.backend.immilog.user.domain.enums.ReportReason;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "사용자 신고 요청 DTO")
public record UserReportRequest(
        ReportReason reason,
        String description
) {
    public UserReportCommand toCommand() {
        return UserReportCommand.builder()
                .reason(reason)
                .description(description)
                .build();
    }
}
