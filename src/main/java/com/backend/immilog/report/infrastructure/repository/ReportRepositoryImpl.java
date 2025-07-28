package com.backend.immilog.report.infrastructure.repository;

import com.backend.immilog.report.domain.enums.ReportStatus;
import com.backend.immilog.report.domain.model.Report;
import com.backend.immilog.report.domain.model.ReportId;
import com.backend.immilog.report.domain.model.ReportTarget;
import com.backend.immilog.report.domain.repository.ReportRepository;
import com.backend.immilog.report.infrastructure.jpa.ReportJpaEntity;
import com.backend.immilog.report.infrastructure.jpa.ReportJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReportRepositoryImpl implements ReportRepository {

    private final ReportJpaRepository jpaRepository;

    public ReportRepositoryImpl(ReportJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Report> findById(ReportId reportId) {
        return jpaRepository.findById(reportId.value()).map(ReportJpaEntity::toDomain);
    }

    @Override
    public Optional<Report> findById(Long reportId) {
        return jpaRepository.findById(reportId).map(ReportJpaEntity::toDomain);
    }

    @Override
    public void delete(Report report) {
        var entity = ReportJpaEntity.from(report);
        jpaRepository.delete(entity);
    }

    @Override
    public void deleteById(ReportId reportId) {
        jpaRepository.deleteById(reportId.value());
    }

    @Override
    public List<Report> findByTarget(ReportTarget target) {
        return jpaRepository.findByIdAndTargetType(target.targetId(), target.type())
                .stream()
                .map(ReportJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Report> findByReporterId(Long reporterId) {
        return jpaRepository.findByTargetIdOrderByCreatedAtDesc(reporterId)
                .stream()
                .map(ReportJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Report> findByStatus(ReportStatus status) {
        return jpaRepository.findByStatus(status)
                .stream()
                .map(ReportJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByTargetAndReporterId(
            ReportTarget target,
            Long reporterId
    ) {
        return jpaRepository.existsByTargetIdAndReporterId(
                target.targetId(),
                reporterId
        );
    }

    @Override
    public long countByTarget(ReportTarget target) {
        return jpaRepository.countByReporterId(target.targetId());
    }

    @Override
    public long countByReporterId(Long reporterId) {
        return jpaRepository.countByReporterId(reporterId);
    }

    @Override
    public List<Report> findPendingReports() {
        return jpaRepository.findAllByStatus(ReportStatus.PENDING)
                .stream()
                .map(ReportJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Report> findReportsUnderReview() {
        return jpaRepository.findAllByStatus(ReportStatus.UNDER_REVIEW)
                .stream()
                .map(ReportJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Report save(Report report) {
        var entity = ReportJpaEntity.from(report);
        var savedEntity = jpaRepository.save(entity);
        return savedEntity.toDomain();
    }
}
