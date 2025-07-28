package com.backend.immilog.user.application.usecase;

import com.backend.immilog.image.application.usecase.UploadImageUseCase;
import com.backend.immilog.user.application.command.UserInfoUpdateCommand;
import com.backend.immilog.user.application.command.UserPasswordChangeCommand;
import com.backend.immilog.user.application.result.LocationResult;
import com.backend.immilog.user.application.services.command.UserCommandService;
import com.backend.immilog.user.application.services.query.UserQueryService;
import com.backend.immilog.user.domain.model.Location;
import com.backend.immilog.user.domain.model.Profile;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.user.domain.service.UserPasswordPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface UpdateProfileUseCase {
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
    class UserUpdater implements UpdateProfileUseCase {
        private final UserQueryService userQueryService;
        private final UserCommandService userCommandService;
        private final UploadImageUseCase uploadImageUseCase;
        private final UserPasswordPolicy userPasswordPolicy;

        public UserUpdater(
                UserQueryService userQueryService,
                UserCommandService userCommandService,
                UploadImageUseCase uploadImageUseCase,
                UserPasswordPolicy userPasswordPolicy
        ) {
            this.userQueryService = userQueryService;
            this.userCommandService = userCommandService;
            this.uploadImageUseCase = uploadImageUseCase;
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
            var newLocation = Location.of(userInfoUpdateCommand.country(), region);
            var updatedUser = user
                    .updateProfile(newProfile)
                    .updateLocation(newLocation)
                    .changeStatus(userInfoUpdateCommand.status());

            userCommandService.save(updatedUser);
            uploadImageUseCase.deleteImage(previousProfileImage, userInfoUpdateCommand.profileImage());
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
