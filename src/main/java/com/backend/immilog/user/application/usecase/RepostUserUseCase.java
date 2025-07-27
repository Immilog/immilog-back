package com.backend.immilog.user.application.usecase;

import com.backend.immilog.shared.aop.annotation.DistributedLock;
import com.backend.immilog.user.application.command.UserReportCommand;
import com.backend.immilog.user.application.service.ReportApplicationService;
import com.backend.immilog.user.domain.model.report.ReportReason;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

public interface RepostUserUseCase {
    void reportUser(
            Long targetUserSeq,
            Long reporterUserSeq,
            UserReportCommand command
    );

    @Slf4j
    @Service
    class Reporter implements RepostUserUseCase {
        private final ReportApplicationService reportApplicationService;

        public Reporter(ReportApplicationService reportApplicationService) {
            this.reportApplicationService = reportApplicationService;
        }

        @Async
        @DistributedLock(key = "'reportUser : '", identifier = "#p0.toString()", expireTime = 5)
        @Override
        public void reportUser(
                Long targetUserSeq,
                Long reporterUserSeq,
                UserReportCommand command
        ) {
            boolean isOther = command.reason().equals(ReportReason.OTHER);
            var description = isOther ? command.description() : command.reason().reason();
            reportApplicationService.reportUser(targetUserSeq, reporterUserSeq, description, command.reason());
            log.info("User {} reported by {}", targetUserSeq, reporterUserSeq);
        }
    }
}
