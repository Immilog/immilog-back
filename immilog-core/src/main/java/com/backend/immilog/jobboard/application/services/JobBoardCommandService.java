package com.backend.immilog.jobboard.application.services;

import com.backend.immilog.jobboard.domain.model.*;
import com.backend.immilog.jobboard.domain.repositories.JobBoardRepository;
import com.backend.immilog.jobboard.domain.service.JobBoardDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobBoardCommandService {
    private final JobBoardRepository jobBoardRepository;
    private final JobBoardDomainService jobBoardDomainService;

    public JobBoardCommandService(
            JobBoardRepository jobBoardRepository,
            JobBoardDomainService jobBoardDomainService
    ) {
        this.jobBoardRepository = jobBoardRepository;
        this.jobBoardDomainService = jobBoardDomainService;
    }

    @Transactional
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
        return jobBoardDomainService.createJobBoard(
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
    }

    @Transactional
    public void deactivateJobBoard(String jobBoardId) {
        var jobBoard = jobBoardRepository.findById(jobBoardId)
                .orElseThrow(() -> new RuntimeException("JobBoard not found"));
        jobBoard.deactivate();
        jobBoardRepository.save(jobBoard);
    }

    @Transactional
    public void activateJobBoard(String jobBoardId) {
        var jobBoard = jobBoardRepository.findById(jobBoardId)
                .orElseThrow(() -> new RuntimeException("JobBoard not found"));
        jobBoard.activate();
        jobBoardRepository.save(jobBoard);
    }

    @Transactional
    public void recordJobBoardView(
            String jobBoardId,
            String viewerUserId
    ) {
        jobBoardDomainService.recordJobBoardView(
                JobBoardId.of(jobBoardId),
                viewerUserId
        );
    }

    @Transactional
    public void deleteJobBoard(String jobBoardId) {
        jobBoardRepository.deleteById(jobBoardId);
    }
}