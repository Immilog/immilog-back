package com.backend.immilog.shared.security.password;

import com.backend.immilog.user.domain.service.PasswordEncryptionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SpringPasswordEncryptionService implements PasswordEncryptionService {

    private final PasswordEncoder passwordEncoder;

    public SpringPasswordEncryptionService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(
            String rawPassword,
            String encodedPassword
    ) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}