package com.backend.immilog.user.application.services;

import com.backend.immilog.shared.domain.model.UserData;
import com.backend.immilog.shared.domain.service.UserDataProvider;
import com.backend.immilog.user.application.services.query.UserQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * User 도메인의 UserDataProvider 구현체
 * Shared 인터페이스를 통해 다른 도메인에 사용자 데이터 제공
 */
@Slf4j
@Service
public class UserDataProviderImpl implements UserDataProvider {
    
    private final UserQueryService userQueryService;
    
    public UserDataProviderImpl(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }
    
    @Override
    public UserData getUserData(String userId) {
        try {
            var user = userQueryService.getUserById(userId);
            return new UserData(
                    user.getUserId().value(),
                    user.getNickname(),
                    user.getImageUrl()
            );
        } catch (Exception e) {
            log.warn("Failed to get user data for userId: {}", userId, e);
            return new UserData(userId, "Unknown", null);
        }
    }
    
    @Override
    public boolean existsUser(String userId) {
        try {
            userQueryService.getUserById(userId);
            return true;
        } catch (Exception e) {
            log.debug("User not found: {}", userId);
            return false;
        }
    }
}