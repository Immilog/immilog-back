package com.backend.immilog.jobboard.infrastructure.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JobBoardJpaRepository extends JpaRepository<JobBoardEntity, String> {
    Page<JobBoardEntity> findByCountryIdOrderByCreatedAtDesc(
            String countryId,
            Pageable pageable
    );

    List<JobBoardEntity> findByUserIdOrderByCreatedAtDesc(String userId);

    List<JobBoardEntity> findByApplicationDeadlineBeforeAndIsActiveTrue(LocalDate date);
}