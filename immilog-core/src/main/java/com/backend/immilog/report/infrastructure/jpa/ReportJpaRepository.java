package com.backend.immilog.report.infrastructure.jpa;

import com.backend.immilog.report.domain.enums.ReportStatus;
import com.backend.immilog.report.domain.enums.ReportTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportJpaRepository extends JpaRepository<ReportJpaEntity, String> {

    boolean existsByTargetIdAndReporterId(
            String targetId,
            String reporterId
    );

    long countByTargetId(String reportedUserId);

    List<ReportJpaEntity> findByTargetIdOrderByCreatedAtDesc(String reportedUserId);

    List<ReportJpaEntity> findByReporterIdOrderByCreatedAtDesc(String ReporterId);

    List<ReportJpaEntity> findByIdAndTargetType(
            String id,
            ReportTargetType type
    );

    Optional<ReportJpaEntity> findByStatus(ReportStatus status);

    long countByReporterId(String reporterId);

    List<ReportJpaEntity> findAllByStatus(ReportStatus reportStatus);
}