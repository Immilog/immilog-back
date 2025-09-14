package com.backend.immilog.user.application.services;

import com.backend.immilog.shared.domain.model.UserData;
import com.backend.immilog.shared.domain.service.UserDataProvider;
import com.backend.immilog.user.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDataProviderImpl implements UserDataProvider {

    private final UserRepository userRepository;

    @Override
    public UserData getUserData(String userId) {
        try {
            var user = userRepository.findById(userId);
            return new UserData(
                user.getUserId().value(), 
                user.getNickname(), 
                user.getImageUrl(),
                user.getCountryId(),
                user.getRegion()
            );
        } catch (Exception e) {
            log.warn("Failed to get user data for userId: {}", userId, e);
            return new UserData(userId, "Unknown", null, null, null);
        }
    }
    
    @Override
    public boolean existsUser(String userId) {
        try {
            userRepository.findById(userId);
            return true;
        } catch (Exception e) {
            log.debug("User not found: {}", userId);
            return false;
        }
    }
}