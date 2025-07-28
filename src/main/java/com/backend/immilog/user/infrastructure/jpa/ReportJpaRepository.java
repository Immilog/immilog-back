package com.backend.immilog.user.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportJpaRepository extends JpaRepository<ReportJpaEntity, Long> {

    boolean existsByReportedUserSeqAndReporterUserSeq(
            Long reportedUserSeq,
            Long reporterUserSeq
    );

    long countByReportedUserSeq(Long reportedUserSeq);

    List<ReportJpaEntity> findByReportedUserSeqOrderByCreatedAtDesc(Long reportedUserSeq);

    List<ReportJpaEntity> findByReporterUserSeqOrderByCreatedAtDesc(Long reporterUserSeq);
}