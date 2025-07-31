package com.backend.immilog.jobboard.application.dto;

import com.backend.immilog.jobboard.domain.model.*;
import com.backend.immilog.shared.enums.Country;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record JobBoardResult(
        String id,
        String userId,
        String companyName,
        String companyLocation,
        String title,
        String location,
        String workType,
        Experience experience,
        Industry industry,
        BigDecimal salaryAmount,
        String salaryCurrency,
        String description,
        String requirements,
        String benefits,
        LocalDate applicationDeadline,
        String contactEmail,
        Boolean isActive,
        Long viewCount,
        Country country,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean canApply,
        Boolean isExpired
) {
    public static JobBoardResult from(JobBoard jobBoard) {
        return new JobBoardResult(
                jobBoard.id().value(),
                jobBoard.userId(),
                jobBoard.companyName(),
                jobBoard.company().location(), // Company location
                jobBoard.title().value(),
                jobBoard.location().value(),
                jobBoard.workType().name(),
                jobBoard.experience(),
                jobBoard.industry(),
                jobBoard.salary().amount(),
                jobBoard.salary().currency(),
                jobBoard.description().value(),
                jobBoard.requirements().value(),
                jobBoard.benefits().value(),
                jobBoard.applicationDeadline().value(),
                jobBoard.contactEmail().value(),
                jobBoard.isActive(),
                jobBoard.viewCount(),
                jobBoard.country(),
                jobBoard.createdAt(),
                jobBoard.updatedAt(),
                jobBoard.canApply(),
                jobBoard.isExpired()
        );
    }
}