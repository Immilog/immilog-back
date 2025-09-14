package com.backend.immilog.user.application.usecase;

import com.backend.immilog.shared.infrastructure.DataRepository;
import com.backend.immilog.user.application.dto.in.UserSignInCommand;
import com.backend.immilog.user.application.dto.out.LocationResult;
import com.backend.immilog.user.application.dto.out.UserSignInResult;
import com.backend.immilog.user.domain.repositories.UserRepository;
import com.backend.immilog.user.domain.service.UserPasswordPolicy;
import com.backend.immilog.user.domain.service.UserTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface LoginUserUseCase {
    UserSignInResult signIn(
            UserSignInCommand command,
            CompletableFuture<LocationResult> country
    );

    UserSignInResult getUserSignInDTO(
            String userId,
            LocationResult country
    );

    UserSignInResult refreshToken(String refreshToken);

    @Service
    @RequiredArgsConstructor
    class UserLoginProcessor implements LoginUserUseCase {
        private final UserRepository userRepository;
        private final DataRepository dataRepository;
        private final UserTokenGenerator userTokenGenerator;
        private final UserPasswordPolicy userPasswordPolicy;

        private final int REFRESH_TOKEN_EXPIRE_TIME = 5 * 29 * 24 * 60;
        private final String TOKEN_PREFIX = "Refresh: ";

        @Override
        public UserSignInResult signIn(
                UserSignInCommand command,
                CompletableFuture<LocationResult> country
        ) {
            final var user = userRepository.findByEmail(command.email());
            userPasswordPolicy.validatePasswordMatch(command.password(), user.getPassword());

            var userCountryId = user.getCountryId();
            var userEmail = user.getEmail();

            final var accessToken = userTokenGenerator.generate(user.getUserId().value(), userEmail, user.getUserRole(), userCountryId);
            final var refreshToken = userTokenGenerator.generateRefreshToken();

            dataRepository.save(TOKEN_PREFIX + refreshToken, userEmail, REFRESH_TOKEN_EXPIRE_TIME);
            var locationResult = country.orTimeout(5, TimeUnit.SECONDS).exceptionally(throwable -> new LocationResult("Error", "Timeout")).join();
            boolean locationMatch = userCountryId.equals(locationResult.country());

            return UserSignInResult.of(user, accessToken, refreshToken, locationMatch);
        }

        @Override
        public UserSignInResult getUserSignInDTO(
                String userId,
                LocationResult locationResult
        ) {
            final var user = userRepository.findById(userId);
            var userCountryId = user.getCountryId();
            var userEmail = user.getEmail();
            boolean isLocationMatch = userCountryId.equals(locationResult.country()) && user.getRegion().equals(locationResult.city());
            final var accessToken = userTokenGenerator.generate(user.getUserId().value(), userEmail, user.getUserRole(), userCountryId);
            final var refreshToken = userTokenGenerator.generateRefreshToken();

            dataRepository.save(TOKEN_PREFIX + refreshToken, userEmail, REFRESH_TOKEN_EXPIRE_TIME);

            return UserSignInResult.of(user, accessToken, refreshToken, isLocationMatch);
        }

        @Override
        public UserSignInResult refreshToken(String refreshToken) {
            final var userEmail = dataRepository.findByKey(TOKEN_PREFIX + refreshToken);
            if (userEmail == null) {
                throw new IllegalArgumentException("Invalid refresh token");
            }

            final var user = userRepository.findByEmail(userEmail);
            var userCountryId = user.getCountryId();

            final var newAccessToken = userTokenGenerator.generate(user.getUserId().value(), userEmail, user.getUserRole(), userCountryId);
            final var newRefreshToken = userTokenGenerator.generateRefreshToken();

            dataRepository.deleteByKey(TOKEN_PREFIX + refreshToken);
            dataRepository.save(TOKEN_PREFIX + newRefreshToken, userEmail, REFRESH_TOKEN_EXPIRE_TIME);

            return UserSignInResult.of(user, newAccessToken, newRefreshToken, true);
        }

    }
}
