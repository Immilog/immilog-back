package com.backend.immilog.jobboard.infrastructure.jpa;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.backend.immilog.jobboard.domain.model.*;
import com.backend.immilog.shared.enums.Country;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@DynamicUpdate
@Entity
@Table(name = "job_board")
public class JobBoardEntity {
    @Id
    @Column(name = "job_board_id")
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "company_location")
    private String companyLocation;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "location")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_type")
    private WorkType workType;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience")
    private Experience experience;

    @Enumerated(EnumType.STRING)
    @Column(name = "industry")
    private Industry industry;

    @Column(name = "salary", precision = 10, scale = 2)
    private BigDecimal salary;

    @Column(name = "salary_currency")
    private String salaryCurrency;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "requirements", columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "benefits", columnDefinition = "TEXT")
    private String benefits;

    @Column(name = "application_deadline")
    private LocalDate applicationDeadline;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "country")
    private Country country;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = NanoIdUtils.randomNanoId();
        }
    }

    protected JobBoardEntity() {}

    public JobBoardEntity(
            String id,
            String userId,
            String companyName,
            String companyLocation,
            String title,
            String location,
            WorkType workType,
            Experience experience,
            Industry industry,
            BigDecimal salary,
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
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.companyName = companyName;
        this.companyLocation = companyLocation;
        this.title = title;
        this.location = location;
        this.workType = workType;
        this.experience = experience;
        this.industry = industry;
        this.salary = salary;
        this.salaryCurrency = salaryCurrency;
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

    public static JobBoardEntity from(JobBoard jobBoard) {
        return new JobBoardEntity(
                jobBoard.id().value(),
                jobBoard.userId(),
                jobBoard.companyName(),
                jobBoard.company().location(),
                jobBoard.title().value(),
                jobBoard.location().value(),
                jobBoard.workType(),
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
                jobBoard.updatedAt()
        );
    }

    public JobBoard toDomain() {
        return JobBoard.restore(
                JobBoardId.of(id),
                userId,
                JobBoardCompany.of(companyName, companyLocation != null ? companyLocation : location, industry != null ? industry : Industry.IT),
                JobTitle.of(title),
                JobLocation.of(location),
                workType != null ? workType : WorkType.FULL_TIME,
                experience,
                industry,
                Salary.of(salary, salaryCurrency != null ? salaryCurrency : "KRW"),
                JobDescription.of(description),
                JobRequirements.of(requirements),
                JobBenefits.of(benefits),
                ApplicationDeadline.of(applicationDeadline),
                ContactEmail.of(contactEmail),
                isActive,
                viewCount,
                country,
                createdAt,
                updatedAt
        );
    }
}