package com.backend.immilog.user.application.usecase;

import com.backend.immilog.image.application.ImageUploadUseCase;
import com.backend.immilog.user.application.command.UserInfoUpdateCommand;
import com.backend.immilog.user.application.command.UserPasswordChangeCommand;
import com.backend.immilog.user.application.result.LocationResult;
import com.backend.immilog.user.application.services.UserCommandService;
import com.backend.immilog.user.application.services.UserQueryService;
import com.backend.immilog.user.domain.model.user.Location;
import com.backend.immilog.user.domain.model.user.Profile;
import com.backend.immilog.user.domain.model.user.UserStatus;
import com.backend.immilog.user.domain.service.UserPasswordPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface UpdateUserUseCase {
    void updateInformation(
            Long userSeq,
            CompletableFuture<LocationResult> futureRegion,
            UserInfoUpdateCommand userInfoUpdateCommand
    );

    void changePassword(
            Long userSeq,
            UserPasswordChangeCommand command
    );

    void blockOrUnblockUser(
            Long targetUserSeq,
            Long adminSeq,
            String userStatus
    );

    @Slf4j
    @Service
    class UserUpdater implements UpdateUserUseCase {
        private final UserQueryService userQueryService;
        private final UserCommandService userCommandService;
        private final ImageUploadUseCase imageUploader;
        private final UserPasswordPolicy userPasswordPolicy;

        public UserUpdater(
                UserQueryService userQueryService,
                UserCommandService userCommandService,
                ImageUploadUseCase imageUploader,
                UserPasswordPolicy userPasswordPolicy
        ) {
            this.userQueryService = userQueryService;
            this.userCommandService = userCommandService;
            this.imageUploader = imageUploader;
            this.userPasswordPolicy = userPasswordPolicy;
        }

        @Override
        public void updateInformation(
                Long userSeq,
                CompletableFuture<LocationResult> futureRegion,
                UserInfoUpdateCommand userInfoUpdateCommand
        ) {
            var user = userQueryService.getUserById(userSeq);
            var previousProfileImage = user.getImageUrl();
            var region = getRegion(futureRegion);
            var newProfile = Profile.of(
                    userInfoUpdateCommand.nickName(),
                    userInfoUpdateCommand.profileImage(),
                    userInfoUpdateCommand.interestCountry()
            );
            var newLocation = Location.of(
                    userInfoUpdateCommand.country(),
                    region
            );
            var updatedUser = user
                    .updateProfile(newProfile)
                    .updateLocation(newLocation)
                    .changeStatus(userInfoUpdateCommand.status());

            userCommandService.save(updatedUser);
            imageUploader.deleteFile(previousProfileImage, userInfoUpdateCommand.profileImage());
        }

        @Override
        public void changePassword(
                Long userSeq,
                UserPasswordChangeCommand command
        ) {
            var user = userQueryService.getUserById(userSeq);
            final var existingPassword = command.existingPassword();
            final var newPassword = command.newPassword();
            final var currentPassword = user.getPassword();
            userPasswordPolicy.validatePasswordMatch(existingPassword, currentPassword);
            var encodedPassword = userPasswordPolicy.encodePassword(newPassword);
            var updatedUser = user.changePassword(encodedPassword);
            userCommandService.save(updatedUser);
        }

        @Override
        public void blockOrUnblockUser(
                Long targetUserSeq,
                Long adminSeq,
                String userStatus
        ) {
            userQueryService.getUserById(adminSeq).validateAdminRole();
            var targetUser = userQueryService.getUserById(targetUserSeq).changeStatus(UserStatus.valueOf(userStatus));
            userCommandService.save(targetUser);
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
