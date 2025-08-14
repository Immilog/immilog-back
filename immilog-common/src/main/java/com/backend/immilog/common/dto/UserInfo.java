package com.backend.immilog.common.dto;

public record UserInfo(
        String userId,
        String nickname,
        String email,
        String profileImageUrl,
        String countryId
) {
    public static UserInfo of(
            String userId,
            String nickname, 
            String email,
            String profileImageUrl,
            String countryId
    ) {
        return new UserInfo(userId, nickname, email, profileImageUrl, countryId);
    }
}