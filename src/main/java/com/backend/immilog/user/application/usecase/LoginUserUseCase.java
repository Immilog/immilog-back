package com.backend.immilog.user.application.usecase;

import com.backend.immilog.user.application.command.UserSignInCommand;
import com.backend.immilog.user.application.result.LocationResult;
import com.backend.immilog.user.application.result.UserSignInResult;
import com.backend.immilog.user.application.services.command.TokenCommandService;
import com.backend.immilog.user.application.services.query.UserQueryService;
import com.backend.immilog.user.domain.service.UserPasswordPolicy;
import com.backend.immilog.user.domain.service.UserTokenGenerator;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface LoginUserUseCase {
    UserSignInResult signIn(
            UserSignInCommand command,
            CompletableFuture<LocationResult> country
    );

    UserSignInResult getUserSignInDTO(
            Long userSeq,
            LocationResult country
    );

    @Service
    class UserLoginProcessor implements LoginUserUseCase {
        private final UserQueryService userQueryService;
        private final TokenCommandService tokenCommandService;
        private final UserTokenGenerator userTokenGenerator;
        private final UserPasswordPolicy userPasswordPolicy;

        public UserLoginProcessor(
                UserQueryService userQueryService,
                TokenCommandService tokenCommandService,
                UserTokenGenerator tokenProvider,
                UserPasswordPolicy userPasswordPolicy
        ) {
            this.userQueryService = userQueryService;
            this.tokenCommandService = tokenCommandService;
            this.userTokenGenerator = tokenProvider;
            this.userPasswordPolicy = userPasswordPolicy;
        }

        private final int REFRESH_TOKEN_EXPIRE_TIME = 5 * 29 * 24 * 60;
        private final String TOKEN_PREFIX = "Refresh: ";

        @Override
        public UserSignInResult signIn(
                UserSignInCommand command,
                CompletableFuture<LocationResult> country
        ) {
            final var user = userQueryService.getUserByEmail(command.email());
            userPasswordPolicy.validatePasswordMatch(command.password(), user.getPassword());

            var userCountry = user.getCountry();
            var userEmail = user.getEmail();

            final var accessToken = userTokenGenerator.generate(user.getUserId().value(), userEmail, user.getUserRole(), userCountry);
            final var refreshToken = userTokenGenerator.generateRefreshToken();

            tokenCommandService.saveKeyAndValue(TOKEN_PREFIX + refreshToken, userEmail, REFRESH_TOKEN_EXPIRE_TIME);
            var locationResult = country.orTimeout(5, TimeUnit.SECONDS).exceptionally(throwable -> new LocationResult("Error", "Timeout")).join();
            boolean locationMatch = userCountry.koreanName().equals(locationResult.country()) && user.getRegion().equals(locationResult.city());

            return UserSignInResult.of(user, accessToken, refreshToken, locationMatch);
        }

        @Override
        public UserSignInResult getUserSignInDTO(
                Long userSeq,
                LocationResult locationResult
        ) {
            final var user = userQueryService.getUserById(userSeq);
            var userCountry = user.getCountry();
            var userEmail = user.getEmail();
            boolean isLocationMatch = userCountry.koreanName().equals(locationResult.country()) && user.getRegion().equals(locationResult.city());
            final var accessToken = userTokenGenerator.generate(user.getUserId().value(), userEmail, user.getUserRole(), userCountry);
            final var refreshToken = userTokenGenerator.generateRefreshToken();

            tokenCommandService.saveKeyAndValue(TOKEN_PREFIX + refreshToken, userEmail, REFRESH_TOKEN_EXPIRE_TIME);

            return UserSignInResult.of(user, accessToken, refreshToken, isLocationMatch);
        }

    }
}
