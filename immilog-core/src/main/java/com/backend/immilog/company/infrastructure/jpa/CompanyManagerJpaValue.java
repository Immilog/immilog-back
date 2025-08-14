package com.backend.immilog.company.infrastructure.jpa;

import com.backend.immilog.company.domain.model.CompanyManager;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PROTECTED)
@Embeddable
public class CompanyManagerJpaValue {
    @Column(name = "company_country_id")
    private String companyCountryId;

    @Column(name = "company_region")
    private String companyRegion;

    @Column(name = "company_manager_user_id")
    private String companyManagerUserId;

    protected CompanyManagerJpaValue() {}

    protected CompanyManagerJpaValue(
            String companyCountryId,
            String companyRegion,
            String companyManagerUserId
    ) {
        this.companyCountryId = companyCountryId;
        this.companyRegion = companyRegion;
        this.companyManagerUserId = companyManagerUserId;
    }

    public static CompanyManagerJpaValue of(
            String companyCountryId,
            String companyRegion,
            String companyManagerUserId
    ) {
        return new CompanyManagerJpaValue(
                companyCountryId,
                companyRegion,
                companyManagerUserId
        );
    }

    public CompanyManager toDomain() {
        return CompanyManager.of(companyCountryId, companyRegion, companyManagerUserId);
    }
}
