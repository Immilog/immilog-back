package com.backend.immilog.report.application.dto;


import com.backend.immilog.report.domain.enums.ReportReason;

public record ReportCommand(
        Long reporterUserSeq,
        ReportReason reason,
        String description
) {
}
