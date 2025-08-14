package com.backend.immilog.company.application.dto;

import com.backend.immilog.company.domain.model.Industry;

public record CompanyRegisterCommand(
        Industry industry,
        String name,
        String email,
        String phone,
        String address,
        String homepage,
        String countryId,
        String region,
        String logo
) {
}