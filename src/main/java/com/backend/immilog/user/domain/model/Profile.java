package com.backend.immilog.user.domain.model;

import com.backend.immilog.shared.enums.Country;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;

public record Profile(
        String nickname,
        String imageUrl,
        Country interestCountry
) {
    public Profile {
        validateNickname(nickname);
        validateImageUrl(imageUrl);
        validateInterestCountry(interestCountry);
    }

    public static Profile of(
            String nickname,
            String imageUrl,
            Country interestCountry
    ) {
        return new Profile(nickname, imageUrl, interestCountry);
    }

    private void validateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new UserException(UserErrorCode.INVALID_NICKNAME);
        }
        if (nickname.length() > 20) {
            throw new UserException(UserErrorCode.INVALID_NICKNAME);
        }
    }

    private void validateImageUrl(String imageUrl) {
        if (imageUrl != null && imageUrl.trim().isEmpty()) {
            imageUrl = null;
        }
    }

    private void validateInterestCountry(Country interestCountry) {
        if (interestCountry == null) {
            throw new UserException(UserErrorCode.INVALID_REGION);
        }
    }
}
