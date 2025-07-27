package com.backend.immilog.user.application.result;

import com.backend.immilog.user.domain.model.enums.Country;
import com.backend.immilog.user.domain.model.enums.UserRole;
import com.backend.immilog.user.domain.model.user.User;
import com.backend.immilog.user.domain.model.user.UserStatus;

import java.sql.Date;

public record UserInfoResult(
        Long seq,
        String nickName,
        String email,
        String profileImage,
        Long reportedCount,
        Date reportedDate,
        Country country,
        Country interestCountry,
        String region,
        UserRole userRole,
        UserStatus userStatus
) {
    public static UserInfoResult from(User user) {
        return new UserInfoResult(
                user.getUserId().value(),
                user.getNickname(),
                user.getEmail(),
                user.getImageUrl(),
                0L,
                null,
                user.getCountry(),
                user.getProfile().interestCountry(),
                user.getRegion(),
                user.getUserRole(),
                user.getUserStatus()
        );
    }

    public static UserInfoResult from(
            Long seq,
            String nickName,
            String profileImage
    ) {
        return new UserInfoResult(
                seq,
                nickName,
                null,
                profileImage,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
