package com.backend.immilog.user.domain.service;

import com.backend.immilog.user.domain.model.Auth;
import com.backend.immilog.user.domain.model.Location;
import com.backend.immilog.user.domain.model.Profile;
import com.backend.immilog.user.domain.model.User;
import com.backend.immilog.user.domain.repositories.UserRepository;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;

    public UserRegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerNewUser(
            Auth auth,
            Profile profile,
            Location location
    ) {
        this.validateEmailUniqueness(auth.email());
        return User.create(auth, profile, location);
    }

    private void validateEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException(UserErrorCode.EXISTING_USER);
        }
    }
}