package com.backend.immilog.user.domain.model;

import com.backend.immilog.user.application.command.UserReportCommand;
import com.backend.immilog.user.domain.enums.ReportReason;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Report(
        Long seq,
        Long reportedUserSeq,
        Long reporterUserSeq,
        String description,
        ReportReason reason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static Report of(
            Long targetUserSeq,
            Long reporterUserSeq,
            UserReportCommand reportUserCommand,
            boolean isOther
    ) {
        String description = isOther ?
                reportUserCommand.description() :
                reportUserCommand.reason().getReason();
        return Report.builder()
                .reportedUserSeq(targetUserSeq)
                .reporterUserSeq(reporterUserSeq)
                .reason(reportUserCommand.reason())
                .description(description)
                .build();
    }
}
