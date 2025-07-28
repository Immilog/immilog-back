package com.backend.immilog.user.application.services;

import com.backend.immilog.user.domain.model.user.*;
import com.backend.immilog.user.domain.repositories.UserRepository;
import com.backend.immilog.user.domain.service.EmailVerificationService;
import com.backend.immilog.user.domain.service.UserPasswordPolicy;
import com.backend.immilog.user.domain.service.UserRegistrationService;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User Application Service
 * 사용자 관련 애플리케이션 로직을 처리하는 서비스
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserRegistrationService userRegistrationService;
    private final UserPasswordPolicy userPasswordPolicy;
    private final EmailVerificationService emailVerificationService;

    public UserService(
            UserRepository userRepository,
            UserRegistrationService userRegistrationService,
            UserPasswordPolicy userPasswordPolicy,
            EmailVerificationService emailVerificationService
    ) {
        this.userRepository = userRepository;
        this.userRegistrationService = userRegistrationService;
        this.userPasswordPolicy = userPasswordPolicy;
        this.emailVerificationService = emailVerificationService;
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
        String encodedPassword = userPasswordPolicy.encodePassword(rawPassword);

        // 2. 도메인 객체 생성
        Auth auth = Auth.of(email, encodedPassword);
        Profile profile = Profile.of(nickname, imageUrl, interestCountry);
        Location location = Location.of(country, region);

        // 3. 도메인 서비스를 통한 사용자 등록
        User newUser = userRegistrationService.registerNewUser(auth, profile, location);

        // 4. 저장
        User savedUser = userRepository.save(newUser);

        return savedUser.getUserId();
    }

    @Transactional(readOnly = true)
    public User authenticateUser(
            String email,
            String rawPassword
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 비밀번호 검증
        userPasswordPolicy.validatePasswordMatch(rawPassword, user.getPassword());

        // 사용자 상태 검증
        user.validateActiveStatus();

        return user;
    }

    public void updateUserProfile(
            UserId userId,
            String nickname,
            String imageUrl,
            com.backend.immilog.user.domain.model.enums.Country interestCountry
    ) {
        User user = getUserById(userId);

        Profile newProfile = Profile.of(nickname, imageUrl, interestCountry);
        user.updateProfile(newProfile);

        userRepository.save(user);
    }

    public void changePassword(
            UserId userId,
            String currentPassword,
            String newPassword
    ) {
        User user = getUserById(userId);

        // 현재 비밀번호 검증
        userPasswordPolicy.validatePasswordMatch(currentPassword, user.getPassword());

        // 새 비밀번호 암호화 및 변경
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

    @Transactional(readOnly = true)
    public User getUserById(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }
}