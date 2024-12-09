package com.backend.immilog.user.application.command;

import com.backend.immilog.user.domain.enums.UserCountry;
import com.backend.immilog.user.domain.enums.UserStatus;
import lombok.Builder;

@Builder
public record UserInfoUpdateCommand(
        String nickName,
        String profileImage,
        UserCountry country,
        UserCountry interestCountry,
        Double latitude,
        Double longitude,
        UserStatus status
) {
}
