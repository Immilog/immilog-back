package com.backend.immilog.user.domain.service;

import com.backend.immilog.shared.enums.Country;
import com.backend.immilog.shared.security.token.TokenProvider;
import com.backend.immilog.user.domain.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserTokenGenerator {
    private final TokenProvider tokenProvider;

    public UserTokenGenerator(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public String generate(
            String userId,
            String email,
            UserRole userRole,
            Country country
    ) {
        return tokenProvider.issueAccessToken(userId, email, userRole, country);
    }

    public String generateRefreshToken() {
        return tokenProvider.issueRefreshToken();
    }
}
