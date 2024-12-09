package com.backend.immilog.user.domain.repositories;

import com.backend.immilog.user.domain.model.Report;

public interface ReportRepository {
    boolean existsByUserSeqNumbers(
            Long targetUserSeq,
            Long reporterUserSeq
    );

    Report save(Report report);
}
