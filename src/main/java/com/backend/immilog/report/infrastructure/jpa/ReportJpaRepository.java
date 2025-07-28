package com.backend.immilog.report.infrastructure.jpa;

import com.backend.immilog.report.domain.enums.ReportStatus;
import com.backend.immilog.report.domain.enums.ReportTargetType;
import com.backend.immilog.report.domain.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportJpaRepository extends JpaRepository<ReportJpaEntity, Long> {

    boolean existsByTargetIdAndReporterId(
            Long targetId,
            Long reporterId
    );

    long countByTargetId(Long reportedUserSeq);

    List<ReportJpaEntity> findByTargetIdOrderByCreatedAtDesc(Long reportedUserSeq);

    List<ReportJpaEntity> findByReporterIdOrderByCreatedAtDesc(Long ReporterId);

    List<ReportJpaEntity> findByIdAndTargetType(
            Long id,
            ReportTargetType type
    );

    Optional<ReportJpaEntity> findByStatus(ReportStatus status);

    long countByReporterId(Long reporterId);

    List<ReportJpaEntity> findAllByStatus(ReportStatus reportStatus);
}