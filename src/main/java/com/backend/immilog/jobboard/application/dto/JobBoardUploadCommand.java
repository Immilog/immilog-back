package com.backend.immilog.jobboard.application.dto;

import com.backend.immilog.jobboard.domain.model.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "구인게시판 생성 요청 서비스 DTO")
public record JobBoardUploadCommand(
        String userId,
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
    public JobTitle toJobTitle() {
        return JobTitle.of(title);
    }

    public JobLocation toJobLocation() {
        return JobLocation.of(location);
    }

    public JobDescription toJobDescription() {
        return JobDescription.of(description);
    }

    public JobRequirements toJobRequirements() {
        return JobRequirements.of(requirements);
    }

    public JobBenefits toJobBenefits() {
        return JobBenefits.of(benefits);
    }

    public ApplicationDeadline toApplicationDeadline() {
        return ApplicationDeadline.of(applicationDeadline);
    }

    public ContactEmail toContactEmail() {
        return ContactEmail.of(contactEmail);
    }

    public Salary toSalary() {
        return Salary.of(salary, salaryCurrency != null ? salaryCurrency : "KRW");
    }

    public JobBoardCompany toJobBoardCompany() {
        return JobBoardCompany.of(companyName, companyLocation, industry);
    }
}