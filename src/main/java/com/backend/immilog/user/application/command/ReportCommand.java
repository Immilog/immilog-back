package com.backend.immilog.user.application.command;

import com.backend.immilog.user.domain.model.report.ReportReason;

public record ReportCommand(
        ReportReason reason,
        String description
) {
}
