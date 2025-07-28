package com.backend.immilog.user.application.services;

import com.backend.immilog.user.application.services.command.UserCommandService;
import com.backend.immilog.user.application.services.query.UserQueryService;
import com.backend.immilog.user.domain.model.user.*;
import com.backend.immilog.user.domain.service.UserPasswordPolicy;
import com.backend.immilog.user.domain.service.UserRegistrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final UserRegistrationService userRegistrationService;
    private final UserPasswordPolicy userPasswordPolicy;

    public UserService(
            UserQueryService userQueryService,
            UserCommandService userCommandService,
            UserRegistrationService userRegistrationService,
            UserPasswordPolicy userPasswordPolicy
    ) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
        this.userRegistrationService = userRegistrationService;
        this.userPasswordPolicy = userPasswordPolicy;
    }

    public UserId registerUser(
            String email,
            String rawPassword,
            String nickname,
            String imageUrl,
            com.backend.immilog.user.domain.model.enums.Country interestCountry,
            com.backend.immilog.user.domain.model.enums.Country country,
            String region
    ) {
        // 1. 비밀번호 암호화
        var encodedPassword = userPasswordPolicy.encodePassword(rawPassword);

        // 2. 도메인 객체 생성
        var auth = Auth.of(email, encodedPassword);
        var profile = Profile.of(nickname, imageUrl, interestCountry);
        var location = Location.of(country, region);

        // 3. 도메인 서비스를 통한 사용자 등록
        var newUser = userRegistrationService.registerNewUser(auth, profile, location);

        // 4. 저장
        var savedUser = userCommandService.save(newUser);

        return savedUser.getUserId();
    }

    @Transactional(readOnly = true)
    public User authenticateUser(
            String email,
            String rawPassword
    ) {
        var user = userQueryService.getUserByEmail(email);
        userPasswordPolicy.validatePasswordMatch(rawPassword, user.getPassword());
        user.validateActiveStatus();
        return user;
    }

    public void updateUserProfile(
            UserId userId,
            String nickname,
            String imageUrl,
            com.backend.immilog.user.domain.model.enums.Country interestCountry
    ) {
        var user = getUserById(userId);

        var newProfile = Profile.of(nickname, imageUrl, interestCountry);
        user.updateProfile(newProfile);

        userCommandService.save(user);
    }

    public void changePassword(
            UserId userId,
            String currentPassword,
            String newPassword
    ) {
        var user = getUserById(userId);

        // 현재 비밀번호 검증
        userPasswordPolicy.validatePasswordMatch(currentPassword, user.getPassword());

        // 새 비밀번호 암호화 및 변경
        String encodedNewPassword = userPasswordPolicy.encodePassword(newPassword);
        user.changePassword(encodedNewPassword);

        userCommandService.save(user);
    }

    public void activateUser(UserId userId) {
        User user = getUserById(userId);
        user.activate();
        userCommandService.save(user);
    }

    public void blockUser(UserId userId) {
        User user = getUserById(userId);
        user.block();
        userCommandService.save(user);
    }

    private User getUserById(UserId userId) {
        return userQueryService.getUserById(userId);
    }

    private User getUserByEmail(String email) {
        return userQueryService.getUserByEmail(email);
    }
}