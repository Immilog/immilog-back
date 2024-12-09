package com.backend.immilog.user.domain.model;

import com.backend.immilog.user.application.command.CompanyRegisterCommand;
import com.backend.immilog.user.domain.enums.Industry;
import com.backend.immilog.user.domain.enums.UserCountry;
import lombok.Builder;

@Builder
public record Company(
        Long seq,
        Industry industry,
        String companyName,
        String companyEmail,
        String companyPhone,
        String companyAddress,
        String companyHomepage,
        UserCountry companyCountry,
        String companyRegion,
        String companyLogo,
        Long companyManagerUserSeq
) {
    public static Company of(
            Long userSeq,
            CompanyRegisterCommand request
    ) {
        return Company.builder()
                .industry(request.industry())
                .companyName(request.companyName())
                .companyEmail(request.companyEmail())
                .companyPhone(request.companyPhone())
                .companyAddress(request.companyAddress())
                .companyHomepage(request.companyHomepage())
                .companyCountry(request.companyCountry())
                .companyRegion(request.companyRegion())
                .companyLogo(request.companyLogo())
                .companyManagerUserSeq(userSeq)
                .build();
    }

    public Company copyWithNewIndustry(
            Industry industry
    ) {
        return buildCompany(industry, CompanyField.INDUSTRY);
    }

    public Company copyWithNewCompanyName(
            String companyName
    ) {
        return buildCompany(companyName, CompanyField.COMPANY_NAME);
    }

    public Company copyWithNewCompanyPhone(
            String companyPhone
    ) {
        return buildCompany(companyPhone, CompanyField.COMPANY_PHONE);
    }

    public Company copyWithNewCompanyLogo(
            String companyLogo
    ) {
        return buildCompany(companyLogo, CompanyField.COMPANY_LOGO);
    }

    public Company copyWithNewCompanyHomepage(
            String companyHomepage
    ) {
        return buildCompany(companyHomepage, CompanyField.COMPANY_HOMEPAGE);
    }

    public Company copyWithNewCompanyEmail(
            String companyEmail
    ) {
        return buildCompany(companyEmail, CompanyField.COMPANY_EMAIL);
    }

    public Company copyWithNewCompanyCountry(
            UserCountry country
    ) {
        return buildCompany(country, CompanyField.COMPANY_COUNTRY);
    }

    public Company copyWithNewCompanyAddress(
            String address
    ) {
        return buildCompany(address, CompanyField.COMPANY_ADDRESS);
    }

    public Company copyWithNewCompanyRegion(
            String region
    ) {
        return buildCompany(region, CompanyField.COMPANY_REGION);
    }

    private <T> Company buildCompany(
            T item,
            CompanyField field
    ) {
        return Company.builder()
                .industry(field == CompanyField.INDUSTRY ? (Industry) item : this.industry())
                .companyName(field == CompanyField.COMPANY_NAME ? (String) item : this.companyName())
                .companyEmail(field == CompanyField.COMPANY_EMAIL ? (String) item : this.companyEmail())
                .companyPhone(field == CompanyField.COMPANY_PHONE ? (String) item : this.companyPhone())
                .companyAddress(field == CompanyField.COMPANY_ADDRESS ? (String) item : this.companyAddress())
                .companyHomepage(field == CompanyField.COMPANY_HOMEPAGE ? (String) item : this.companyHomepage())
                .companyCountry(field == CompanyField.COMPANY_COUNTRY ? (UserCountry) item : this.companyCountry())
                .companyRegion(field == CompanyField.COMPANY_REGION ? (String) item : this.companyRegion())
                .companyLogo(field == CompanyField.COMPANY_LOGO ? (String) item : this.companyLogo())
                .companyManagerUserSeq(this.companyManagerUserSeq())
                .build();
    }

    private enum CompanyField {
        INDUSTRY,
        COMPANY_NAME,
        COMPANY_EMAIL,
        COMPANY_PHONE,
        COMPANY_ADDRESS,
        COMPANY_HOMEPAGE,
        COMPANY_COUNTRY,
        COMPANY_REGION,
        COMPANY_LOGO
    }
}


