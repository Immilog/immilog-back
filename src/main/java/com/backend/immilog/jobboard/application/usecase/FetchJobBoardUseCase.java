package com.backend.immilog.jobboard.application.usecase;

import com.backend.immilog.jobboard.application.dto.JobBoardResult;
import com.backend.immilog.jobboard.application.services.JobBoardQueryService;
import com.backend.immilog.shared.enums.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Objects;

public interface FetchJobBoardUseCase {
    Page<JobBoardResult> getJobBoards(
            Country country,
            Integer page
    );

    JobBoardResult getJobBoardDetail(String jobBoardId);

    @Service
    class FetcherJobBoard implements FetchJobBoardUseCase {
        private final JobBoardQueryService jobBoardQueryService;

        public FetcherJobBoard(JobBoardQueryService jobBoardQueryService) {
            this.jobBoardQueryService = jobBoardQueryService;
        }

        @Override
        public Page<JobBoardResult> getJobBoards(
                Country country,
                Integer page
        ) {
            var pageable = PageRequest.of(Objects.requireNonNullElse(page, 0), 10);
            return jobBoardQueryService.getJobBoards(country, pageable);
        }

        @Override
        public JobBoardResult getJobBoardDetail(String jobBoardId) {
            return jobBoardQueryService.getJobBoardDetail(jobBoardId);
        }
    }
}