package com.backend.immilog.user.application.dto.out;

import com.backend.immilog.user.domain.model.User;
import com.backend.immilog.user.presentation.payload.UserSignInPayload;

public record UserSignInResult(
        String userId,
        String email,
        String nickname,
        String accessToken,
        String refreshToken,
        String country,
        String interestCountry,
        String region,
        String userProfileUrl,
        Boolean isLocationMatch
) {
    public static UserSignInResult of(
            User user,
            String accessToken,
            String refreshToken,
            boolean isLocationMatch
    ) {
        return new UserSignInResult(
                user.getUserId().value(),
                user.getEmail(),
                user.getNickname(),
                accessToken == null ? "" : accessToken,
                refreshToken == null ? "" : refreshToken,
                user.getCountryId(),
                user.getInterestCountryId(),
                user.getRegion(),
                user.getImageUrl(),
                isLocationMatch
        );
    }

    public UserSignInPayload.UserSignInInformation toInfraDTO() {
        return new UserSignInPayload.UserSignInInformation(
                this.userId,
                this.email,
                this.nickname,
                this.accessToken,
                this.refreshToken,
                this.country,
                this.interestCountry,
                this.region,
                this.userProfileUrl,
                this.isLocationMatch
        );
    }
}

