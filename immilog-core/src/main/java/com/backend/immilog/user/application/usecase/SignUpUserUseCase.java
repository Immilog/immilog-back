package com.backend.immilog.user.application.usecase;

import com.backend.immilog.user.application.dto.in.UserSignUpCommand;
import com.backend.immilog.user.application.dto.out.EmailVerificationResult;
import com.backend.immilog.user.application.result.UserNicknameResult;
import com.backend.immilog.user.application.services.UserService;
import com.backend.immilog.user.domain.model.UserId;
import com.backend.immilog.user.domain.repositories.UserRepository;
import com.backend.immilog.user.domain.service.EmailVerificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface SignUpUserUseCase {
    UserNicknameResult signUp(UserSignUpCommand command);

    Boolean isNicknameAvailable(String nickname);

    EmailVerificationResult verifyEmail(String userId);

    @Slf4j
    @Service
    class UserSignUpProcessor implements SignUpUserUseCase {
        private final UserService userService;
        private final UserRepository userRepository;
        private final EmailVerificationService emailVerificationService;

        public UserSignUpProcessor(
                UserService userService,
                UserRepository userRepository,
                EmailVerificationService emailVerificationService
        ) {
            this.userService = userService;
            this.userRepository = userRepository;
            this.emailVerificationService = emailVerificationService;
        }

        @Override
        @Transactional
        public UserNicknameResult signUp(UserSignUpCommand command) {
            var userId = userService.registerUser(
                    command.email(),
                    command.password(),
                    command.nickName(),
                    command.profileImage(),
                    parseCountryId(command.interestCountry(), command.country()),
                    command.country(),
                    command.region()
            );

            var savedUser = userRepository.findById(userId);
            return new UserNicknameResult(userId.value(), savedUser.getNickname());
        }

        @Override
        @Transactional(readOnly = true)
        public Boolean isNicknameAvailable(String nickname) {
            return userRepository.findByNickname(nickname).isPresent();
        }

        @Override
        @Transactional
        public EmailVerificationResult verifyEmail(String userId) {
            var user = userRepository.findById(UserId.of(userId));

            var verificationResult = emailVerificationService.generateVerificationResult(
                    user.getUserStatus(),
                    user.getCountryId()
            );

            userRepository.save(user.activate());

            return new EmailVerificationResult(verificationResult.message(), verificationResult.isLoginAvailable());
        }

        private String parseCountryId(
                String interestCountryValue,
                String defaultCountry
        ) {
            if (interestCountryValue == null || interestCountryValue.isEmpty()) {
                return defaultCountry;
            }
            return interestCountryValue;
        }
    }
}
