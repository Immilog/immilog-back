package com.backend.immilog.user.domain.model;

import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;

public record Location(
        String countryId,
        String region
) {
    public Location {
        validateCountry(countryId);
        validateRegion(region);
    }

    public static Location of(
            String countryId,
            String region
    ) {
        return new Location(countryId, region);
    }

    private void validateCountry(String countryId) {
        if (countryId == null || countryId.trim().isEmpty()) {
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
