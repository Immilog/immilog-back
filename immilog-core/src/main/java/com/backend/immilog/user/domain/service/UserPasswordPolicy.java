package com.backend.immilog.user.domain.service;

import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.springframework.stereotype.Service;

@Service
public class UserPasswordPolicy {

    private final PasswordEncryptionService passwordEncryptionService;

    public UserPasswordPolicy(PasswordEncryptionService passwordEncryptionService) {
        this.passwordEncryptionService = passwordEncryptionService;
    }

    public void validatePasswordMatch(
            String rawPassword,
            String encodedPassword
    ) {
        if (!passwordEncryptionService.matches(rawPassword, encodedPassword)) {
            throw new UserException(UserErrorCode.PASSWORD_NOT_MATCH);
        }
    }

    public String encodePassword(String rawPassword) {
        validateRawPassword(rawPassword);
        return passwordEncryptionService.encode(rawPassword);
    }

    private void validateRawPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD_FORMAT);
        }
        if (password.length() < 8 || password.length() > 50) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD_FORMAT);
        }
    }
}
