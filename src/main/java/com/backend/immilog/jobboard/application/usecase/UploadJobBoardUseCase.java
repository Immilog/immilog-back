package com.backend.immilog.jobboard.application.usecase;

import com.backend.immilog.jobboard.application.dto.JobBoardResult;
import com.backend.immilog.jobboard.application.dto.JobBoardUploadCommand;
import com.backend.immilog.jobboard.application.services.JobBoardCommandService;
import org.springframework.stereotype.Service;

public interface UploadJobBoardUseCase {
    JobBoardResult uploadJobBoard(JobBoardUploadCommand command);

    @Service
    class UploaderJobBoard implements UploadJobBoardUseCase {
        private final JobBoardCommandService jobBoardCommandService;

        public UploaderJobBoard(JobBoardCommandService jobBoardCommandService) {
            this.jobBoardCommandService = jobBoardCommandService;
        }

        @Override
        public JobBoardResult uploadJobBoard(JobBoardUploadCommand command) {
            var savedJobBoard = jobBoardCommandService.createJobBoard(
                    command.userId(),
                    command.toJobBoardCompany(),
                    command.toJobTitle(),
                    command.toJobLocation(),
                    command.workType(),
                    command.experience(),
                    command.industry(),
                    command.toSalary(),
                    command.toJobDescription(),
                    command.toJobRequirements(),
                    command.toJobBenefits(),
                    command.toApplicationDeadline(),
                    command.toContactEmail(),
                    command.country()
            );
            return JobBoardResult.from(savedJobBoard);
        }
    }
}