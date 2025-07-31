package com.backend.immilog.jobboard.domain.model;

import com.backend.immilog.shared.enums.Country;

import java.time.LocalDateTime;

public class JobBoard {
    private final JobBoardId id;
    private final String userId;
    private final JobBoardCompany company;
    private final JobTitle title;
    private final JobLocation location;
    private final WorkType workType;
    private final Experience experience;
    private final Industry industry;
    private final Salary salary;
    private final JobDescription description;
    private final JobRequirements requirements;
    private final JobBenefits benefits;
    private final ApplicationDeadline applicationDeadline;
    private final ContactEmail contactEmail;
    private Boolean isActive;
    private Long viewCount;
    private final Country country;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public JobBoard(
            JobBoardId id,
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
            Boolean isActive,
            Long viewCount,
            Country country,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.company = company;
        this.title = title;
        this.location = location;
        this.workType = workType;
        this.experience = experience;
        this.industry = industry;
        this.salary = salary;
        this.description = description;
        this.requirements = requirements;
        this.benefits = benefits;
        this.applicationDeadline = applicationDeadline;
        this.contactEmail = contactEmail;
        this.isActive = isActive;
        this.viewCount = viewCount;
        this.country = country;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static JobBoard create(
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
            Country country
    ) {
        validateJobBoardCreation(userId, company, title, description, requirements);

        return new JobBoard(
                JobBoardId.generate(),
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
                true,
                0L,
                country,
                LocalDateTime.now(),
                null
        );
    }

    public static JobBoard restore(
            JobBoardId id,
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
            Boolean isActive,
            Long viewCount,
            Country country,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new JobBoard(
                id,
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
                isActive,
                viewCount,
                country,
                createdAt,
                updatedAt
        );
    }

    private static void validateJobBoardCreation(
            String userId,
            JobBoardCompany company,
            JobTitle title,
            JobDescription description,
            JobRequirements requirements
    ) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (company == null) {
            throw new IllegalArgumentException("Company information is required");
        }
        if (title == null) {
            throw new IllegalArgumentException("Job title is required");
        }
        if (description == null) {
            throw new IllegalArgumentException("Job description is required");
        }
        if (requirements == null) {
            throw new IllegalArgumentException("Job requirements are required");
        }
    }

    public void deactivate() {
        if (!this.isActive) {
            throw new IllegalStateException("Job board is already inactive");
        }
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (this.isActive) {
            throw new IllegalStateException("Job board is already active");
        }
        if (applicationDeadline.isExpired()) {
            throw new IllegalStateException("Cannot activate job board with expired deadline");
        }
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementViewCount() {
        this.viewCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return applicationDeadline.isExpired();
    }

    public boolean canApply() {
        return isActive && !isExpired();
    }

    public String companyName() {
        return this.company.name();
    }

    public JobBoardCompany company() {
        return this.company;
    }

    public JobBoardId id() {return id;}

    public String userId() {return userId;}

    public JobTitle title() {return title;}

    public JobLocation location() {return location;}

    public WorkType workType() {return workType;}

    public Experience experience() {return experience;}

    public Industry industry() {return industry;}

    public Salary salary() {return salary;}

    public JobDescription description() {return description;}

    public JobRequirements requirements() {return requirements;}

    public JobBenefits benefits() {return benefits;}

    public ApplicationDeadline applicationDeadline() {return applicationDeadline;}

    public ContactEmail contactEmail() {return contactEmail;}

    public Boolean isActive() {return isActive;}

    public Long viewCount() {return viewCount;}

    public Country country() {return country;}

    public LocalDateTime createdAt() {return createdAt;}

    public LocalDateTime updatedAt() {return updatedAt;}
}