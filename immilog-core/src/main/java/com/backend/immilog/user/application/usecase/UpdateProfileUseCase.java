package com.backend.immilog.user.application.usecase;

// import com.backend.immilog.image.application.usecase.UploadImageUseCase;

import com.backend.immilog.image.application.usecase.UploadImageUseCase;
import com.backend.immilog.user.application.dto.in.UserInfoUpdateCommand;
import com.backend.immilog.user.application.dto.in.UserPasswordChangeCommand;
import com.backend.immilog.user.application.dto.out.LocationResult;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.user.domain.model.Location;
import com.backend.immilog.user.domain.model.Profile;
import com.backend.immilog.user.domain.repositories.UserRepository;
import com.backend.immilog.user.domain.service.UserPasswordPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface UpdateProfileUseCase {
    void updateInformation(
            String userId,
            CompletableFuture<LocationResult> futureRegion,
            UserInfoUpdateCommand userInfoUpdateCommand
    );

    void changePassword(
            String userId,
            UserPasswordChangeCommand command
    );

    void updateUserStatus(
            String targetUserId,
            String adminUserId,
            UserStatus requestedStatus
    );

    @Slf4j
    @Service
    @RequiredArgsConstructor
    class UserUpdater implements UpdateProfileUseCase {
        private final UploadImageUseCase uploadImageUseCase;
        private final UserRepository userRepository;
        private final UserPasswordPolicy userPasswordPolicy;

        @Override
        public void updateInformation(
                String userId,
                CompletableFuture<LocationResult> futureRegion,
                UserInfoUpdateCommand userInfoUpdateCommand
        ) {
            var user = userRepository.findById(userId);
            var previousProfileImage = user.getImageUrl();
            var region = getRegion(futureRegion);
            var newProfile = Profile.of(
                    userInfoUpdateCommand.nickName(),
                    userInfoUpdateCommand.profileImage(),
                    userInfoUpdateCommand.interestCountryId()
            );
            var newLocation = Location.of(userInfoUpdateCommand.countryId(), region);
            var updatedUser = user
                    .updateProfile(newProfile)
                    .updateLocation(newLocation)
                    .changeStatus(userInfoUpdateCommand.status());

            userRepository.save(updatedUser);
             uploadImageUseCase.deleteImage(previousProfileImage, userInfoUpdateCommand.profileImage());
        }

        @Override
        public void changePassword(
                String userId,
                UserPasswordChangeCommand command
        ) {
            var user = userRepository.findById(userId);
            final var existingPassword = command.existingPassword();
            final var newPassword = command.newPassword();
            final var currentPassword = user.getPassword();
            userPasswordPolicy.validatePasswordMatch(existingPassword, currentPassword);
            var encodedPassword = userPasswordPolicy.encodePassword(newPassword);
            var updatedUser = user.changePassword(encodedPassword);
            userRepository.save(updatedUser);
        }

        @Override
        public void updateUserStatus(
                String targetUserId,
                String adminUserId,
                UserStatus requestedStatus
        ) {
            userRepository.findById(adminUserId).validateAdminRole();
            var targetUser = userRepository.findById(targetUserId).changeStatus(requestedStatus);
            userRepository.save(targetUser);
        }

        private String getRegion(CompletableFuture<LocationResult> country) {
            try {
                if (country.get() != null) {
                    var countryPair = country.join();
                    return countryPair.city();
                }
            } catch (InterruptedException | ExecutionException e) {
                log.info(e.getMessage());
            }
            return null;
        }
    }
}
