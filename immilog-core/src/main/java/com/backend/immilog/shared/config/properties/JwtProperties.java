package com.backend.immilog.shared.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.time.Duration;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String issuer,
        String secretKey,
        Duration accessTokenExpiration,
        Duration refreshTokenExpiration
) {

    @ConstructorBinding
    public JwtProperties {
        if (accessTokenExpiration == null) {
            accessTokenExpiration = Duration.ofDays(1);
        }
        if (refreshTokenExpiration == null) {
            refreshTokenExpiration = Duration.ofDays(180);
        }
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenExpiration.toMillis();
    }

    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpiration.toMillis();
    }
}