package com.backend.immilog.user.domain.model;

import com.backend.immilog.user.domain.enums.UserRole;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User 도메인 테스트")
class UserTest {

    private Auth createValidAuth() {
        return Auth.of("test@example.com", "encodedPassword123");
    }

    private Profile createValidProfile() {
        return Profile.of("테스트유저", "https://example.com/image.jpg", "KR");
    }

    private Location createValidLocation() {
        return Location.of("KR", "서울특별시");
    }

    @Test
    @DisplayName("정상적인 정보로 새 사용자를 생성할 수 있다")
    void createNewUser() {
        // given
        Auth auth = createValidAuth();
        Profile profile = createValidProfile();
        Location location = createValidLocation();

        // when
        User user = User.create(auth, profile, location);

        // then
        assertThat(user.getUserId()).isNull();
        assertThat(user.getAuth()).isEqualTo(auth);
        assertThat(user.getUserRole()).isEqualTo(UserRole.ROLE_USER);
        assertThat(user.getProfile()).isEqualTo(profile);
        assertThat(user.getLocation()).isEqualTo(location);
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("기존 사용자 정보로 User를 복원할 수 있다")
    void restoreExistingUser() {
        // given
        UserId userId = UserId.of("user123");
        Auth auth = createValidAuth();
        UserRole userRole = UserRole.ROLE_ADMIN;
        Profile profile = createValidProfile();
        Location location = createValidLocation();
        UserStatus userStatus = UserStatus.ACTIVE;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        // when
        User user = User.restore(userId, auth, userRole, profile, location, userStatus, createdAt, updatedAt);

        // then
        assertThat(user.getUserId()).isEqualTo(userId);
        assertThat(user.getAuth()).isEqualTo(auth);
        assertThat(user.getUserRole()).isEqualTo(userRole);
        assertThat(user.getProfile()).isEqualTo(profile);
        assertThat(user.getLocation()).isEqualTo(location);
        assertThat(user.getUserStatus()).isEqualTo(userStatus);
        assertThat(user.getCreatedAt()).isEqualTo(createdAt);
        assertThat(user.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("비밀번호를 변경할 수 있다")
    void changePassword() {
        // given
        User user = User.create(createValidAuth(), createValidProfile(), createValidLocation());
        String newEncodedPassword = "newEncodedPassword456";
        LocalDateTime beforeUpdate = user.getUpdatedAt();

        // when
        User updatedUser = user.changePassword(newEncodedPassword);

        // then
        assertThat(updatedUser).isSameAs(user);
        assertThat(user.getPassword()).isEqualTo(newEncodedPassword);
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getUpdatedAt()).isAfter(beforeUpdate);
    }

    @Test
    @DisplayName("null 비밀번호로 변경 시 예외가 발생한다")
    void changePasswordWithNull() {
        // given
        User user = User.create(createValidAuth(), createValidProfile(), createValidLocation());

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> user.changePassword(null));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_PASSWORD_FORMAT);
    }

