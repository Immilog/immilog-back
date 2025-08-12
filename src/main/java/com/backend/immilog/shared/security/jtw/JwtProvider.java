package com.backend.immilog.shared.security.jtw;

import com.backend.immilog.shared.config.properties.JwtProperties;
import com.backend.immilog.shared.security.token.TokenProvider;
import com.backend.immilog.user.domain.enums.UserRole;
import com.backend.immilog.user.infrastructure.security.UserDetailsServiceImpl;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider implements TokenProvider {

    private final JwtProperties jwtProperties;
    private final UserDetailsServiceImpl userDetailsService;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secretKey()));
    }

    @Override
    public String issueAccessToken(
            String id,
            String email,
            UserRole userRole,
            String countryId
    ) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getAccessTokenExpirationMs());

        return Jwts.builder()
                .subject(id)
                .claim("email", email)
                .claim("userRole", userRole.name())
                .claim("countryId", countryId)
                .issuer(jwtProperties.issuer())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String issueRefreshToken() {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getRefreshTokenExpirationMs());

        return Jwts.builder()
                .issuer(jwtProperties.issuer())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException ex) {
            log.debug("Invalid JWT token: {}", ex.getMessage());
            return false;
        }
    }

    @Override
    public String getIdFromToken(String token) {
        token = removeBearer(token);

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public String getEmailFromToken(String token) {
        token = removeBearer(token);

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }

    @Override
    public Authentication getAuthentication(String token) {
        token = removeBearer(token);

        var claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        var userId = claims.getSubject();

        var userRole = UserRole.valueOf(claims.get("userRole", String.class));

        var userDetails = userDetailsService.loadUserByUsername(claims.get("email", String.class)); // 이메일로 UserDetails를 로드

        List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
        authorities.addAll(userRole.getAuthorities());

        return new UsernamePasswordAuthenticationToken(userId, null, authorities);
    }

    @Override
    public UserRole getUserRoleFromToken(String authorizationHeader) {
        String token = removeBearer(authorizationHeader);

        String userRoleString = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userRole", String.class);

        return UserRole.valueOf(userRoleString);
    }

    private String removeBearer(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}