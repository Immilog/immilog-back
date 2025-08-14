package com.backend.immilog.user.application.usecase;

import com.backend.immilog.user.application.command.UserSignUpCommand;
import com.backend.immilog.user.application.result.EmailVerificationResult;
import com.backend.immilog.user.application.result.userNicknameResult;
import com.backend.immilog.user.application.services.UserService;
import com.backend.immilog.user.application.services.command.UserCommandService;
import com.backend.immilog.user.application.services.query.UserQueryService;
import com.backend.immilog.user.domain.model.UserId;
import com.backend.immilog.user.domain.service.EmailVerificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

public interface SignUpUserUseCase {
    userNicknameResult signUp(UserSignUpCommand command);

    Boolean isNicknameAvailable(String nickname);

    EmailVerificationResult verifyEmail(String userId);

    @Slf4j
    @Service
    class UserSignUpProcessor implements SignUpUserUseCase {
        private final UserService userService;
        private final UserQueryService userQueryService;
        private final UserCommandService userCommandService;
        private final EmailVerificationService emailVerificationService;

        public UserSignUpProcessor(
                UserService userService,
                UserQueryService userQueryService,
                UserCommandService userCommandService,
                EmailVerificationService emailVerificationService
        ) {
            this.userService = userService;
            this.userQueryService = userQueryService;
            this.userCommandService = userCommandService;
            this.emailVerificationService = emailVerificationService;
        }

        @Override
        public userNicknameResult signUp(UserSignUpCommand command) {
            var userId = userService.registerUser(
                    command.email(),
                    command.password(),
                    command.nickName(),
                    command.profileImage(),
                    parseCountryId(command.interestCountry(), command.country()),
                    command.country(),
                    command.region()
            );

            var savedUser = userQueryService.getUserById(userId);
            return new userNicknameResult(userId.value(), savedUser.getNickname());
        }

        @Override
        public Boolean isNicknameAvailable(String nickname) {
            return userQueryService.isNicknameAvailable(nickname);
        }

        @Override
        public EmailVerificationResult verifyEmail(String userId) {
            var user = userQueryService.getUserById(UserId.of(userId));

            var verificationResult = emailVerificationService.generateVerificationResult(
                    user.getUserStatus(),
                    user.getCountryId()
            );

            userCommandService.save(user.activate());

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
