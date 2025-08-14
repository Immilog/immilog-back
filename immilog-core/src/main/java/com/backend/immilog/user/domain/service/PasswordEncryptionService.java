package com.backend.immilog.user.domain.service;

public interface PasswordEncryptionService {
    String encode(String rawPassword);

    boolean matches(
            String rawPassword,
            String encodedPassword
    );
}