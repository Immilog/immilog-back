package com.backend.immilog.user.application.result;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.user.domain.model.User;
import com.backend.immilog.user.domain.model.enums.UserCountry;
import com.backend.immilog.user.domain.model.enums.UserStatus;
import lombok.Builder;

import java.sql.Date;

@Builder
public record UserInfoResult(
        Long seq,
        String nickName,
        String email,
        String profileImage,
        Long reportedCount,
        Date reportedDate,
        UserCountry country,
        UserCountry interestCountry,
        String region,
        UserRole userRole,
        UserStatus userStatus
) {
    public static UserInfoResult from(
            User user
    ) {
        return UserInfoResult.builder()
                .seq(user.seq())
                .nickName(user.nickName())
                .email(user.email())
                .profileImage(user.imageUrl())
                .reportedCount(user.reportInfo().getReportedCount())
                .reportedDate(user.reportInfo().getReportedDate())
                .country(user.location().getCountry())
                .region(user.location().getRegion())
                .interestCountry(user.interestCountry())
                .userRole(user.userRole())
                .userStatus(user.userStatus())
                .build();
    }

    public static UserInfoResult from(
            Long seq,
            String nickName,
            String profileImage
    ) {
        return UserInfoResult.builder()
                .seq(seq)
                .nickName(nickName)
                .profileImage(profileImage)
                .build();
    }
}
