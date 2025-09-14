package com.backend.immilog.user.application.services;

import com.backend.immilog.user.domain.model.*;
import com.backend.immilog.user.domain.repositories.UserRepository;
import com.backend.immilog.user.domain.service.UserPasswordPolicy;
import com.backend.immilog.user.domain.service.UserRegistrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserRegistrationService userRegistrationService;
    private final UserPasswordPolicy userPasswordPolicy;

    public UserService(
            UserRepository userRepository,
            UserRegistrationService userRegistrationService,
            UserPasswordPolicy userPasswordPolicy
    ) {
        this.userRepository = userRepository;
        this.userRegistrationService = userRegistrationService;
        this.userPasswordPolicy = userPasswordPolicy;
    }

    public UserId registerUser(
            String email,
            String rawPassword,
            String nickname,
            String imageUrl,
            String interestCountryId,
            String countryId,
            String region
    ) {
        // 1. 비밀번호 암호화
        var encodedPassword = userPasswordPolicy.encodePassword(rawPassword);

        // 2. 도메인 객체 생성
        var auth = Auth.of(email, encodedPassword);
        var profile = Profile.of(nickname, imageUrl, interestCountryId);
        var location = Location.of(countryId, region);

        // 3. 도메인 서비스를 통한 사용자 등록
        var newUser = userRegistrationService.registerNewUser(auth, profile, location);

        // 4. 저장
        var savedUser = userRepository.save(newUser);

        return savedUser.getUserId();
    }

    @Transactional(readOnly = true)
    public User authenticateUser(
            String email,
            String rawPassword
    ) {
        var user = userRepository.findByEmail(email);
        userPasswordPolicy.validatePasswordMatch(rawPassword, user.getPassword());
        user.validateActiveStatus();
        return user;
    }

    public void updateUserProfile(
            UserId userId,
            String nickname,
            String imageUrl,
            String interestCountryId
    ) {
        var user = getUserById(userId);

        var newProfile = Profile.of(nickname, imageUrl, interestCountryId);
        user.updateProfile(newProfile);

        userRepository.save(user);
    }

    public void changePassword(
            UserId userId,
            String currentPassword,
            String newPassword
    ) {
        var user = getUserById(userId);

        userPasswordPolicy.validatePasswordMatch(currentPassword, user.getPassword());

        String encodedNewPassword = userPasswordPolicy.encodePassword(newPassword);
        user.changePassword(encodedNewPassword);
        userRepository.save(user);
    }

    public void activateUser(UserId userId) {
        User user = getUserById(userId);
        user.activate();
        userRepository.save(user);
    }

    public void blockUser(UserId userId) {
        User user = getUserById(userId);
        user.block();
        userRepository.save(user);
    }

    private User getUserById(UserId userId) {
        return userRepository.findById(userId);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}