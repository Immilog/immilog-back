package com.backend.immilog.user.application.usecase;

import com.backend.immilog.shared.aop.annotation.DistributedLock;
import com.backend.immilog.user.application.command.ReportCommand;
import com.backend.immilog.user.application.services.ReportService;
import com.backend.immilog.user.domain.model.report.ReportReason;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

public interface RepostUserUseCase {
    void reportUser(
            Long targetUserSeq,
            Long reporterUserSeq,
            ReportCommand command
    );

    @Slf4j
    @Service
    class Reporter implements RepostUserUseCase {
        private final ReportService reportService;

        public Reporter(ReportService reportService) {
            this.reportService = reportService;
        }

        @Async
        @DistributedLock(key = "'reportUser : '", identifier = "#p0.toString()", expireTime = 5)
        @Override
        public void reportUser(
                Long targetUserSeq,
                Long reporterUserSeq,
                ReportCommand command
        ) {
            boolean isOther = command.reason().equals(ReportReason.OTHER);
            var description = isOther ? command.description() : command.reason().reason();
            reportService.reportUser(targetUserSeq, reporterUserSeq, description, command.reason());
            log.info("User {} reported by {}", targetUserSeq, reporterUserSeq);
        }
    }
}
