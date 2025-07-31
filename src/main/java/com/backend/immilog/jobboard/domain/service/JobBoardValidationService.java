package com.backend.immilog.jobboard.domain.service;

import com.backend.immilog.jobboard.domain.model.*;
import org.springframework.stereotype.Service;

@Service
public class JobBoardValidationService {

    public void validateJobBoardCreation(
            String userId,
            JobBoardCompany company,
            JobTitle title,
            JobDescription description,
            JobRequirements requirements,
            ApplicationDeadline deadline
    ) {
        validateUserId(userId);
        validateCompany(company);
        validateTitle(title);
        validateDescription(description);
        validateRequirements(requirements);
        validateDeadline(deadline);
    }

    public void validateJobBoardUpdate(
            JobBoard existingJobBoard,
            JobTitle newTitle,
            JobDescription newDescription,
            JobRequirements newRequirements
    ) {
        if (!existingJobBoard.isActive()) {
            throw new IllegalStateException("Cannot update inactive job board");
        }
        
        if (existingJobBoard.isExpired()) {
            throw new IllegalStateException("Cannot update expired job board");
        }

        validateTitle(newTitle);
        validateDescription(newDescription);
        validateRequirements(newRequirements);
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
    }

    private void validateCompany(JobBoardCompany company) {
        if (company == null) {
            throw new IllegalArgumentException("Company information is required");
        }
    }

    private void validateTitle(JobTitle title) {
        if (title == null) {
            throw new IllegalArgumentException("Job title is required");
        }
    }

    private void validateDescription(JobDescription description) {
        if (description == null) {
            throw new IllegalArgumentException("Job description is required");
        }
    }

    private void validateRequirements(JobRequirements requirements) {
        if (requirements == null) {
            throw new IllegalArgumentException("Job requirements are required");
        }
    }

    private void validateDeadline(ApplicationDeadline deadline) {
        if (deadline == null) {
            throw new IllegalArgumentException("Application deadline is required");
        }
        
        if (deadline.isExpired()) {
            throw new IllegalArgumentException("Application deadline cannot be in the past");
        }
    }
}