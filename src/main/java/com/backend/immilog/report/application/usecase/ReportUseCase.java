package com.backend.immilog.report.application.usecase;

import com.backend.immilog.report.application.dto.ReportCommand;
import com.backend.immilog.report.application.service.ReportService;
import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.shared.aop.annotation.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

public interface ReportUseCase {
    void execute(
            Long targetUserSeq,
            ReportCommand command
    );

    @Slf4j
    @Service
    class Reporter implements ReportUseCase {
        private final ReportService reportService;

        public Reporter(ReportService reportService) {
            this.reportService = reportService;
        }

        @Async
        @DistributedLock(key = "'targetSeq : '", identifier = "#p0.toString()", expireTime = 5)
        @Override
        public void execute(
                Long targetSeq,
                ReportCommand command
        ) {
            var reporterUserId = command.reporterUserSeq();
            var reason = command.reason();
            var isOther = reason.equals(ReportReason.OTHER);
            var description = isOther ? command.description() : reason.getDescription();
            reportService.report(targetSeq, reporterUserId, reason, description);
            log.info("User {} reported by {}", targetSeq, reporterUserId);
        }
    }
}
