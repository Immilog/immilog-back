package com.backend.immilog.global.security;

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

    // 기본값을 제공하는 생성자
    @ConstructorBinding
    public JwtProperties {
        if (accessTokenExpiration == null) {
            accessTokenExpiration = Duration.ofDays(1);
        }
        if (refreshTokenExpiration == null) {
            refreshTokenExpiration = Duration.ofDays(180); // 6개월
        }
    }

    // 밀리초로 변환하는 편의 메서드들
    public long getAccessTokenExpirationMs() {
        return accessTokenExpiration.toMillis();
    }

    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpiration.toMillis();
    }
}