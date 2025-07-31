package com.backend.immilog.user.domain.model;

import com.backend.immilog.shared.enums.Country;
import com.backend.immilog.user.domain.enums.UserRole;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;

import java.time.LocalDateTime;

public class User {
    private final UserId userId;
    private Auth auth;
    private final UserRole userRole;
    private Profile profile;
    private Location location;
    private UserStatus userStatus;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User(
            UserId userId,
            Auth auth,
            UserRole userRole,
            Profile profile,
            Location location,
            UserStatus userStatus,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.userId = userId;
        this.auth = auth;
        this.userRole = userRole;
        this.profile = profile;
        this.location = location;
        this.userStatus = userStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User create(
            Auth auth,
            Profile profile,
            Location location
    ) {
        return new User(
                null,
                auth,
                UserRole.ROLE_USER,
                profile,
                location,
                UserStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static User restore(
            UserId userId,
            Auth auth,
            UserRole userRole,
            Profile profile,
            Location location,
            UserStatus userStatus,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new User(
                userId,
                auth,
                userRole,
                profile,
                location,
                userStatus,
                createdAt,
                updatedAt
        );
    }

    public User changePassword(String encodedNewPassword) {
        validatePasswordChange(encodedNewPassword);
        this.auth = Auth.of(this.auth.email(), encodedNewPassword);
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public User updateProfile(Profile newProfile) {
        if (newProfile == null) {
            throw new UserException(UserErrorCode.INVALID_NICKNAME);
        }
        this.profile = newProfile;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public User updateLocation(Location newLocation) {
        if (newLocation == null) {
            throw new UserException(UserErrorCode.INVALID_REGION);
        }
        this.location = newLocation;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public User changeStatus(UserStatus newStatus) {
        if (newStatus == null || newStatus.equals(this.userStatus)) {
            return this;
        }
        this.userStatus = newStatus;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public User activate() {
        if (this.userStatus != UserStatus.PENDING) {
            throw new UserException(UserErrorCode.USER_STATUS_NOT_ACTIVE);
        }
        return changeStatus(UserStatus.ACTIVE);
    }

    public User block() {
        return changeStatus(UserStatus.BLOCKED);
    }

    public void validateAdminRole() {
        if (!this.userRole.isAdmin()) {
            throw new UserException(UserErrorCode.NOT_AN_ADMIN_USER);
        }
    }

    public void validateActiveStatus() {
        if (this.userStatus != UserStatus.ACTIVE) {
            throw new UserException(UserErrorCode.USER_STATUS_NOT_ACTIVE);
        }
    }

    public boolean isSameUser(UserId otherUserId) {
        return this.userId != null && this.userId.equals(otherUserId);
    }

    private void validatePasswordChange(String encodedNewPassword) {
        if (encodedNewPassword == null || encodedNewPassword.trim().isEmpty()) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD_FORMAT);
        }
    }

    public UserId getUserId() {return userId;}

    public String getEmail() {return auth.email();}

    public String getPassword() {return auth.password();}

    public UserRole getUserRole() {return userRole;}

    public String getNickname() {return profile.nickname();}

    public String getImageUrl() {return profile.imageUrl();}

    public Country getInterestCountry() {return profile.interestCountry();}

    public Country getCountry() {return location.country();}

    public String getRegion() {return location.region();}

    public UserStatus getUserStatus() {return userStatus;}

    public LocalDateTime getCreatedAt() {return createdAt;}

    public LocalDateTime getUpdatedAt() {return updatedAt;}

    public Auth getAuth() {return auth;}

    public Profile getProfile() {return profile;}

    public Location getLocation() {return location;}
}