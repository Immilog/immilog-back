package com.backend.immilog.jobboard.domain.service;

import com.backend.immilog.jobboard.domain.model.*;
import com.backend.immilog.jobboard.domain.repositories.JobBoardRepository;
import org.springframework.stereotype.Service;

@Service
public class JobBoardDomainService {
    private final JobBoardRepository jobBoardRepository;
    private final JobBoardValidationService validationService;

    public JobBoardDomainService(
            JobBoardRepository jobBoardRepository,
            JobBoardValidationService validationService
    ) {
        this.jobBoardRepository = jobBoardRepository;
        this.validationService = validationService;
    }

    public JobBoard createJobBoard(
            String userId,
            JobBoardCompany company,
            JobTitle title,
            JobLocation location,
            WorkType workType,
            Experience experience,
            Industry industry,
            Salary salary,
            JobDescription description,
            JobRequirements requirements,
            JobBenefits benefits,
            ApplicationDeadline applicationDeadline,
            ContactEmail contactEmail,
            String countryId
    ) {
        validationService.validateJobBoardCreation(
                userId,
                company,
                title,
                description,
                requirements,
                applicationDeadline
        );

        JobBoard jobBoard = JobBoard.create(
                userId,
                company,
                title,
                location,
                workType,
                experience,
                industry,
                salary,
                description,
                requirements,
                benefits,
                applicationDeadline,
                contactEmail,
                countryId
        );

        return jobBoardRepository.save(jobBoard);
    }

    public void deactivateExpiredJobBoards() {
        // TODO : 나중에 스케줄링 으로 정리
    }

    public boolean canUserApplyToJobBoard(
            String userId,
            JobBoard jobBoard
    ) {
        if (userId.equals(jobBoard.userId())) {
            return false;
        }

        return jobBoard.canApply();
    }

    public void recordJobBoardView(
            JobBoardId jobBoardId,
            String viewerUserId
    ) {
        JobBoard jobBoard = jobBoardRepository.findById(jobBoardId.value())
                .orElseThrow(() -> new IllegalArgumentException("Job board not found"));

        if (!jobBoard.userId().equals(viewerUserId)) {
            jobBoard.incrementViewCount();
            jobBoardRepository.save(jobBoard);
        }
    }
}