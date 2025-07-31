package com.backend.immilog.jobboard.application.services;

import com.backend.immilog.jobboard.application.dto.JobBoardResult;
import com.backend.immilog.jobboard.domain.model.JobBoard;
import com.backend.immilog.jobboard.domain.repositories.JobBoardRepository;
import com.backend.immilog.shared.enums.Country;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class JobBoardQueryService {
    private final JobBoardRepository jobBoardRepository;

    public JobBoardQueryService(JobBoardRepository jobBoardRepository) {
        this.jobBoardRepository = jobBoardRepository;
    }

    @Transactional(readOnly = true)
    public Page<JobBoardResult> getJobBoards(
            Country country,
            Pageable pageable
    ) {
        var jobBoards = jobBoardRepository.findJobBoards(country, pageable);
        return jobBoards.map(this::convertToJobBoardResult);
    }

    @Transactional(readOnly = true)
    public JobBoardResult getJobBoardDetail(String jobBoardId) {
        var jobBoard = jobBoardRepository.findById(jobBoardId)
                .orElseThrow(() -> new RuntimeException("JobBoard not found"));
        return convertToJobBoardResult(jobBoard);
    }

    private JobBoardResult convertToJobBoardResult(JobBoard jobBoard) {
        return JobBoardResult.from(jobBoard);
    }
}