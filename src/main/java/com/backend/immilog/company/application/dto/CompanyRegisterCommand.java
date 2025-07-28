package com.backend.immilog.company.application.dto;

import com.backend.immilog.company.domain.model.Industry;
import com.backend.immilog.user.domain.model.enums.Country;

public record CompanyRegisterCommand(
        Industry industry,
        String name,
        String email,
        String phone,
        String address,
        String homepage,
        Country country,
        String region,
        String logo
) {
}