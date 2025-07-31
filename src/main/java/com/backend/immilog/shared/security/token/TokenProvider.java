package com.backend.immilog.shared.security.token;

import com.backend.immilog.shared.enums.Country;
import com.backend.immilog.user.domain.enums.UserRole;
import org.springframework.security.core.Authentication;

public interface TokenProvider {
    void init();

    String issueAccessToken(
            String id,
            String email,
            UserRole userRole,
            Country country
    );

    String issueRefreshToken();

    boolean validateToken(String token);

    String getIdFromToken(String token);

    String getEmailFromToken(String token);

    Authentication getAuthentication(String token);

    UserRole getUserRoleFromToken(String authorizationHeader);
}
