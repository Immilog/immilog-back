package com.backend.immilog.user.application.usecase;

import com.backend.immilog.user.application.command.UserSignUpCommand;
import com.backend.immilog.user.application.result.EmailVerificationResult;
import com.backend.immilog.user.application.result.UserNickNameResult;
import com.backend.immilog.user.application.service.UserApplicationService;
import com.backend.immilog.user.application.services.UserQueryService;
import com.backend.immilog.user.domain.model.enums.Country;
import com.backend.immilog.user.domain.model.user.User;
import com.backend.immilog.user.domain.model.user.UserId;
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
        private final UserApplicationService userApplicationService;
        private final UserQueryService userQueryService;
        private final EmailVerificationService emailVerificationService;

        public UserSignUpProcessor(
                UserApplicationService userApplicationService,
                UserQueryService userQueryService,
                EmailVerificationService emailVerificationService
        ) {
            this.userApplicationService = userApplicationService;
            this.userQueryService = userQueryService;
            this.emailVerificationService = emailVerificationService;
        }

        @Override
        public UserNickNameResult signUp(UserSignUpCommand command) {
            // 새로운 UserApplicationService를 통한 회원가입
            UserId userId = userApplicationService.registerUser(
                    command.email(),
                    command.password(),
                    command.nickName(),
                    command.profileImage(),
                    parseCountry(command.interestCountry(), command.country()),
                    Country.valueOf(command.country()),
                    command.region()
            );

            // 생성된 사용자 정보 조회
            User savedUser = userApplicationService.getUserById(userId);
            return new UserNickNameResult(userId.value(), savedUser.getNickname());
        }

        @Override
        public Boolean isNicknameAvailable(String nickname) {
            return userQueryService.isNicknameAvailable(nickname);
        }

        @Override
        public EmailVerificationResult verifyEmail(Long userSeq) {
            User user = userApplicationService.getUserById(UserId.of(userSeq));

            var verificationResult = emailVerificationService.generateVerificationResult(
                    user.getUserStatus(),
                    user.getCountry()
            );

            return new EmailVerificationResult(
                    verificationResult.message(),
                    verificationResult.isLoginAvailable()
            );
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
