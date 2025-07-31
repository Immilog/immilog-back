package com.backend.immilog.company.infrastructure.jpa;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.backend.immilog.company.domain.model.Company;
import com.backend.immilog.company.domain.model.Industry;
import com.backend.immilog.shared.enums.Country;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@Entity
@Table(name = "company")
public class CompanyJpaEntity {
    @Id
    @Column(name = "company_id")
    private String id;

    @Embedded
    private CompanyManagerJpaValue manager;

    @Embedded
    private CompanyJpaMetaData companyData;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = NanoIdUtils.randomNanoId();
        }
    }

    protected CompanyJpaEntity() {}

    protected CompanyJpaEntity(
            String id,
            Industry industry,
            String companyName,
            String companyEmail,
            String companyPhone,
            String companyAddress,
            String companyHomepage,
            Country companyCountry,
            String companyRegion,
            String companyLogo,
            String companyManagerUserId
    ) {
        CompanyJpaMetaData companyData = CompanyJpaMetaData.of(
                industry,
                companyName,
                companyEmail,
                companyPhone,
                companyAddress,
                companyHomepage,
                companyLogo
        );
        CompanyManagerJpaValue manager = CompanyManagerJpaValue.of(
                companyCountry,
                companyRegion,
                companyManagerUserId
        );
        this.id = id;
        this.manager = manager;
        this.companyData = companyData;
    }

    public static CompanyJpaEntity from(Company company) {
        return new CompanyJpaEntity(
                company.id(),
                company.industry(),
                company.name(),
                company.email(),
                company.phone(),
                company.address(),
                company.homepage(),
                company.country(),
                company.region(),
                company.logo(),
                company.managerUserId()
        );
    }

    public Company toDomain() {
        return new Company(this.id, this.manager.toDomain(), this.companyData.toDomain());
    }
}
