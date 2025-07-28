package com.backend.immilog.company.domain.model;

import com.backend.immilog.user.domain.model.enums.Country;

public record CompanyManager(
        Country country,
        String region,
        Long userSeq
) {
    public static CompanyManager of(
            Country country,
            String region,
            Long userSeq
    ) {
        validateParameters(country, region, userSeq);
        return new CompanyManager(country, region, userSeq);
    }

    private static void validateParameters(
            Country country,
            String region,
            Long userSeq
    ) {
        if (country == null) {
            throw new IllegalArgumentException("Country cannot be null");
        }
        if (region == null || region.trim().isEmpty()) {
            throw new IllegalArgumentException("Region cannot be null or empty");
        }
        if (userSeq == null || userSeq <= 0) {
            throw new IllegalArgumentException("UserSeq must be positive");
        }
    }

    public static CompanyManager createEmpty() {
        return new CompanyManager(null, null, null);
    }

    public CompanyManager withCountry(Country newCountry) {
        return new CompanyManager(newCountry, region, userSeq);
    }

    public CompanyManager withRegion(String newRegion) {
        return new CompanyManager(country, newRegion, userSeq);
    }

    public CompanyManager withUserSeq(Long newUserSeq) {
        return new CompanyManager(country, region, newUserSeq);
    }
}