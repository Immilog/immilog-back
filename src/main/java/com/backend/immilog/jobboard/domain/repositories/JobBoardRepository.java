package com.backend.immilog.jobboard.domain.repositories;

import com.backend.immilog.jobboard.domain.model.JobBoard;
import com.backend.immilog.jobboard.domain.model.JobBoardId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JobBoardRepository {
    Page<JobBoard> findJobBoards(
            String countryId,
            Pageable pageable
    );

    Optional<JobBoard> findById(String jobBoardId);

    Optional<JobBoard> findByJobBoardId(JobBoardId jobBoardId);

    List<JobBoard> findByUserId(String userId);

    List<JobBoard> findExpiredJobBoards(LocalDate currentDate);

    List<JobBoard> findActiveJobBoardsByDeadlineBefore(LocalDate date);

    JobBoard save(JobBoard jobBoard);

    void deleteById(String jobBoardId);

    void deleteByJobBoardId(JobBoardId jobBoardId);
}