package com.backend.immilog.global.filters;

import com.backend.immilog.global.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@AllArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private JwtProvider jwtProvider;
    private static final String[] ALL_WHITELIST = {
            "/api/v1/users/**",
            "/api/v1/locations",
            "/ws/**",
            "/swagger-ui/index.html",
            "/swagger-ui/**",
            "/v2/api-docs",
            "/swagger-resources/**",
            "/webjars/**"
    };

    private boolean isFilterCheck(
            String requestURI
    ) {
        return !PatternMatchUtils.simpleMatch(ALL_WHITELIST, requestURI);
    }

    private String extractTokenFromRequest(
            HttpServletRequest request
    ) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException {

        String token = extractTokenFromRequest(request);

        try {
            // 화이트리스트에 있는 경우에는 필터링을 건너뛰어서 다음 필터로 진행
            if (isFilterCheck(request.getRequestURI())) {
                // 화이트리스트에 없는 경우에만 검증 처리
                if (token != null) {
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(jwtProvider.getAuthentication(token));
                }
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }
}
