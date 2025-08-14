package com.backend.immilog.company.domain.model;

public record CompanyManager(
        String countryId,
        String region,
        String userId
) {
    public static CompanyManager of(
            String countryId,
            String region,
            String userId
    ) {
        validateParameters(countryId, region, userId);
        return new CompanyManager(countryId, region, userId);
    }

    private static void validateParameters(
            String countryId,
            String region,
            String userId
    ) {
        if (countryId == null || countryId.trim().isEmpty()) {
            throw new IllegalArgumentException("CountryId cannot be null or empty");
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

    public CompanyManager withCountry(String newCountryId) {
        return new CompanyManager(newCountryId, region, userId);
    }

    public CompanyManager withRegion(String newRegion) {
        return new CompanyManager(countryId, newRegion, userId);
    }

    public CompanyManager withUserId(String newUserId) {
        return new CompanyManager(countryId, region, newUserId);
    }
}