package com.backend.immilog.country.presentation;

import com.backend.immilog.country.domain.Country;
import com.backend.immilog.country.domain.CountryStatus;

import java.time.LocalDateTime;
import java.util.List;

public record CountryResponse(
        String id,
        String nameKo,
        String nameEn,
        String continent,
        CountryStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CountryResponse from(Country country) {
        return new CountryResponse(
                country.id().value(),
                country.nameKo(),
                country.nameEn(),
                country.continent(),
                country.status(),
                country.createdAt(),
                country.updatedAt()
        );
    }

    public static List<CountryResponse> fromList(List<Country> countries) {
        return countries.stream().map(CountryResponse::from).toList();
    }
}