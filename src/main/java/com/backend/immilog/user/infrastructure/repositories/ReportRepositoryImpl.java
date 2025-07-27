package com.backend.immilog.user.infrastructure.repositories;

import com.backend.immilog.user.domain.model.report.Report;
import com.backend.immilog.user.domain.model.report.ReportId;
import com.backend.immilog.user.domain.model.user.UserId;
import com.backend.immilog.user.domain.repositories.ReportRepository;
import com.backend.immilog.user.infrastructure.jpa.ReportAggregateJpaEntity;
import com.backend.immilog.user.infrastructure.jpa.ReportAggregateJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ReportRepositoryImpl implements ReportRepository {

    private final ReportAggregateJpaRepository jpaRepository;

    public ReportRepositoryImpl(ReportAggregateJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Report> findById(ReportId reportId) {
        return jpaRepository.findById(reportId.value()).map(ReportAggregateJpaEntity::toDomain);
    }

    @Override
    public Report save(Report report) {
        var entity = ReportAggregateJpaEntity.from(report);
        var savedEntity = jpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public boolean existsByReportedUserIdAndReporterUserId(
            Long reportedUserId,
            Long reporterUserId
    ) {
        return jpaRepository.existsByReportedUserSeqAndReporterUserSeq(reportedUserId, reporterUserId);
    }

    @Override
    public long countByReportedUserId(UserId reportedUserId) {
        return jpaRepository.countByReportedUserSeq(reportedUserId.value());
    }

    @Override
    public List<Report> findByReportedUserId(UserId reportedUserId) {
        return jpaRepository.findByReportedUserSeqOrderByCreatedAtDesc(reportedUserId.value())
                .stream()
                .map(ReportAggregateJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findByReporterUserId(UserId reporterUserId) {
        return jpaRepository.findByReporterUserSeqOrderByCreatedAtDesc(reporterUserId.value())
                .stream()
                .map(ReportAggregateJpaEntity::toDomain)
                .collect(Collectors.toList());
    }
}
