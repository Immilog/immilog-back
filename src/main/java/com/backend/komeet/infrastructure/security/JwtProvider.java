package com.backend.komeet.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.backend.komeet.user.model.dtos.TokenIssuanceDto;
import com.backend.komeet.user.enums.UserRole;
import com.backend.komeet.user.application.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT 토큰을 생성하고 검증하는 클래스
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {
    private final UserDetailsServiceImpl userDetailsService;

    @Value("${token.issuer}")
    private String issuer;
    private SecretKey secretKey;

    @Value("${token.secret-key}")
    private String secretKeyString;
    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyString));
    }

    private static final long ONE_DAY = 24 * 60 * 60 * 1000L;
    private static final long SIX_MONTH = 6 * 30 * 24 * 60 * 60 * 1000L;

    /**
     * 주어진 TokenIssuanceDto를 사용하여 액세스 토큰을 발급합니다.
     *
     * @param tokenTokenIssuanceDto 액세스 토큰 발급에 필요한 정보를 담은 TokenIssuanceDto
     * @return 발급된 액세스 토큰
     */
    public String issueAccessToken(TokenIssuanceDto tokenTokenIssuanceDto) {
        Claims claims = Jwts.claims().setSubject(tokenTokenIssuanceDto.getId().toString());
        claims.put("email", tokenTokenIssuanceDto.getEmail());
        claims.put("userRole", tokenTokenIssuanceDto.getUserRole());
        claims.put("country", tokenTokenIssuanceDto.getCountry().getCountryCode());

        return buildJwt(claims);
    }

    /**
     * 재발급에 사용될 리프레시 토큰 발급
     *
     * @return 발급된 리프레시 토큰
     */
    public String issueRefreshToken() {
        return buildJwt(null);
    }

    /**
     * 주어진 토큰을 검증
     *
     * @param token 검증할 토큰
     * @return 토큰이 유효하면 true, 그렇지 않으면 false 반환
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    /**
     * 주어진 토큰에서 사용자 ID 추출
     *
     * @param token 추출할 사용자 ID가 포함된 엑세스 토큰
     * @return 추출된 사용자 ID
     */
    public Long getIdFromToken(String token) {
        token = removeBearer(token);

        return Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    /**
     * 주어진 토큰에서 이메일 추출
     *
     * @param token 추출할 이메일이 포함된 엑세스 토큰
     * @return 추출된 이메일
     */
    public String getEmailFromToken(String token) {
        token = removeBearer(token);

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    /**
     * 주어진 토큰을 사용하여 인증 객체 생성
     *
     * @param token 사용하여 인증을 수행할 토큰
     * @return 생성된 Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        token = removeBearer(token);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.get("email", String.class);
        UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
        authorities.addAll(userRole.getAuthorities()); // 추가된 역할 권한

        return new UsernamePasswordAuthenticationToken(
                userDetails, "", authorities
        );
    }

    /**
     * 주어진 토큰에서 "Bearer " 접두어를 제거한 토큰 반환
     *
     * @param token 제거할 "Bearer " 접두어가 포함된 토큰
     * @return "Bearer " 접두어가 제거된 토큰
     */
    private String removeBearer(String token) {
        if (token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    /**
     * 주어진 Claims를 사용하여 JWT 생성
     *
     * @param claims JWT에 포함될 클레임 정보
     * @return 생성된 JWT
     */
    private String buildJwt(Claims claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(
                        new Date(System.currentTimeMillis() +
                                (claims == null ? SIX_MONTH : ONE_DAY)))
                .signWith(secretKey)
                .compact();
    }

}
