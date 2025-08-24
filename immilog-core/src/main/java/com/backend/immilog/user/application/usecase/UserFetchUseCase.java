package com.backend.immilog.user.application.usecase;

import com.backend.immilog.user.application.dto.UserResult;
import com.backend.immilog.user.application.services.query.UserQueryService;
import org.springframework.stereotype.Service;

@Service
public class UserFetchUseCase {
    
    private final UserQueryService userQueryService;
    
    public UserFetchUseCase(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }
    
    public UserResult getUserById(String userId) {
        var user = userQueryService.getUserById(userId);
        return UserResult.from(user);
    }
}