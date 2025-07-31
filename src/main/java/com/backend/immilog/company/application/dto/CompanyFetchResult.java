package com.backend.immilog.company.application.dto;

import com.backend.immilog.company.domain.model.Company;
import com.backend.immilog.company.domain.model.CompanyManager;
import com.backend.immilog.company.domain.model.CompanyMetaData;
import com.backend.immilog.company.domain.model.Industry;
import com.backend.immilog.company.presentation.CompanyPayload;
import com.backend.immilog.shared.enums.Country;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

public record CompanyFetchResult(
        @Schema(description = "회사 식별자") String id,
        @Schema(description = "산업 분야") Industry industry,
        @Schema(description = "회사명") String companyName,
        @Schema(description = "회사 이메일") String companyEmail,
        @Schema(description = "회사 전화번호") String companyPhone,
        @Schema(description = "회사 주소") String companyAddress,
        @Schema(description = "회사 홈페이지") String companyHomepage,
        @Schema(description = "회사 국가") Country companyCountry,
        @Schema(description = "회사 지역") String companyRegion,
        @Schema(description = "회사 로고") String companyLogo,
        @Schema(description = "회사 관리자 식별자") String companyManagerUserId
) {
    public static CompanyFetchResult from(Company company) {
        return new CompanyFetchResult(
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
        return new Company(
                id,
                CompanyManager.of(
                        companyCountry,
                        companyRegion,
                        companyManagerUserId
                ),
                CompanyMetaData.of(
                        industry,
                        companyName,
                        companyEmail,
                        companyPhone,
                        companyAddress,
                        companyHomepage,
                        companyLogo
                )
        );
    }

    public static CompanyFetchResult createEmpty() {
        return new CompanyFetchResult(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public CompanyPayload.CompanyResponse toResponse() {
        if (this.id() == null) {
            return new CompanyPayload.CompanyResponse(HttpStatus.NO_CONTENT.value(), "회사 정보가 비어있습니다.", null);
        } else {
            return new CompanyPayload.CompanyResponse(HttpStatus.OK.value(), "success", this);
        }
    }
}