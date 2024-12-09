package com.backend.immilog.user.presentation.request;

import com.backend.immilog.user.application.command.CompanyRegisterCommand;
import com.backend.immilog.user.domain.enums.Industry;
import com.backend.immilog.user.domain.enums.UserCountry;
import lombok.Builder;

@Builder
public record CompanyRegisterRequest(
        Industry industry,
        String companyName,
        String companyEmail,
        String companyPhone,
        String companyAddress,
        String companyHomepage,
        UserCountry companyCountry,
        String companyRegion,
        String companyLogo
) {
    public CompanyRegisterCommand toCommand() {
        return CompanyRegisterCommand.builder()
                .industry(industry())
                .companyName(companyName())
                .companyEmail(companyEmail())
                .companyPhone(companyPhone())
                .companyAddress(companyAddress())
                .companyHomepage(companyHomepage())
                .companyCountry(companyCountry())
                .companyRegion(companyRegion())
                .companyLogo(companyLogo())
                .build();
    }
}
