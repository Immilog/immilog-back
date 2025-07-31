package com.backend.immilog.jobboard.infrastructure.repositories;

import com.backend.immilog.jobboard.domain.model.JobBoard;
import com.backend.immilog.jobboard.domain.model.JobBoardId;
import com.backend.immilog.jobboard.domain.repositories.JobBoardRepository;
import com.backend.immilog.jobboard.infrastructure.jpa.JobBoardEntity;
import com.backend.immilog.jobboard.infrastructure.jpa.JobBoardJpaRepository;
import com.backend.immilog.shared.enums.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class JobBoardRepositoryImpl implements JobBoardRepository {
    private final JobBoardJpaRepository jobBoardJpaRepository;

    public JobBoardRepositoryImpl(JobBoardJpaRepository jobBoardJpaRepository) {
        this.jobBoardJpaRepository = jobBoardJpaRepository;
    }

    @Override
    public Page<JobBoard> findJobBoards(
            Country country,
            Pageable pageable
    ) {
        return jobBoardJpaRepository.findByCountryOrderByCreatedAtDesc(country, pageable)
                .map(JobBoardEntity::toDomain);
    }

    @Override
    public Optional<JobBoard> findById(String jobBoardId) {
        return jobBoardJpaRepository.findById(jobBoardId)
                .map(JobBoardEntity::toDomain);
    }

    @Override
    public JobBoard save(JobBoard jobBoard) {
        var entity = jobBoardJpaRepository.save(JobBoardEntity.from(jobBoard));
        return entity.toDomain();
    }

    @Override
    public Optional<JobBoard> findByJobBoardId(JobBoardId jobBoardId) {
        return jobBoardJpaRepository.findById(jobBoardId.value())
                .map(JobBoardEntity::toDomain);
    }

    @Override
    public List<JobBoard> findByUserId(String userId) {
        return jobBoardJpaRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(JobBoardEntity::toDomain)
                .toList();
    }

    @Override
    public List<JobBoard> findExpiredJobBoards(LocalDate currentDate) {
        return jobBoardJpaRepository.findByApplicationDeadlineBeforeAndIsActiveTrue(currentDate)
                .stream()
                .map(JobBoardEntity::toDomain)
                .toList();
    }

    @Override
    public List<JobBoard> findActiveJobBoardsByDeadlineBefore(LocalDate date) {
        return jobBoardJpaRepository.findByApplicationDeadlineBeforeAndIsActiveTrue(date)
                .stream()
                .map(JobBoardEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(String jobBoardId) {
        jobBoardJpaRepository.deleteById(jobBoardId);
    }

    @Override
    public void deleteByJobBoardId(JobBoardId jobBoardId) {
        jobBoardJpaRepository.deleteById(jobBoardId.value());
    }
}