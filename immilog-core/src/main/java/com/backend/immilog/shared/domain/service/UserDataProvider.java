package com.backend.immilog.shared.domain.service;

import com.backend.immilog.shared.domain.model.UserData;

public interface UserDataProvider {
    
    UserData getUserData(String userId);
    
    boolean existsUser(String userId);
    
    default boolean isValidUser(String userId) {
        return userId != null && !userId.trim().isEmpty() && existsUser(userId);
    }
}