package com.backend.immilog.user.infrastructure.jpa;

import com.backend.immilog.user.domain.enums.UserRole;
import com.backend.immilog.user.domain.model.User;
import com.backend.immilog.user.domain.model.UserId;
import com.backend.immilog.user.domain.model.UserStatus;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@DynamicUpdate
@Entity
@Table(name = "user")
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long id;

    @Embedded
    private AuthJpaValue auth;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status")
    private UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole;

    @Embedded
    private LocationJpaValue location;

    @Embedded
    private ProfileJpaValue profile;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected UserJpaEntity() {}

    private UserJpaEntity(
            Long id,
            AuthJpaValue auth,
            UserRole userRole,
            ProfileJpaValue profile,
            LocationJpaValue location,
            UserStatus userStatus,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.auth = auth;
        this.userRole = userRole;
        this.profile = profile;
        this.location = location;
        this.userStatus = userStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserJpaEntity from(User user) {
        return new UserJpaEntity(
                user.getUserId() != null ? user.getUserId().value() : null,
                AuthJpaValue.from(user.getAuth()),
                user.getUserRole(),
                ProfileJpaValue.from(user.getProfile()),
                LocationJpaValue.from(user.getLocation()),
                user.getUserStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public User toDomain() {
        validateRequiredFields();

        UserId userId = this.id != null ? UserId.of(this.id) : null;

        if (userId == null) {
            return User.create(
                    this.auth.toDomain(),
                    this.profile.toDomain(),
                    this.location.toDomain()
            );
        } else {
            return User.restore(
                    userId,
                    this.auth.toDomain(),
                    this.userRole,
                    this.profile.toDomain(),
                    this.location.toDomain(),
                    this.userStatus,
                    this.createdAt,
                    this.updatedAt
            );
        }
    }

    public void updateFromDomain(User user) {
        this.auth = AuthJpaValue.from(user.getAuth());
        this.profile = ProfileJpaValue.from(user.getProfile());
        this.location = LocationJpaValue.from(user.getLocation());
        this.userStatus = user.getUserStatus();
        this.updatedAt = user.getUpdatedAt();
    }

    private void validateRequiredFields() {
        if (this.userRole == null || this.userStatus == null) {
            throw new UserException(UserErrorCode.ENTITY_TO_DOMAIN_ERROR);
        }
    }

    public Long getId() {return id;}

    public AuthJpaValue getAuth() {return auth;}

    public UserRole getUserRole() {return userRole;}

    public ProfileJpaValue getProfile() {return profile;}

    public LocationJpaValue getLocation() {return location;}

    public UserStatus getUserStatus() {return userStatus;}

    public LocalDateTime getCreatedAt() {return createdAt;}

    public LocalDateTime getUpdatedAt() {return updatedAt;}
}