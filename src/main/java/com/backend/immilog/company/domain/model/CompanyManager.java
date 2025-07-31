package com.backend.immilog.company.domain.model;

import com.backend.immilog.shared.enums.Country;

public record CompanyManager(
        Country country,
        String region,
        String userId
) {
    public static CompanyManager of(
            Country country,
            String region,
            String userId
    ) {
        validateParameters(country, region, userId);
        return new CompanyManager(country, region, userId);
    }

    private static void validateParameters(
            Country country,
            String region,
            String userId
    ) {
        if (country == null) {
            throw new IllegalArgumentException("Country cannot be null");
        }
        if (region == null || region.trim().isEmpty()) {
            throw new IllegalArgumentException("Region cannot be null or empty");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("UserId must be not null or blank");
        }
    }

    public static CompanyManager createEmpty() {
        return new CompanyManager(null, null, null);
    }

    public CompanyManager withCountry(Country newCountry) {
        return new CompanyManager(newCountry, region, userId);
    }

    public CompanyManager withRegion(String newRegion) {
        return new CompanyManager(country, newRegion, userId);
    }

    public CompanyManager withUserId(String newUserId) {
        return new CompanyManager(country, region, newUserId);
    }
}