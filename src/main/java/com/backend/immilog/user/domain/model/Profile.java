package com.backend.immilog.user.domain.model;

import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;

public record Profile(
        String nickname,
        String imageUrl,
        String interestCountryId
) {
    public Profile {
        validateNickname(nickname);
        validateImageUrl(imageUrl);
        validateInterestCountry(interestCountryId);
    }

    public static Profile of(
            String nickname,
            String imageUrl,
            String interestCountryId
    ) {
        return new Profile(nickname, imageUrl, interestCountryId);
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

    private void validateInterestCountry(String interestCountryId) {
        if (interestCountryId == null || interestCountryId.trim().isEmpty()) {
            throw new UserException(UserErrorCode.INVALID_REGION);
        }
    }
}