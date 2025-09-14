package com.backend.immilog.user.domain.model;

import com.backend.immilog.user.domain.enums.UserRole;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private Auth validAuth;
    private Profile validProfile;
    private Location validLocation;
    private LocalDateTime fixedTime;

    @BeforeEach
    void setUp() {
        validAuth = Auth.of("test@example.com", "encodedPassword123");
        validProfile = Profile.of("testUser", "http://image.url", "KR");
        validLocation = Location.of("KR", "Seoul");
        fixedTime = LocalDateTime.of(2023, 1, 1, 12, 0);
    }

    @Nested
    @DisplayName("User 생성 테스트")
    class UserCreationTest {

        @Test
        @DisplayName("create 메서드로 새 User를 생성할 수 있다")
        void createNewUser() {
            User user = User.create(validAuth, validProfile, validLocation);

            assertThat(user).isNotNull();
            assertThat(user.getUserId()).isNull();
            assertThat(user.getEmail()).isEqualTo("test@example.com");
            assertThat(user.getNickname()).isEqualTo("testUser");
            assertThat(user.getUserRole()).isEqualTo(UserRole.ROLE_USER);
            assertThat(user.getUserStatus()).isEqualTo(UserStatus.PENDING);
            assertThat(user.getCreatedAt()).isNotNull();
            assertThat(user.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("restore 메서드로 기존 User를 복원할 수 있다")
        void restoreExistingUser() {
            UserId userId = UserId.of("user123");
            
            User user = User.restore(
                    userId,
                    validAuth,
                    UserRole.ROLE_ADMIN,
                    validProfile,
                    validLocation,
                    UserStatus.ACTIVE,
                    fixedTime,
                    fixedTime
            );

            assertThat(user.getUserId()).isEqualTo(userId);
            assertThat(user.getUserRole()).isEqualTo(UserRole.ROLE_ADMIN);
            assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(user.getCreatedAt()).isEqualTo(fixedTime);
            assertThat(user.getUpdatedAt()).isEqualTo(fixedTime);
        }
    }

    @Nested
    @DisplayName("User 비밀번호 변경 테스트")
    class PasswordChangeTest {

        @Test
        @DisplayName("유효한 비밀번호로 변경할 수 있다")
        void changePasswordSuccessfully() {
            User user = User.create(validAuth, validProfile, validLocation);
            String newPassword = "newEncodedPassword456";

            User updatedUser = user.changePassword(newPassword);

            assertThat(updatedUser.getPassword()).isEqualTo(newPassword);
            assertThat(updatedUser.getEmail()).isEqualTo("test@example.com");
            assertThat(updatedUser.getUpdatedAt()).isAfter(updatedUser.getCreatedAt());
        }

        @Test
        @DisplayName("null 비밀번호로 변경 시 예외가 발생한다")
        void changePasswordWithNullThrowsException() {
            User user = User.create(validAuth, validProfile, validLocation);

            assertThatThrownBy(() -> user.changePassword(null))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_PASSWORD_FORMAT.getMessage());
        }

        @Test
        @DisplayName("빈 비밀번호로 변경 시 예외가 발생한다")
        void changePasswordWithEmptyThrowsException() {
            User user = User.create(validAuth, validProfile, validLocation);

            assertThatThrownBy(() -> user.changePassword("   "))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_PASSWORD_FORMAT.getMessage());
        }
    }

    @Nested
    @DisplayName("User 프로필 업데이트 테스트")
    class ProfileUpdateTest {

        @Test
        @DisplayName("유효한 프로필로 업데이트할 수 있다")
        void updateProfileSuccessfully() {
            User user = User.create(validAuth, validProfile, validLocation);
            Profile newProfile = Profile.of("newNick", "http://new.image", "JP");

            User updatedUser = user.updateProfile(newProfile);

            assertThat(updatedUser.getNickname()).isEqualTo("newNick");
            assertThat(updatedUser.getImageUrl()).isEqualTo("http://new.image");
            assertThat(updatedUser.getInterestCountryId()).isEqualTo("JP");
            assertThat(updatedUser.getUpdatedAt()).isAfter(updatedUser.getCreatedAt());
        }

        @Test
        @DisplayName("null 프로필로 업데이트 시 예외가 발생한다")
        void updateProfileWithNullThrowsException() {
            User user = User.create(validAuth, validProfile, validLocation);

            assertThatThrownBy(() -> user.updateProfile(null))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_NICKNAME.getMessage());
        }
    }

    @Nested
    @DisplayName("User 위치 업데이트 테스트")
    class LocationUpdateTest {

        @Test
        @DisplayName("유효한 위치로 업데이트할 수 있다")
        void updateLocationSuccessfully() {
            User user = User.create(validAuth, validProfile, validLocation);
            Location newLocation = Location.of("JP", "Tokyo");

            User updatedUser = user.updateLocation(newLocation);

            assertThat(updatedUser.getCountryId()).isEqualTo("JP");
            assertThat(updatedUser.getRegion()).isEqualTo("Tokyo");
            assertThat(updatedUser.getUpdatedAt()).isAfter(updatedUser.getCreatedAt());
        }

        @Test
        @DisplayName("null 위치로 업데이트 시 예외가 발생한다")
        void updateLocationWithNullThrowsException() {
            User user = User.create(validAuth, validProfile, validLocation);

            assertThatThrownBy(() -> user.updateLocation(null))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_REGION.getMessage());
        }
    }

    @Nested
    @DisplayName("User 상태 변경 테스트")
    class StatusChangeTest {

        @Test
        @DisplayName("다른 상태로 변경할 수 있다")
        void changeStatusSuccessfully() {
            User user = User.create(validAuth, validProfile, validLocation);

            User updatedUser = user.changeStatus(UserStatus.ACTIVE);

            assertThat(updatedUser.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(updatedUser.getUpdatedAt()).isAfter(updatedUser.getCreatedAt());
        }

        @Test
        @DisplayName("동일한 상태로 변경 시 변경되지 않는다")
        void changeStatusToSameStatusDoesNothing() {
            User user = User.create(validAuth, validProfile, validLocation);
            LocalDateTime originalUpdatedAt = user.getUpdatedAt();

            User updatedUser = user.changeStatus(UserStatus.PENDING);

            assertThat(updatedUser.getUserStatus()).isEqualTo(UserStatus.PENDING);
            assertThat(updatedUser.getUpdatedAt()).isEqualTo(originalUpdatedAt);
        }

        @Test
        @DisplayName("null 상태로 변경 시 변경되지 않는다")
        void changeStatusWithNullDoesNothing() {
            User user = User.create(validAuth, validProfile, validLocation);
            LocalDateTime originalUpdatedAt = user.getUpdatedAt();

            User updatedUser = user.changeStatus(null);

            assertThat(updatedUser.getUserStatus()).isEqualTo(UserStatus.PENDING);
            assertThat(updatedUser.getUpdatedAt()).isEqualTo(originalUpdatedAt);
        }

        @Test
        @DisplayName("activate 메서드로 활성화할 수 있다")
        void activateUser() {
            User user = User.create(validAuth, validProfile, validLocation);

            User activatedUser = user.activate();

            assertThat(activatedUser.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        @DisplayName("block 메서드로 차단할 수 있다")
        void blockUser() {
            User user = User.create(validAuth, validProfile, validLocation);

            User blockedUser = user.block();

            assertThat(blockedUser.getUserStatus()).isEqualTo(UserStatus.BLOCKED);
        }
    }

    @Nested
    @DisplayName("User 권한 검증 테스트")
    class AuthorizationTest {

        @Test
        @DisplayName("관리자 권한 검증이 성공한다")
        void validateAdminRoleSucceeds() {
            User adminUser = User.restore(
                    UserId.of("admin123"),
                    validAuth,
                    UserRole.ROLE_ADMIN,
                    validProfile,
                    validLocation,
                    UserStatus.ACTIVE,
                    fixedTime,
                    fixedTime
            );

            adminUser.validateAdminRole();
        }

        @Test
        @DisplayName("일반 사용자의 관리자 권한 검증이 실패한다")
        void validateAdminRoleFailsForRegularUser() {
            User regularUser = User.create(validAuth, validProfile, validLocation);

            assertThatThrownBy(regularUser::validateAdminRole)
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.NOT_AN_ADMIN_USER.getMessage());
        }

        @Test
        @DisplayName("활성 상태 검증이 성공한다")
        void validateActiveStatusSucceeds() {
            User activeUser = User.create(validAuth, validProfile, validLocation)
                    .activate();

            activeUser.validateActiveStatus();
        }

        @Test
        @DisplayName("비활성 상태의 활성 상태 검증이 실패한다")
        void validateActiveStatusFailsForInactiveUser() {
            User inactiveUser = User.create(validAuth, validProfile, validLocation);

            assertThatThrownBy(inactiveUser::validateActiveStatus)
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.USER_STATUS_NOT_ACTIVE.getMessage());
        }
    }

    @Nested
    @DisplayName("User 동등성 테스트")
    class UserEqualityTest {

        @Test
        @DisplayName("동일한 UserId를 가진 User는 같은 사용자이다")
        void isSameUserWithSameUserId() {
            UserId userId = UserId.of("user123");
            User user = User.restore(
                    userId,
                    validAuth,
                    UserRole.ROLE_USER,
                    validProfile,
                    validLocation,
                    UserStatus.ACTIVE,
                    fixedTime,
                    fixedTime
            );

            boolean result = user.isSameUser(userId);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("다른 UserId를 가진 User는 다른 사용자이다")
        void isSameUserWithDifferentUserId() {
            UserId userId = UserId.of("user123");
            UserId otherUserId = UserId.of("user456");
            User user = User.restore(
                    userId,
                    validAuth,
                    UserRole.ROLE_USER,
                    validProfile,
                    validLocation,
                    UserStatus.ACTIVE,
                    fixedTime,
                    fixedTime
            );

            boolean result = user.isSameUser(otherUserId);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("UserId가 null인 User는 다른 사용자와 같지 않다")
        void isSameUserWithNullUserId() {
            User user = User.create(validAuth, validProfile, validLocation);
            UserId otherUserId = UserId.of("user123");

            boolean result = user.isSameUser(otherUserId);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("User 접근자 메서드 테스트")
    class AccessorMethodsTest {

        @Test
        @DisplayName("모든 접근자 메서드가 올바른 값을 반환한다")
        void allAccessorMethodsReturnCorrectValues() {
            UserId userId = UserId.of("user123");
            User user = User.restore(
                    userId,
                    validAuth,
                    UserRole.ROLE_ADMIN,
                    validProfile,
                    validLocation,
                    UserStatus.ACTIVE,
                    fixedTime,
                    fixedTime.plusHours(1)
            );

            assertThat(user.getUserId()).isEqualTo(userId);
            assertThat(user.getEmail()).isEqualTo("test@example.com");
            assertThat(user.getPassword()).isEqualTo("encodedPassword123");
            assertThat(user.getUserRole()).isEqualTo(UserRole.ROLE_ADMIN);
            assertThat(user.getNickname()).isEqualTo("testUser");
            assertThat(user.getImageUrl()).isEqualTo("http://image.url");
            assertThat(user.getInterestCountryId()).isEqualTo("KR");
            assertThat(user.getCountryId()).isEqualTo("KR");
            assertThat(user.getRegion()).isEqualTo("Seoul");
            assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(user.getCreatedAt()).isEqualTo(fixedTime);
            assertThat(user.getUpdatedAt()).isEqualTo(fixedTime.plusHours(1));
            assertThat(user.getAuth()).isEqualTo(validAuth);
            assertThat(user.getProfile()).isEqualTo(validProfile);
            assertThat(user.getLocation()).isEqualTo(validLocation);
        }
    }
}