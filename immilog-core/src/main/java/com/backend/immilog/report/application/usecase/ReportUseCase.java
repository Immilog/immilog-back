package com.backend.immilog.report.application.usecase;

import com.backend.immilog.report.application.dto.ReportCommand;
import com.backend.immilog.report.application.service.ReportService;
import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.shared.aop.annotation.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReportUseCase {
    private final ReportService reportService;

    public ReportUseCase(ReportService reportService) {
        this.reportService = reportService;
    }

    @Async
    @DistributedLock(key = "'targetId : '", identifier = "#p0.toString()", expireTime = 5)
    public void execute(String targetId, ReportCommand command) {
        var reporterUserId = command.reporterUserId();
        var reason = command.reason();
        var isOther = reason.equals(ReportReason.OTHER);
        var description = isOther ? command.description() : reason.getDescription();
        reportService.report(targetId, reporterUserId, reason, description);
        log.info("User {} reported by {}", targetId, reporterUserId);
    }
}