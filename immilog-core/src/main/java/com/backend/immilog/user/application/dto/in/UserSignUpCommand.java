package com.backend.immilog.user.application.dto.in;

public record UserSignUpCommand(
        String nickName,
        String password,
        String email,
        String country,
        String interestCountry,
        String region,
        String profileImage
) {
}
