package com.backend.immilog.company.infrastructure.jpa;

import com.backend.immilog.company.domain.model.Company;
import com.backend.immilog.company.domain.model.Industry;
import com.backend.immilog.shared.enums.Country;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@Entity
public class CompanyJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @Embedded
    private CompanyManagerJpaValue manager;

    @Embedded
    private CompanyJpaMetaData companyData;

    protected CompanyJpaEntity() {}

    protected CompanyJpaEntity(
            Long seq,
            Industry industry,
            String companyName,
            String companyEmail,
            String companyPhone,
            String companyAddress,
            String companyHomepage,
            Country companyCountry,
            String companyRegion,
            String companyLogo,
            Long companyManagerUserSeq
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
                companyManagerUserSeq
        );
        this.seq = seq;
        this.manager = manager;
        this.companyData = companyData;
    }

    public static CompanyJpaEntity from(Company company) {
        return new CompanyJpaEntity(
                company.seq(),
                company.industry(),
                company.name(),
                company.email(),
                company.phone(),
                company.address(),
                company.homepage(),
                company.country(),
                company.region(),
                company.logo(),
                company.managerUserSeq()
        );
    }

    public Company toDomain() {
        return new Company(this.seq, this.manager.toDomain(), this.companyData.toDomain());
    }
}
