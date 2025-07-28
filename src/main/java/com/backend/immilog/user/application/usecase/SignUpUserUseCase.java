package com.backend.immilog.user.application.usecase;

import com.backend.immilog.user.application.command.UserSignUpCommand;
import com.backend.immilog.user.application.result.EmailVerificationResult;
import com.backend.immilog.user.application.result.UserNickNameResult;
import com.backend.immilog.user.application.services.UserService;
import com.backend.immilog.user.application.services.query.UserQueryService;
import com.backend.immilog.user.domain.enums.Country;
import com.backend.immilog.user.domain.model.UserId;
import com.backend.immilog.user.domain.service.EmailVerificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

public interface SignUpUserUseCase {
    UserNickNameResult signUp(UserSignUpCommand command);

    Boolean isNicknameAvailable(String nickname);

    EmailVerificationResult verifyEmail(Long userSeq);

    @Slf4j
    @Service
    class UserSignUpProcessor implements SignUpUserUseCase {
        private final UserService userService;
        private final UserQueryService userQueryService;
        private final EmailVerificationService emailVerificationService;

        public UserSignUpProcessor(
                UserService userService,
                UserQueryService userQueryService,
                EmailVerificationService emailVerificationService
        ) {
            this.userService = userService;
            this.userQueryService = userQueryService;
            this.emailVerificationService = emailVerificationService;
        }

        @Override
        public UserNickNameResult signUp(UserSignUpCommand command) {
            var userId = userService.registerUser(
                    command.email(),
                    command.password(),
                    command.nickName(),
                    command.profileImage(),
                    parseCountry(command.interestCountry(), command.country()),
                    Country.valueOf(command.country()),
                    command.region()
            );

            var savedUser = userQueryService.getUserById(userId);
            return new UserNickNameResult(userId.value(), savedUser.getNickname());
        }

        @Override
        public Boolean isNicknameAvailable(String nickname) {
            return userQueryService.isNicknameAvailable(nickname);
        }

        @Override
        public EmailVerificationResult verifyEmail(Long userSeq) {
            var user = userQueryService.getUserById(UserId.of(userSeq));

            var verificationResult = emailVerificationService.generateVerificationResult(
                    user.getUserStatus(),
                    user.getCountry()
            );

            return new EmailVerificationResult(verificationResult.message(), verificationResult.isLoginAvailable());
        }

        private Country parseCountry(
                String interestCountryValue,
                String defaultCountry
        ) {
            if (interestCountryValue == null || interestCountryValue.isEmpty()) {
                return Country.valueOf(defaultCountry);
            }
            return Country.valueOf(interestCountryValue);
        }
    }
}
