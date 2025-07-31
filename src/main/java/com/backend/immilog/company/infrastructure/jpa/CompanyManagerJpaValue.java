package com.backend.immilog.company.infrastructure.jpa;

import com.backend.immilog.company.domain.model.CompanyManager;
import com.backend.immilog.shared.enums.Country;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PROTECTED)
@Embeddable
public class CompanyManagerJpaValue {
    @Enumerated(EnumType.STRING)
    @Column(name = "company_country")
    private Country companyCountry;

    @Column(name = "company_region")
    private String companyRegion;

    @Column(name = "company_manager_user_id")
    private String companyManagerUserId;

    protected CompanyManagerJpaValue() {}

    protected CompanyManagerJpaValue(
            Country companyCountry,
            String companyRegion,
            String companyManagerUserId
    ) {
        this.companyCountry = companyCountry;
        this.companyRegion = companyRegion;
        this.companyManagerUserId = companyManagerUserId;
    }

    public static CompanyManagerJpaValue of(
            Country companyCountry,
            String companyRegion,
            String companyManagerUserId
    ) {
        return new CompanyManagerJpaValue(
                companyCountry,
                companyRegion,
                companyManagerUserId
        );
    }

    public CompanyManager toDomain() {
        return CompanyManager.of(companyCountry, companyRegion, companyManagerUserId);
    }
}
