package com.backend.immilog.user.application.command;

import com.backend.immilog.user.domain.enums.UserStatus;

public record UserInfoUpdateCommand(
        String nickName,
        String profileImage,
        String countryId,
        String interestCountryId,
        Double latitude,
        Double longitude,
        UserStatus status
) {
}