    @Test
    @DisplayName("빈 비밀번호로 변경 시 예외가 발생한다")
    void changePasswordWithEmpty() {
        // given
        User user = User.create(createValidAuth(), createValidProfile(), createValidLocation());

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> user.changePassword(""));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_PASSWORD_FORMAT);
    }

    @Test
    @DisplayName("공백 비밀번호로 변경 시 예외가 발생한다")
    void changePasswordWithBlank() {
        // given
        User user = User.create(createValidAuth(), createValidProfile(), createValidLocation());

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> user.changePassword("   "));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_PASSWORD_FORMAT);
    }

    @Test
    @DisplayName("프로필을 업데이트할 수 있다")
    void updateProfile() {
        // given
        User user = User.create(createValidAuth(), createValidProfile(), createValidLocation());
        Profile newProfile = Profile.of("새로운닉네임", "https://new.example.com/image.jpg", "JP");
        LocalDateTime beforeUpdate = user.getUpdatedAt();

        // when
        User updatedUser = user.updateProfile(newProfile);

        // then
        assertThat(updatedUser).isSameAs(user);
        assertThat(user.getProfile()).isEqualTo(newProfile);
        assertThat(user.getNickname()).isEqualTo("새로운닉네임");
        assertThat(user.getImageUrl()).isEqualTo("https://new.example.com/image.jpg");
        assertThat(user.getInterestCountryId()).isEqualTo("JP");
        assertThat(user.getUpdatedAt()).isAfter(beforeUpdate);
    }

    @Test
    @DisplayName("null 프로필로 업데이트 시 예외가 발생한다")
    void updateProfileWithNull() {
        // given
        User user = User.create(createValidAuth(), createValidProfile(), createValidLocation());

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> user.updateProfile(null));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_NICKNAME);
    }

    @Test
    @DisplayName("위치를 업데이트할 수 있다")
    void updateLocation() {
        // given
        User user = User.create(createValidAuth(), createValidProfile(), createValidLocation());
        Location newLocation = Location.of("JP", "도쿄");
        LocalDateTime beforeUpdate = user.getUpdatedAt();

        // when
        User updatedUser = user.updateLocation(newLocation);

        // then
        assertThat(updatedUser).isSameAs(user);
        assertThat(user.getLocation()).isEqualTo(newLocation);
        assertThat(user.getCountryId()).isEqualTo("JP");
        assertThat(user.getRegion()).isEqualTo("도쿄");
        assertThat(user.getUpdatedAt()).isAfter(beforeUpdate);
    }

    @Test
    @DisplayName("null 위치로 업데이트 시 예외가 발생한다")
    void updateLocationWithNull() {
        // given
        User user = User.create(createValidAuth(), createValidProfile(), createValidLocation());

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> user.updateLocation(null));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_REGION);
    }

    @Test
    @DisplayName("사용자 상태를 변경할 수 있다")
    void changeStatus() {
        // given
        User user = User.create(createValidAuth(), createValidProfile(), createValidLocation());
        LocalDateTime beforeUpdate = user.getUpdatedAt();

        // when
        User updatedUser = user.changeStatus(UserStatus.ACTIVE);

        // then
        assertThat(updatedUser).isSameAs(user);
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getUpdatedAt()).isAfter(beforeUpdate);
    }

    @Test
    @DisplayName("null 상태로 변경 시 변경되지 않는다")
    void changeStatusWithNull() {
        // given
        User user = User.create(createValidAuth(), createValidProfile(), createValidLocation());
        UserStatus originalStatus = user.getUserStatus();

        // when
        User updatedUser = user.changeStatus(null);

        // then
        assertThat(updatedUser).isSameAs(user);
        assertThat(user.getUserStatus()).isEqualTo(originalStatus);
    }

    @Test
    @DisplayName("같은 상태로 변경 시 변경되지 않는다")
    void changeStatusWithSameStatus() {
        // given
        User user = User.create(createValidAuth(), createValidProfile(), createValidLocation());
        UserStatus currentStatus = user.getUserStatus();

        // when
        User updatedUser = user.changeStatus(currentStatus);

        // then
        assertThat(updatedUser).isSameAs(user);
        assertThat(user.getUserStatus()).isEqualTo(currentStatus);
    }

    @Test
    @DisplayName("PENDING 상태인 사용자를 활성화할 수 있다")
    void activatePendingUser() {
        // given
        User user = User.create(createValidAuth(), createValidProfile(), createValidLocation());
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.PENDING);

        // when
        User activatedUser = user.activate();

        // then
        assertThat(activatedUser).isSameAs(user);
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("사용자를 차단할 수 있다")
    void blockUser() {
        // given
        User user = User.create(createValidAuth(), createValidProfile(), createValidLocation());

        // when
        User blockedUser = user.block();

        // then
        assertThat(blockedUser).isSameAs(user);
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.BLOCKED);
    }

    @Test
    @DisplayName("관리자 권한을 검증할 수 있다")
    void validateAdminRole() {
        // given
        User adminUser = User.restore(
                UserId.of("admin123"), createValidAuth(), UserRole.ROLE_ADMIN,
                createValidProfile(), createValidLocation(), UserStatus.ACTIVE,
                LocalDateTime.now(), LocalDateTime.now()
        );

        // when & then
        assertDoesNotThrow(adminUser::validateAdminRole);
    }

    @Test
    @DisplayName("일반 사용자가 관리자 권한 검증 시 예외가 발생한다")
    void validateAdminRoleWithRegularUser() {
        // given
        User regularUser = User.create(createValidAuth(), createValidProfile(), createValidLocation());

        // when & then
        UserException exception = assertThrows(UserException.class, regularUser::validateAdminRole);
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.NOT_AN_ADMIN_USER);
    }

    @Test
    @DisplayName("활성 상태를 검증할 수 있다")
    void validateActiveStatus() {
        // given
        User activeUser = User.restore(
                UserId.of("user123"), createValidAuth(), UserRole.ROLE_USER,
                createValidProfile(), createValidLocation(), UserStatus.ACTIVE,
                LocalDateTime.now(), LocalDateTime.now()
        );

        // when & then
        assertDoesNotThrow(activeUser::validateActiveStatus);
    }

    @Test
    @DisplayName("비활성 상태의 사용자가 활성 상태 검증 시 예외가 발생한다")
    void validateActiveStatusWithInactiveUser() {
        // given
        User inactiveUser = User.create(createValidAuth(), createValidProfile(), createValidLocation());
        assertThat(inactiveUser.getUserStatus()).isEqualTo(UserStatus.PENDING);

        // when & then
        UserException exception = assertThrows(UserException.class, inactiveUser::validateActiveStatus);
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_STATUS_NOT_ACTIVE);
    }

    @Test
    @DisplayName("같은 사용자인지 확인할 수 있다")
    void isSameUser() {
        // given
        UserId userId = UserId.of("user123");
        User user = User.restore(
                userId, createValidAuth(), UserRole.ROLE_USER,
                createValidProfile(), createValidLocation(), UserStatus.ACTIVE,
                LocalDateTime.now(), LocalDateTime.now()
        );

        // when & then
        assertTrue(user.isSameUser(userId));
        assertTrue(user.isSameUser(UserId.of("user123")));
        assertFalse(user.isSameUser(UserId.of("user456")));
        assertFalse(user.isSameUser(null));
    }

    @Test
    @DisplayName("ID가 null인 신규 사용자는 모든 비교에서 false를 반환한다")
    void isSameUserWithNullId() {
        // given
        User newUser = User.create(createValidAuth(), createValidProfile(), createValidLocation());
        assertThat(newUser.getUserId()).isNull();

        // when & then
        assertFalse(newUser.isSameUser(UserId.of("user123")));
        assertFalse(newUser.isSameUser(null));
    }

    @Test
    @DisplayName("모든 getter 메서드가 정상 작동한다")
    void allGetterMethods() {
        // given
        UserId userId = UserId.of("user123");
        Auth auth = createValidAuth();
        UserRole userRole = UserRole.ROLE_ADMIN;
        Profile profile = createValidProfile();
        Location location = createValidLocation();
        UserStatus userStatus = UserStatus.ACTIVE;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        User user = User.restore(userId, auth, userRole, profile, location, userStatus, createdAt, updatedAt);

        // when & then
        assertThat(user.getUserId()).isEqualTo(userId);
        assertThat(user.getEmail()).isEqualTo(auth.email());
        assertThat(user.getPassword()).isEqualTo(auth.password());
        assertThat(user.getUserRole()).isEqualTo(userRole);
        assertThat(user.getNickname()).isEqualTo(profile.nickname());
        assertThat(user.getImageUrl()).isEqualTo(profile.imageUrl());
        assertThat(user.getInterestCountryId()).isEqualTo(profile.interestCountryId());
        assertThat(user.getCountryId()).isEqualTo(location.countryId());
        assertThat(user.getRegion()).isEqualTo(location.region());
        assertThat(user.getUserStatus()).isEqualTo(userStatus);
        assertThat(user.getCreatedAt()).isEqualTo(createdAt);
        assertThat(user.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(user.getAuth()).isEqualTo(auth);
        assertThat(user.getProfile()).isEqualTo(profile);
        assertThat(user.getLocation()).isEqualTo(location);
    }

    @Test
    @DisplayName("연속적인 업데이트 작업이 정상 동작한다")
    void chainedUpdates() {
        // given
        User user = User.create(createValidAuth(), createValidProfile(), createValidLocation());
        Profile newProfile = Profile.of("새닉네임", "https://new.com/image.jpg", "JP");
        Location newLocation = Location.of("JP", "오사카");

        // when
        user.updateProfile(newProfile)
                .updateLocation(newLocation)
                .changeStatus(UserStatus.ACTIVE)
                .changePassword("newPassword123");

        // then
        assertThat(user.getProfile()).isEqualTo(newProfile);
        assertThat(user.getLocation()).isEqualTo(newLocation);
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getPassword()).isEqualTo("newPassword123");
    }
}