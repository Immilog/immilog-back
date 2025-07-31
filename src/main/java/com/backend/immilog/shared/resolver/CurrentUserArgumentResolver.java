package com.backend.immilog.shared.resolver;

import com.backend.immilog.shared.annotation.CurrentUser;
import com.backend.immilog.shared.model.AuthenticatedUser;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class) &&
                (parameter.getParameterType().equals(AuthenticatedUser.class) ||
                        parameter.getParameterType().equals(String.class));
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        // JWT에서 추출한 사용자 정보를 기반으로 처리
        if (principal instanceof UserDetails userDetails) {
            String userId = userDetails.getUsername();

            if (parameter.getParameterType().equals(String.class)) {
                return userId;
            }

            if (parameter.getParameterType().equals(AuthenticatedUser.class)) {
                // UserDetails에서 추가 정보 추출 (필요시 구현)
                return AuthenticatedUser.of(userId, "", "");
            }
        }

        // JWT Authentication의 경우 principal이 String일 수 있음
        if (principal instanceof String userId) {
            if (parameter.getParameterType().equals(String.class)) {
                return userId;
            }

            if (parameter.getParameterType().equals(AuthenticatedUser.class)) {
                return AuthenticatedUser.of(userId, "", "");
            }
        }

        throw new IllegalArgumentException("Unsupported parameter type: " + parameter.getParameterType());
    }
}