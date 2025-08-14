package com.backend.immilog.user.domain.service;

import com.backend.immilog.user.domain.enums.UserStatus;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationService {

    public VerificationResult generateVerificationResult(
            UserStatus userStatus,
            String userCountryId
    ) {
        boolean isKoreanUser = isKoreanUser(userCountryId);

        return switch (userStatus) {
            case ACTIVE -> new VerificationResult(isKoreanUser ? "이미 인증된 사용자입니다." : "User is already verified.", true);
            case PENDING ->
                    new VerificationResult(isKoreanUser ? "이메일 인증이 완료되었습니다." : "Email verification is complete.", true);
            case BLOCKED -> new VerificationResult(isKoreanUser ? "차단된 사용자입니다." : "Blocked user.", false);
            default ->
                    new VerificationResult(isKoreanUser ? "이메일 인증이 필요한 사용자가 아닙니다." : "User does not need email verification.", false);
        };
    }

    private boolean isKoreanUser(String countryId) {
        return "SOUTH_KOREA".equals(countryId);
    }

    public record VerificationResult(
            String message,
            boolean isLoginAvailable
    ) {}
}