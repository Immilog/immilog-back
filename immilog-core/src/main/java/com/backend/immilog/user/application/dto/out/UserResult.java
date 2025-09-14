package com.backend.immilog.user.application.dto.out;

import com.backend.immilog.user.domain.model.User;

public record UserResult(
        String userId,
        String email,
        String nickname,
        String profileImage,
        String region,
        String countryId
) {
    public static UserResult from(User user) {
        return new UserResult(
                user.getUserId().value(),
                user.getEmail(),
                user.getNickname(),
                user.getImageUrl(),
                user.getRegion(),
                user.getCountryId()
        );
    }
}