package com.backend.immilog.user.presentation.response;

import com.backend.immilog.global.enums.GlobalCountry;
import lombok.Builder;

@Builder
public record LocationResponse(
        String country,
        String region
) {
    public static LocationResponse from(
            GlobalCountry country,
            String region
    ) {
        return LocationResponse.builder()
                .country(country.getCountryName())
                .region(region)
                .build();
    }
}
