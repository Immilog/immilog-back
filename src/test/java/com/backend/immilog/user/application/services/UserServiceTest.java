package com.backend.immilog.user.application.services;

import com.backend.immilog.shared.enums.Country;
import com.backend.immilog.user.application.services.command.UserCommandService;
import com.backend.immilog.user.application.services.query.UserQueryService;
import com.backend.immilog.user.domain.enums.UserRole;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.user.domain.model.*;
import com.backend.immilog.user.domain.service.UserPasswordPolicy;
import com.backend.immilog.user.domain.service.UserRegistrationService;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@DisplayName("UserService 테스트")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private UserCommandService userCommandService;

    @Mock
    private UserRegistrationService userRegistrationService;

    @Mock
    private UserPasswordPolicy userPasswordPolicy;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(
                userQueryService, userCommandService, userRegistrationService, userPasswordPolicy
        );
    }

    private User createMockUser(UserId userId) {
        return User.restore(
                userId,
                Auth.of("test@example.com", "encodedPassword123"),
                UserRole.ROLE_USER,
                Profile.of("테스트유저", "https://example.com/image.jpg", Country.SOUTH_KOREA),
                Location.of(Country.SOUTH_KOREA, "서울특별시"),
                UserStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("새 사용자를 정상적으로 등록할 수 있다")
    void registerUserSuccessfully() {
        // given
        String email = "test@example.com";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";
        String nickname = "테스트유저";
        String imageUrl = "https://example.com/image.jpg";
        Country interestCountry = Country.SOUTH_KOREA;
        Country country = Country.SOUTH_KOREA;
        String region = "서울특별시";

        Auth expectedAuth = Auth.of(email, encodedPassword);
        Profile expectedProfile = Profile.of(nickname, imageUrl, interestCountry);
        Location expectedLocation = Location.of(country, region);
        User newUser = User.create(expectedAuth, expectedProfile, expectedLocation);
        UserId expectedUserId = UserId.of("user123");
        User savedUser = createMockUser(expectedUserId);

        given(userPasswordPolicy.encodePassword(rawPassword)).willReturn(encodedPassword);
        given(userRegistrationService.registerNewUser(any(Auth.class), any(Profile.class), any(Location.class)))
                .willReturn(newUser);
        given(userCommandService.save(newUser)).willReturn(savedUser);

        // when
        UserId result = userService.registerUser(email, rawPassword, nickname, imageUrl, interestCountry, country, region);

        // then
        assertThat(result).isEqualTo(expectedUserId);

        verify(userPasswordPolicy).encodePassword(rawPassword);
        verify(userRegistrationService).registerNewUser(any(Auth.class), any(Profile.class), any(Location.class));
        verify(userCommandService).save(newUser);

        // Auth, Profile, Location 객체가 올바르게 생성되었는지 검증
        ArgumentCaptor<Auth> authCaptor = ArgumentCaptor.forClass(Auth.class);
        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
        ArgumentCaptor<Location> locationCaptor = ArgumentCaptor.forClass(Location.class);

        verify(userRegistrationService).registerNewUser(authCaptor.capture(), profileCaptor.capture(), locationCaptor.capture());

        assertThat(authCaptor.getValue().email()).isEqualTo(email);
        assertThat(authCaptor.getValue().password()).isEqualTo(encodedPassword);
        assertThat(profileCaptor.getValue().nickname()).isEqualTo(nickname);
        assertThat(profileCaptor.getValue().imageUrl()).isEqualTo(imageUrl);
        assertThat(profileCaptor.getValue().interestCountry()).isEqualTo(interestCountry);
        assertThat(locationCaptor.getValue().country()).isEqualTo(country);
        assertThat(locationCaptor.getValue().region()).isEqualTo(region);
    }

    @Test
    @DisplayName("사용자 인증을 정상적으로 처리할 수 있다")
    void authenticateUserSuccessfully() {
        // given
        String email = "test@example.com";
        String rawPassword = "password123";
        UserId userId = UserId.of("user123");
        User mockUser = createMockUser(userId);

        given(userQueryService.getUserByEmail(email)).willReturn(mockUser);

        // when
        User result = userService.authenticateUser(email, rawPassword);

        // then
        assertThat(result).isEqualTo(mockUser);

        verify(userQueryService).getUserByEmail(email);
        verify(userPasswordPolicy).validatePasswordMatch(rawPassword, "encodedPassword123");
    }

    @Test
    @DisplayName("비활성 사용자 인증 시 예외가 발생한다")
    void authenticateInactiveUserThrowsException() {
        // given
        String email = "test@example.com";
        String rawPassword = "password123";
        UserId userId = UserId.of("user123");
        User inactiveUser = User.restore(
                userId,
                Auth.of(email, "encodedPassword123"),
                UserRole.ROLE_USER,
                Profile.of("테스트유저", null, Country.SOUTH_KOREA),
                Location.of(Country.SOUTH_KOREA, "서울특별시"),
                UserStatus.PENDING, // 비활성 상태
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        given(userQueryService.getUserByEmail(email)).willReturn(inactiveUser);

        // when & then
        assertThatThrownBy(() -> userService.authenticateUser(email, rawPassword))
                .isInstanceOf(UserException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.USER_STATUS_NOT_ACTIVE);

        verify(userQueryService).getUserByEmail(email);
        verify(userPasswordPolicy).validatePasswordMatch(rawPassword, "encodedPassword123");
    }

    @Test
    @DisplayName("사용자 프로필을 업데이트할 수 있다")
    void updateUserProfile() {
        // given
        UserId userId = UserId.of("user123");
        String newNickname = "새로운닉네임";
        String newImageUrl = "https://new.example.com/image.jpg";
        Country newInterestCountry = Country.JAPAN;

        User mockUser = createMockUser(userId);
        User updatedUser = createMockUser(userId);

        given(userQueryService.getUserById(userId)).willReturn(mockUser);
        given(userCommandService.save(any(User.class))).willReturn(updatedUser);

        // when
        userService.updateUserProfile(userId, newNickname, newImageUrl, newInterestCountry);

        // then
        verify(userQueryService).getUserById(userId);
        verify(userCommandService).save(mockUser);

        // User의 updateProfile 메서드가 호출되었는지 확인하기 위해 ArgumentCaptor 사용
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userCommandService).save(userCaptor.capture());

        // 실제로는 mockUser가 업데이트되므로 직접 확인은 어렵지만, save가 호출되었음을 확인
        assertThat(userCaptor.getValue()).isEqualTo(mockUser);
    }

    @Test
    @DisplayName("비밀번호를 변경할 수 있다")
    void changePassword() {
        // given
        UserId userId = UserId.of("user123");
        String currentPassword = "currentPassword123";
        String newPassword = "newPassword456";
        String encodedNewPassword = "encodedNewPassword456";

        User mockUser = createMockUser(userId);

        given(userQueryService.getUserById(userId)).willReturn(mockUser);
        given(userPasswordPolicy.encodePassword(newPassword)).willReturn(encodedNewPassword);
        given(userCommandService.save(any(User.class))).willReturn(mockUser);

        // when
        userService.changePassword(userId, currentPassword, newPassword);

        // then
        verify(userQueryService).getUserById(userId);
        verify(userPasswordPolicy).validatePasswordMatch(currentPassword, "encodedPassword123");
        verify(userPasswordPolicy).encodePassword(newPassword);
        verify(userCommandService).save(mockUser);
    }

    @Test
    @DisplayName("현재 비밀번호가 일치하지 않으면 변경할 수 없다")
    void changePasswordWithWrongCurrentPassword() {
        // given
        UserId userId = UserId.of("user123");
        String wrongCurrentPassword = "wrongPassword";
        String newPassword = "newPassword456";

        User mockUser = createMockUser(userId);

        given(userQueryService.getUserById(userId)).willReturn(mockUser);
        willThrow(new UserException(UserErrorCode.PASSWORD_NOT_MATCH))
                .given(userPasswordPolicy).validatePasswordMatch(wrongCurrentPassword, "encodedPassword123");

        // when & then
        assertThatThrownBy(() -> userService.changePassword(userId, wrongCurrentPassword, newPassword))
                .isInstanceOf(UserException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.PASSWORD_NOT_MATCH);

        verify(userQueryService).getUserById(userId);
        verify(userPasswordPolicy).validatePasswordMatch(wrongCurrentPassword, "encodedPassword123");
        verify(userPasswordPolicy, never()).encodePassword(anyString());
        verify(userCommandService, never()).save(any(User.class));
    }

    @Test
    @DisplayName("사용자를 활성화할 수 있다")
    void activateUser() {
        // given
        UserId userId = UserId.of("user123");
        User pendingUser = User.restore(
                userId,
                Auth.of("test@example.com", "encodedPassword123"),
                UserRole.ROLE_USER,
                Profile.of("테스트유저", null, Country.SOUTH_KOREA),
                Location.of(Country.SOUTH_KOREA, "서울특별시"),
                UserStatus.PENDING,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        given(userQueryService.getUserById(userId)).willReturn(pendingUser);
        given(userCommandService.save(any(User.class))).willReturn(pendingUser);

        // when
        userService.activateUser(userId);

        // then
        verify(userQueryService).getUserById(userId);
        verify(userCommandService).save(pendingUser);
    }

    @Test
    @DisplayName("사용자를 차단할 수 있다")
    void blockUser() {
        // given
        UserId userId = UserId.of("user123");
        User mockUser = createMockUser(userId);

        given(userQueryService.getUserById(userId)).willReturn(mockUser);
        given(userCommandService.save(any(User.class))).willReturn(mockUser);

        // when
        userService.blockUser(userId);

        // then
        verify(userQueryService).getUserById(userId);
        verify(userCommandService).save(mockUser);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회 시 예외가 전파된다")
    void getUserByIdWithNonExistentUser() {
        // given
        UserId userId = UserId.of("nonexistent");
        String newNickname = "새닉네임";

        given(userQueryService.getUserById(userId))
                .willThrow(new UserException(UserErrorCode.USER_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> userService.updateUserProfile(userId, newNickname, null, Country.SOUTH_KOREA))
                .isInstanceOf(UserException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.USER_NOT_FOUND);

        verify(userQueryService).getUserById(userId);
        verify(userCommandService, never()).save(any(User.class));
    }

    @Test
    @DisplayName("비밀번호 인코딩 실패 시 예외가 전파된다")
    void registerUserWithPasswordEncodingFailure() {
        // given
        String email = "test@example.com";
        String rawPassword = "password123";

        given(userPasswordPolicy.encodePassword(rawPassword))
                .willThrow(new UserException(UserErrorCode.INVALID_PASSWORD_FORMAT));

        // when & then
        assertThatThrownBy(() -> userService.registerUser(
                email, rawPassword, "nickname", null, Country.SOUTH_KOREA, Country.SOUTH_KOREA, "서울"
        )).isInstanceOf(UserException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.INVALID_PASSWORD_FORMAT);

        verify(userPasswordPolicy).encodePassword(rawPassword);
        verify(userRegistrationService, never()).registerNewUser(any(), any(), any());
        verify(userCommandService, never()).save(any(User.class));
    }
}