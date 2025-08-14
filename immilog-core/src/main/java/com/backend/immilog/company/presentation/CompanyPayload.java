package com.backend.immilog.company.presentation;

import com.backend.immilog.company.application.dto.CompanyRegisterCommand;
import com.backend.immilog.company.domain.model.Industry;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

public record CompanyPayload() {
    public record CompanyRegisterRequest(
            @Schema(description = "산업 분야") Industry industry,
            @Schema(description = "회사명") String companyName,
            @Schema(description = "회사 이메일") String companyEmail,
            @Schema(description = "회사 전화번호") String companyPhone,
            @Schema(description = "회사 주소") String companyAddress,
            @Schema(description = "회사 홈페이지") String companyHomepage,
            @Schema(description = "회사 국가") String companyCountryId,
            @Schema(description = "회사 지역") String companyRegion,
            @Schema(description = "회사 로고") String companyLogo
    ) {
        public CompanyRegisterCommand toCommand() {
            return new CompanyRegisterCommand(
                    this.industry,
                    this.companyName,
                    this.companyEmail,
                    this.companyPhone,
                    this.companyAddress,
                    this.companyHomepage,
                    this.companyCountryId,
                    this.companyRegion,
                    this.companyLogo
            );
        }
    }

    public record CompanyResponse(
            @Schema(description = "상태 코드", example = "200") Integer status,
            @Schema(description = "메시지", example = "success") String message,
            @Schema(description = "회사 정보", example = "company info") CompanyInformation data
    ) {

        public static CompanyResponse from(CompanyInformation information) {
            if (information.id() == null) {
                return new CompanyPayload.CompanyResponse(HttpStatus.NO_CONTENT.value(), "회사 정보가 비어있습니다.", null);
            } else {
                return new CompanyPayload.CompanyResponse(HttpStatus.OK.value(), "success", information);
            }
        }
    }

    public record CompanyInformation(
            @Schema(description = "회사 식별자") String id,
            @Schema(description = "산업 분야") Industry industry,
            @Schema(description = "회사명") String companyName,
            @Schema(description = "회사 이메일") String companyEmail,
            @Schema(description = "회사 전화번호") String companyPhone,
            @Schema(description = "회사 주소") String companyAddress,
            @Schema(description = "회사 홈페이지") String companyHomepage,
            @Schema(description = "회사 국가") String companyCountryId,
            @Schema(description = "회사 지역") String companyRegion,
            @Schema(description = "회사 로고") String companyLogo,
            @Schema(description = "회사 관리자 식별자") String companyManagerUserId
    ) {
    }
}