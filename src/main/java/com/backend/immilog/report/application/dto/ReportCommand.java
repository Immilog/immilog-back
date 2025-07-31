package com.backend.immilog.report.application.dto;


import com.backend.immilog.report.domain.enums.ReportReason;

public record ReportCommand(
        String reporterUserId,
        ReportReason reason,
        String description
) {
}
