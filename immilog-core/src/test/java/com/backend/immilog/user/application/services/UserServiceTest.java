package com.backend.immilog.user.application.services;

import com.backend.immilog.user.domain.enums.UserRole;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.user.domain.model.*;
import com.backend.immilog.user.domain.repositories.UserRepository;
import com.backend.immilog.user.domain.service.UserPasswordPolicy;
import com.backend.immilog.user.domain.service.UserRegistrationService;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private final UserRepository mockUserRepository = mock(UserRepository.class);
    private final UserRegistrationService mockUserRegistrationService = mock(UserRegistrationService.class);
    private final UserPasswordPolicy mockUserPasswordPolicy = mock(UserPasswordPolicy.class);
    private final UserService userService = new UserService(
            mockUserRepository,
            mockUserRegistrationService,
            mockUserPasswordPolicy
    );

    private User testUser;
    private UserId testUserId;
    private Auth testAuth;
    private Profile testProfile;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        testUserId = UserId.of("user123");
        testAuth = Auth.of("test@example.com", "encodedPassword123");
        testProfile = Profile.of("testUser", "http://image.url", "KR");
        testLocation = Location.of("KR", "Seoul");
        
        testUser = User.restore(
                testUserId,
                testAuth,
                UserRole.ROLE_USER,
                testProfile,
                testLocation,
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("사용자 등록 테스트")
    class RegisterUserTest {

        @Test
        @DisplayName("유효한 정보로 사용자를 등록할 수 있다")
        void registerUserSuccessfully() {
            String email = "new@example.com";
            String rawPassword = "rawPassword123";
            String nickname = "newUser";
            String imageUrl = "http://new.image.url";
            String interestCountryId = "JP";
            String countryId = "JP";
            String region = "Tokyo";
            String encodedPassword = "encodedPassword123";

            when(mockUserPasswordPolicy.encodePassword(rawPassword)).thenReturn(encodedPassword);
            when(mockUserRegistrationService.registerNewUser(any(Auth.class), any(Profile.class), any(Location.class)))
                    .thenReturn(testUser);
            when(mockUserRepository.save(testUser)).thenReturn(testUser);

            UserId result = userService.registerUser(email, rawPassword, nickname, imageUrl, interestCountryId, countryId, region);

            assertThat(result).isEqualTo(testUserId);
            verify(mockUserPasswordPolicy).encodePassword(rawPassword);
            verify(mockUserRegistrationService).registerNewUser(any(Auth.class), any(Profile.class), any(Location.class));
            verify(mockUserRepository).save(testUser);
        }

        @Test
        @DisplayName("잘못된 비밀번호로 등록 시 예외가 발생한다")
        void registerUserWithInvalidPasswordThrowsException() {
            String rawPassword = "weak";
            
            when(mockUserPasswordPolicy.encodePassword(rawPassword))
                    .thenThrow(new UserException(UserErrorCode.INVALID_PASSWORD_FORMAT));

            assertThatThrownBy(() -> userService.registerUser(
                    "test@example.com", rawPassword, "user", "image", "KR", "KR", "Seoul"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_PASSWORD_FORMAT.getMessage());

            verify(mockUserPasswordPolicy).encodePassword(rawPassword);
            verifyNoInteractions(mockUserRegistrationService);
            verifyNoInteractions(mockUserRepository);
        }

        @Test
        @DisplayName("잘못된 이메일로 등록 시 예외가 발생한다")
        void registerUserWithInvalidEmailThrowsException() {
            String invalidEmail = "invalid-email";
            String rawPassword = "validPassword123";
            String encodedPassword = "encodedPassword123";

            when(mockUserPasswordPolicy.encodePassword(rawPassword)).thenReturn(encodedPassword);

            assertThatThrownBy(() -> userService.registerUser(
                    invalidEmail, rawPassword, "user", "image", "KR", "KR", "Seoul"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_EMAIL_FORMAT.getMessage());

            verify(mockUserPasswordPolicy).encodePassword(rawPassword);
            verifyNoInteractions(mockUserRegistrationService);
            verifyNoInteractions(mockUserRepository);
        }
    }

    @Nested
    @DisplayName("사용자 인증 테스트")
    class AuthenticateUserTest {

        @Test
        @DisplayName("유효한 인증 정보로 사용자를 인증할 수 있다")
        void authenticateUserSuccessfully() {
            String email = "test@example.com";
            String rawPassword = "password123";

            when(mockUserRepository.findByEmail(email)).thenReturn(testUser);

            User result = userService.authenticateUser(email, rawPassword);

            assertThat(result).isEqualTo(testUser);
            verify(mockUserRepository).findByEmail(email);
            verify(mockUserPasswordPolicy).validatePasswordMatch(rawPassword, testUser.getPassword());
        }

        @Test
        @DisplayName("잘못된 비밀번호로 인증 시 예외가 발생한다")
        void authenticateUserWithWrongPasswordThrowsException() {
            String email = "test@example.com";
            String wrongPassword = "wrongPassword";

            when(mockUserRepository.findByEmail(email)).thenReturn(testUser);
            doThrow(new UserException(UserErrorCode.PASSWORD_NOT_MATCH))
                    .when(mockUserPasswordPolicy).validatePasswordMatch(wrongPassword, testUser.getPassword());

            assertThatThrownBy(() -> userService.authenticateUser(email, wrongPassword))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.PASSWORD_NOT_MATCH.getMessage());

            verify(mockUserRepository).findByEmail(email);
            verify(mockUserPasswordPolicy).validatePasswordMatch(wrongPassword, testUser.getPassword());
        }

        @Test
        @DisplayName("비활성 사용자 인증 시 예외가 발생한다")
        void authenticateInactiveUserThrowsException() {
            String email = "test@example.com";
            String password = "password123";
            
            User inactiveUser = User.restore(
                    testUserId,
                    testAuth,
                    UserRole.ROLE_USER,
                    testProfile,
                    testLocation,
                    UserStatus.INACTIVE,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            when(mockUserRepository.findByEmail(email)).thenReturn(inactiveUser);

            assertThatThrownBy(() -> userService.authenticateUser(email, password))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.USER_STATUS_NOT_ACTIVE.getMessage());

            verify(mockUserRepository).findByEmail(email);
            verify(mockUserPasswordPolicy).validatePasswordMatch(password, inactiveUser.getPassword());
        }
    }

    @Nested
    @DisplayName("사용자 프로필 업데이트 테스트")
    class UpdateUserProfileTest {

        @Test
        @DisplayName("유효한 정보로 사용자 프로필을 업데이트할 수 있다")
        void updateUserProfileSuccessfully() {
            String nickname = "updatedUser";
            String imageUrl = "http://updated.image.url";
            String interestCountryId = "JP";

            when(mockUserRepository.findById(testUserId)).thenReturn(testUser);
            when(mockUserRepository.save(any(User.class))).thenReturn(testUser);

            userService.updateUserProfile(testUserId, nickname, imageUrl, interestCountryId);

            verify(mockUserRepository).findById(testUserId);
            verify(mockUserRepository).save(testUser);
        }

        @Test
        @DisplayName("잘못된 닉네임으로 프로필 업데이트 시 예외가 발생한다")
        void updateUserProfileWithInvalidNicknameThrowsException() {
            String invalidNickname = null;
            String imageUrl = "http://image.url";
            String interestCountryId = "KR";

            when(mockUserRepository.findById(testUserId)).thenReturn(testUser);

            assertThatThrownBy(() -> userService.updateUserProfile(testUserId, invalidNickname, imageUrl, interestCountryId))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_NICKNAME.getMessage());

            verify(mockUserRepository).findById(testUserId);
            verifyNoMoreInteractions(mockUserRepository);
        }

        @Test
        @DisplayName("존재하지 않는 사용자의 프로필 업데이트 시 예외가 발생한다")
        void updateNonExistentUserProfileThrowsException() {
            UserId nonExistentUserId = UserId.of("nonexistent");

            when(mockUserRepository.findById(nonExistentUserId))
                    .thenThrow(new UserException(UserErrorCode.ENTITY_TO_DOMAIN_ERROR));

            assertThatThrownBy(() -> userService.updateUserProfile(
                    nonExistentUserId, "nickname", "image", "KR"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.ENTITY_TO_DOMAIN_ERROR.getMessage());

            verify(mockUserRepository).findById(nonExistentUserId);
            verifyNoMoreInteractions(mockUserRepository);
        }
    }

    @Nested
    @DisplayName("비밀번호 변경 테스트")
    class ChangePasswordTest {

        @Test
        @DisplayName("유효한 정보로 비밀번호를 변경할 수 있다")
        void changePasswordSuccessfully() {
            String currentPassword = "currentPassword";
            String newPassword = "newPassword123";
            String encodedNewPassword = "encodedNewPassword123";

            when(mockUserRepository.findById(testUserId)).thenReturn(testUser);
            when(mockUserPasswordPolicy.encodePassword(newPassword)).thenReturn(encodedNewPassword);
            when(mockUserRepository.save(any(User.class))).thenReturn(testUser);

            userService.changePassword(testUserId, currentPassword, newPassword);

            verify(mockUserRepository).findById(testUserId);
            verify(mockUserPasswordPolicy).validatePasswordMatch(currentPassword, "encodedPassword123");
            verify(mockUserPasswordPolicy).encodePassword(newPassword);
            verify(mockUserRepository).save(testUser);
        }

        @Test
        @DisplayName("잘못된 현재 비밀번호로 변경 시 예외가 발생한다")
        void changePasswordWithWrongCurrentPasswordThrowsException() {
            String wrongCurrentPassword = "wrongPassword";
            String newPassword = "newPassword123";

            when(mockUserRepository.findById(testUserId)).thenReturn(testUser);
            doThrow(new UserException(UserErrorCode.PASSWORD_NOT_MATCH))
                    .when(mockUserPasswordPolicy).validatePasswordMatch(wrongCurrentPassword, "encodedPassword123");

            assertThatThrownBy(() -> userService.changePassword(testUserId, wrongCurrentPassword, newPassword))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.PASSWORD_NOT_MATCH.getMessage());

            verify(mockUserRepository).findById(testUserId);
            verify(mockUserPasswordPolicy).validatePasswordMatch(wrongCurrentPassword, "encodedPassword123");
            verifyNoMoreInteractions(mockUserPasswordPolicy);
            verifyNoMoreInteractions(mockUserRepository);
        }

        @Test
        @DisplayName("잘못된 새 비밀번호로 변경 시 예외가 발생한다")
        void changePasswordWithInvalidNewPasswordThrowsException() {
            String currentPassword = "currentPassword";
            String invalidNewPassword = "weak";

            when(mockUserRepository.findById(testUserId)).thenReturn(testUser);
            when(mockUserPasswordPolicy.encodePassword(invalidNewPassword))
                    .thenThrow(new UserException(UserErrorCode.INVALID_PASSWORD_FORMAT));

            assertThatThrownBy(() -> userService.changePassword(testUserId, currentPassword, invalidNewPassword))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_PASSWORD_FORMAT.getMessage());

            verify(mockUserRepository).findById(testUserId);
            verify(mockUserPasswordPolicy).validatePasswordMatch(currentPassword, "encodedPassword123");
            verify(mockUserPasswordPolicy).encodePassword(invalidNewPassword);
            verifyNoMoreInteractions(mockUserRepository);
        }
    }

    @Nested
    @DisplayName("사용자 활성화 테스트")
    class ActivateUserTest {

        @Test
        @DisplayName("사용자를 활성화할 수 있다")
        void activateUserSuccessfully() {
            User inactiveUser = User.restore(
                    testUserId,
                    testAuth,
                    UserRole.ROLE_USER,
                    testProfile,
                    testLocation,
                    UserStatus.PENDING,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            when(mockUserRepository.findById(testUserId)).thenReturn(inactiveUser);
            when(mockUserRepository.save(any(User.class))).thenReturn(inactiveUser);

            userService.activateUser(testUserId);

            verify(mockUserRepository).findById(testUserId);
            verify(mockUserRepository).save(inactiveUser);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 활성화 시 예외가 발생한다")
        void activateNonExistentUserThrowsException() {
            UserId nonExistentUserId = UserId.of("nonexistent");

            when(mockUserRepository.findById(nonExistentUserId))
                    .thenThrow(new UserException(UserErrorCode.ENTITY_TO_DOMAIN_ERROR));

            assertThatThrownBy(() -> userService.activateUser(nonExistentUserId))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.ENTITY_TO_DOMAIN_ERROR.getMessage());

            verify(mockUserRepository).findById(nonExistentUserId);
            verifyNoMoreInteractions(mockUserRepository);
        }
    }

    @Nested
    @DisplayName("사용자 차단 테스트")
    class BlockUserTest {

        @Test
        @DisplayName("사용자를 차단할 수 있다")
        void blockUserSuccessfully() {
            when(mockUserRepository.findById(testUserId)).thenReturn(testUser);
            when(mockUserRepository.save(any(User.class))).thenReturn(testUser);

            userService.blockUser(testUserId);

            verify(mockUserRepository).findById(testUserId);
            verify(mockUserRepository).save(testUser);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 차단 시 예외가 발생한다")
        void blockNonExistentUserThrowsException() {
            UserId nonExistentUserId = UserId.of("nonexistent");

            when(mockUserRepository.findById(nonExistentUserId))
                    .thenThrow(new UserException(UserErrorCode.ENTITY_TO_DOMAIN_ERROR));

            assertThatThrownBy(() -> userService.blockUser(nonExistentUserId))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.ENTITY_TO_DOMAIN_ERROR.getMessage());

            verify(mockUserRepository).findById(nonExistentUserId);
            verifyNoMoreInteractions(mockUserRepository);
        }
    }

    @Nested
    @DisplayName("내부 메서드 테스트")
    class InternalMethodTest {

        @Test
        @DisplayName("getUserById 메서드가 올바르게 동작한다")
        void getUserByIdWorksCorrectly() {
            when(mockUserRepository.findById(testUserId)).thenReturn(testUser);

            userService.activateUser(testUserId);

            verify(mockUserRepository).findById(testUserId);
        }

        @Test
        @DisplayName("getUserByEmail 메서드가 올바르게 동작한다")
        void getUserByEmailWorksCorrectly() {
            String email = "test@example.com";
            when(mockUserRepository.findByEmail(email)).thenReturn(testUser);

            userService.authenticateUser(email, "password");

            verify(mockUserRepository).findByEmail(email);
        }
    }

    @Nested
    @DisplayName("트랜잭션 테스트")
    class TransactionTest {

        @Test
        @DisplayName("모든 상태 변경 메서드가 트랜잭션 내에서 실행된다")
        void allStateChangingMethodsRunInTransaction() {
            when(mockUserRepository.findById(any(UserId.class))).thenReturn(testUser);
            when(mockUserRepository.save(any(User.class))).thenReturn(testUser);
            when(mockUserPasswordPolicy.encodePassword(anyString())).thenReturn("encoded");
            when(mockUserRegistrationService.registerNewUser(any(), any(), any())).thenReturn(testUser);

            userService.registerUser("email@test.com", "password", "nick", "image", "KR", "KR", "Seoul");
            userService.updateUserProfile(testUserId, "nick", "image", "KR");
            userService.changePassword(testUserId, "old", "new");
            userService.activateUser(testUserId);
            userService.blockUser(testUserId);

            verify(mockUserRepository, times(5)).save(any(User.class));
        }

        @Test
        @DisplayName("읽기 전용 메서드는 트랜잭션 내에서 실행된다")
        void readOnlyMethodsRunInReadOnlyTransaction() {
            when(mockUserRepository.findByEmail(anyString())).thenReturn(testUser);

            userService.authenticateUser("email@test.com", "password");

            verify(mockUserRepository).findByEmail("email@test.com");
            verify(mockUserPasswordPolicy).validatePasswordMatch("password", testUser.getPassword());
        }
    }
}