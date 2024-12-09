package com.backend.immilog.user.application.services.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportQueryService {
    private final ReportQueryService reportQueryService;

    @Transactional(readOnly = true)
    public boolean existsByUserSeqNumbers(
            Long targetUserSeq,
            Long reporterUserSeq
    ) {
        return reportQueryService.existsByUserSeqNumbers(targetUserSeq, reporterUserSeq);
    }
}
