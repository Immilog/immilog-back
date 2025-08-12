package com.backend.immilog.jobboard.presentation.payload;

import com.backend.immilog.jobboard.application.dto.JobBoardUploadCommand;
import com.backend.immilog.jobboard.domain.model.Experience;
import com.backend.immilog.jobboard.domain.model.Industry;
import com.backend.immilog.jobboard.domain.model.WorkType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record JobBoardCreateRequest(
        String companyName,
        String companyLocation,
        String title,
        String location,
        WorkType workType,
        Experience experience,
        BigDecimal salary,
        String salaryCurrency,
        String description,
        String requirements,
        String benefits,
        LocalDate applicationDeadline,
        String contactEmail,
        String countryId,
        Industry industry
) {
    public JobBoardUploadCommand toCommand(String userId) {
        return new JobBoardUploadCommand(
                userId,
                companyName,
                companyLocation,
                title,
                location,
                workType,
                experience,
                salary,
                salaryCurrency,
                description,
                requirements,
                benefits,
                applicationDeadline,
                contactEmail,
                countryId,
                industry
        );
    }
}