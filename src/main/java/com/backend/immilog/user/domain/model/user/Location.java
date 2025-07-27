package com.backend.immilog.user.domain.model.user;

import com.backend.immilog.user.domain.model.enums.Country;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;

public record Location(
        Country country,
        String region
) {
    public Location {
        validateCountry(country);
        validateRegion(region);
    }

    public static Location of(
            Country country,
            String region
    ) {
        return new Location(country, region);
    }

    private void validateCountry(Country country) {
        if (country == null) {
            throw new UserException(UserErrorCode.INVALID_REGION);
        }
    }

    private void validateRegion(String region) {
        if (region == null || region.trim().isEmpty()) {
            throw new UserException(UserErrorCode.INVALID_REGION);
        }
        if (region.length() > 100) {
            throw new UserException(UserErrorCode.INVALID_REGION);
        }
    }
}
