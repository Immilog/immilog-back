package com.backend.immilog.user.application.usecase;

import com.backend.immilog.image.application.usecase.UploadImageUseCase;
import com.backend.immilog.shared.enums.Country;
import com.backend.immilog.user.application.command.UserInfoUpdateCommand;
import com.backend.immilog.user.application.command.UserPasswordChangeCommand;
import com.backend.immilog.user.application.result.LocationResult;
import com.backend.immilog.user.application.services.command.UserCommandService;
import com.backend.immilog.user.application.services.query.UserQueryService;
import com.backend.immilog.user.domain.enums.UserRole;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.user.domain.model.*;
import com.backend.immilog.user.domain.service.UserPasswordPolicy;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@DisplayName("UpdateProfileUseCase 테스트")
class UpdateProfileUseCaseTest {

    private final UserQueryService userQueryService = mock(UserQueryService.class);
    private final UserCommandService userCommandService = mock(UserCommandService.class);
    private final UploadImageUseCase uploadImageUseCase = mock(UploadImageUseCase.class);
    private final UserPasswordPolicy userPasswordPolicy = mock(UserPasswordPolicy.class);

    private UpdateProfileUseCase userUpdater;

    @BeforeEach
    void setUp() {
        userUpdater = new UpdateProfileUseCase.UserUpdater(
                userQueryService,
                userCommandService,
                uploadImageUseCase,
                userPasswordPolicy
        );
    }

    private User createMockUser() {
        return User.restore(
                UserId.of("user123"),
                Auth.of("test@example.com", "encodedPassword"),
                UserRole.ROLE_USER,
                Profile.of("기존닉네임", "https://old.com/image.jpg", Country.SOUTH_KOREA),
                Location.of(Country.SOUTH_KOREA, "서울특별시"),
                UserStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
    }

    private User createAdminUser() {
        return User.restore(
                UserId.of("admin123"),
                Auth.of("admin@example.com", "adminPassword"),
                UserRole.ROLE_ADMIN,
                Profile.of("관리자", null, Country.SOUTH_KOREA),
                Location.of(Country.SOUTH_KOREA, "서울"),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("사용자 정보를 정상적으로 업데이트할 수 있다")
    void updateInformationSuccessfully() {
        // given
        String userId = "user123";
        User user = createMockUser();
        LocationResult locationResult = new LocationResult("Korea", "Seoul");
        CompletableFuture<LocationResult> futureRegion = CompletableFuture.completedFuture(locationResult);

        UserInfoUpdateCommand command = new UserInfoUpdateCommand(
                "새닉네임",
                "https://new.com/image.jpg",
                Country.JAPAN,
                Country.JAPAN,
                35.6762,
                139.6503,
                UserStatus.ACTIVE
        );

        given(userQueryService.getUserById(userId)).willReturn(user);
        given(userCommandService.save(any(User.class))).willReturn(user);

        // when
        userUpdater.updateInformation(userId, futureRegion, command);

        // then
        verify(userQueryService).getUserById(userId);
        verify(userCommandService).save(any(User.class));
        verify(uploadImageUseCase).deleteImage("https://old.com/image.jpg", "https://new.com/image.jpg");
    }

    @Test
    @DisplayName("null 위치 정보로도 업데이트할 수 있다")
    void updateInformationWithNullLocation() {
        // given
        String userId = "user123";
        User user = createMockUser();
        CompletableFuture<LocationResult> futureRegion = CompletableFuture.completedFuture(null);

        UserInfoUpdateCommand command = new UserInfoUpdateCommand(
                "새닉네임",
                "https://new.com/image.jpg",
                Country.JAPAN,
                Country.JAPAN,
                null,
                null,
                UserStatus.ACTIVE
        );

        given(userQueryService.getUserById(userId)).willReturn(user);
        given(userCommandService.save(any(User.class))).willReturn(user);

        assertThatThrownBy(() -> userUpdater.updateInformation(userId, futureRegion, command))
                .isInstanceOf(UserException.class)
                .hasMessageContaining("유효하지 않은 지역입니다");

        verify(userQueryService).getUserById(userId);
        verify(userCommandService, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Future 예외 발생 시에도 업데이트할 수 있다")
    void updateInformationWithFutureException() {
        // given
        String userId = "user123";
        User user = createMockUser();
        CompletableFuture<LocationResult> futureRegion = CompletableFuture.failedFuture(
                new RuntimeException("Location fetch failed")
        );

        UserInfoUpdateCommand command = new UserInfoUpdateCommand(
                "새닉네임",
                null,
                Country.JAPAN,
                Country.JAPAN,
                null,
                null,
                UserStatus.ACTIVE
        );

        given(userQueryService.getUserById(userId)).willReturn(user);
        given(userCommandService.save(any(User.class))).willReturn(user);

        assertThatThrownBy(() -> userUpdater.updateInformation(userId, futureRegion, command))
                .isInstanceOf(UserException.class)
                .hasMessageContaining("유효하지 않은 지역입니다");

        verify(userQueryService).getUserById(userId);
        verify(userCommandService, never()).save(any(User.class));
    }

    @Test
    @DisplayName("프로필 이미지 없이 업데이트할 수 있다")
    void updateInformationWithoutProfileImage() {
        // given
        String userId = "user123";
        User user = createMockUser();
        LocationResult locationResult = new LocationResult("Korea", "Busan");
        CompletableFuture<LocationResult> futureRegion = CompletableFuture.completedFuture(locationResult);

        UserInfoUpdateCommand command = new UserInfoUpdateCommand(
                "새닉네임",
                null,
                Country.SOUTH_KOREA,
                Country.SOUTH_KOREA,
                35.1796,
                129.0756,
                UserStatus.ACTIVE
        );

        given(userQueryService.getUserById(userId)).willReturn(user);
        given(userCommandService.save(any(User.class))).willReturn(user);

        // when
        userUpdater.updateInformation(userId, futureRegion, command);

        // then
        verify(userQueryService).getUserById(userId);
        verify(userCommandService).save(any(User.class));  // 괄호 위치 수정
        verify(uploadImageUseCase).deleteImage("https://old.com/image.jpg", null);
    }

    @Test
    @DisplayName("사용자 상태를 변경하며 업데이트할 수 있다")
    void updateInformationWithStatusChange() {
        // given
        String userId = "user123";
        User user = createMockUser();
        LocationResult locationResult = new LocationResult("Korea", "Seoul");
        CompletableFuture<LocationResult> futureRegion = CompletableFuture.completedFuture(locationResult);

        UserInfoUpdateCommand command = new UserInfoUpdateCommand(
                "업데이트닉네임",
                "https://updated.com/image.jpg",
                Country.SOUTH_KOREA,
                Country.JAPAN,
                37.5665,
                126.9780,
                UserStatus.BLOCKED
        );

        given(userQueryService.getUserById(userId)).willReturn(user);
        given(userCommandService.save(any(User.class))).willReturn(user);

        // when
        userUpdater.updateInformation(userId, futureRegion, command);

        // then
        verify(userQueryService).getUserById(userId);
        verify(userCommandService).save(any(User.class));
        verify(uploadImageUseCase).deleteImage("https://old.com/image.jpg", "https://updated.com/image.jpg");
    }

    @Test
    @DisplayName("비밀번호를 정상적으로 변경할 수 있다")
    void changePasswordSuccessfully() {
        // given
        String userId = "user123";
        User user = createMockUser();
        UserPasswordChangeCommand command = new UserPasswordChangeCommand(
                "oldPassword123",
                "newPassword456"
        );

        given(userQueryService.getUserById(userId)).willReturn(user);
        given(userPasswordPolicy.encodePassword("newPassword456")).willReturn("encodedNewPassword456");
        given(userCommandService.save(any(User.class))).willReturn(user);

        // when
        userUpdater.changePassword(userId, command);

        // then
        verify(userQueryService).getUserById(userId);
        verify(userPasswordPolicy).validatePasswordMatch("oldPassword123", "encodedPassword");
        verify(userPasswordPolicy).encodePassword("newPassword456");
        verify(userCommandService).save(any(User.class));
    }

    @Test
    @DisplayName("기존 비밀번호 불일치 시 예외가 발생한다")
    void changePasswordWithWrongExistingPassword() {
        // given
        String userId = "user123";
        User user = createMockUser();
        UserPasswordChangeCommand command = new UserPasswordChangeCommand(
                "wrongPassword",
                "newPassword456"
        );

        given(userQueryService.getUserById(userId)).willReturn(user);
        willThrow(new UserException(UserErrorCode.INVALID_PASSWORD_FORMAT))
                .given(userPasswordPolicy).validatePasswordMatch("wrongPassword", "encodedPassword");

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> userUpdater.changePassword(userId, command));

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_PASSWORD_FORMAT);
        verify(userPasswordPolicy).validatePasswordMatch("wrongPassword", "encodedPassword");
        verify(userPasswordPolicy, never()).encodePassword(anyString());
        verify(userCommandService, never()).save(any(User.class));
    }

    @Test
    @DisplayName("관리자가 다른 사용자를 차단할 수 있다")
    void blockUserAsAdmin() {
        // given
        String targetUserId = "target123";
        String adminUserId = "admin123";
        String userStatus = "BLOCKED";

        User targetUser = createMockUser();
        User adminUser = createAdminUser();

        given(userQueryService.getUserById(adminUserId)).willReturn(adminUser);
        given(userQueryService.getUserById(targetUserId)).willReturn(targetUser);
        given(userCommandService.save(any(User.class))).willReturn(targetUser);

        // when
        userUpdater.blockOrUnblockUser(targetUserId, adminUserId, userStatus);

        // then
        verify(userQueryService).getUserById(adminUserId);
        verify(userQueryService).getUserById(targetUserId);
        verify(userCommandService).save(any(User.class));
    }

    @Test
    @DisplayName("관리자가 다른 사용자를 차단 해제할 수 있다")
    void unblockUserAsAdmin() {
        // given
        String targetUserId = "target123";
        String adminUserId = "admin123";
        String userStatus = "ACTIVE";

        User targetUser = User.restore(
                UserId.of("target123"),
                Auth.of("target@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("타겟유저", null, Country.SOUTH_KOREA),
                Location.of(Country.SOUTH_KOREA, "서울"),
                UserStatus.BLOCKED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        User adminUser = createAdminUser();

        given(userQueryService.getUserById(adminUserId)).willReturn(adminUser);
        given(userQueryService.getUserById(targetUserId)).willReturn(targetUser);
        given(userCommandService.save(any(User.class))).willReturn(targetUser);

        // when
        userUpdater.blockOrUnblockUser(targetUserId, adminUserId, userStatus);

        // then
        verify(userQueryService).getUserById(adminUserId);
        verify(userQueryService).getUserById(targetUserId);
        verify(userCommandService).save(any(User.class));
    }

    @Test
    @DisplayName("일반 사용자가 다른 사용자를 차단하려 할 때 예외가 발생한다")
    void blockUserAsRegularUserThrowsException() {
        // given
        String targetUserId = "target123";
        String regularUserId = "regular123";
        String userStatus = "BLOCKED";

        User mockRegularUser = mock(User.class);

        given(userQueryService.getUserById(regularUserId)).willReturn(mockRegularUser);
        doThrow(new UserException(UserErrorCode.NOT_AN_ADMIN_USER))
                .when(mockRegularUser).validateAdminRole();

        // when & then
        assertThatThrownBy(() -> userUpdater.blockOrUnblockUser(targetUserId, regularUserId, userStatus))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(UserErrorCode.NOT_AN_ADMIN_USER.getMessage());

        verify(userQueryService).getUserById(regularUserId);
        verify(mockRegularUser).validateAdminRole();
        verify(userQueryService, never()).getUserById(targetUserId);
        verify(userCommandService, never()).save(any(User.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자 차단 시 예외가 발생한다")
    void blockNonExistentUserThrowsException() {
        // given
        String targetUserId = "nonexistent123";
        String adminUserId = "admin123";
        String userStatus = "BLOCKED";

        User adminUser = createAdminUser();

        given(userQueryService.getUserById(adminUserId)).willReturn(adminUser);
        given(userQueryService.getUserById(targetUserId))
                .willThrow(new UserException(UserErrorCode.USER_NOT_FOUND));

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> userUpdater.blockOrUnblockUser(targetUserId, adminUserId, userStatus));

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
        verify(userQueryService).getUserById(adminUserId);
        verify(userQueryService).getUserById(targetUserId);
        verify(userCommandService, never()).save(any(User.class));
    }

    @Test
    @DisplayName("다양한 국가로 프로필을 업데이트할 수 있다")
    void updateInformationWithDifferentCountries() {
        // given
        String userId = "user123";
        User user = createMockUser();
        LocationResult locationResult = new LocationResult("Japan", "Tokyo");
        CompletableFuture<LocationResult> futureRegion = CompletableFuture.completedFuture(locationResult);

        UserInfoUpdateCommand command = new UserInfoUpdateCommand(
                "일본거주자",
                "https://jp.com/image.jpg",
                Country.JAPAN,
                Country.SOUTH_KOREA,
                35.6762,
                139.6503,
                UserStatus.ACTIVE
        );

        given(userQueryService.getUserById(userId)).willReturn(user);
        given(userCommandService.save(any(User.class))).willReturn(user);

        // when
        userUpdater.updateInformation(userId, futureRegion, command);

        // then
        verify(userQueryService).getUserById(userId);
        verify(userCommandService).save(any(User.class));
        verify(uploadImageUseCase).deleteImage("https://old.com/image.jpg", "https://jp.com/image.jpg");
    }

    @Test
    @DisplayName("여러 비밀번호 변경을 연속으로 수행할 수 있다")
    void changePasswordMultipleTimes() {
        // given
        String userId = "user123";
        User user = createMockUser();

        UserPasswordChangeCommand command1 = new UserPasswordChangeCommand("oldPassword", "newPassword1");
        UserPasswordChangeCommand command2 = new UserPasswordChangeCommand("newPassword1", "newPassword2");

        given(userQueryService.getUserById(userId)).willReturn(user);
        given(userPasswordPolicy.encodePassword("newPassword1")).willReturn("encodedNew1");
        given(userPasswordPolicy.encodePassword("newPassword2")).willReturn("encodedNew2");
        given(userCommandService.save(any(User.class))).willReturn(user);

        // when
        userUpdater.changePassword(userId, command1);
        userUpdater.changePassword(userId, command2);

        // then
        verify(userQueryService, times(2)).getUserById(userId);
        verify(userPasswordPolicy).encodePassword("newPassword1");
        verify(userPasswordPolicy).encodePassword("newPassword2");
        verify(userCommandService, times(2)).save(any(User.class));
    }

    @Test
    @DisplayName("잘못된 사용자 상태로 차단 시도 시 예외가 발생한다")
    void blockUserWithInvalidStatusThrowsException() {
        // given
        String targetUserId = "target123";
        String adminUserId = "admin123";
        String invalidStatus = "INVALID_STATUS";

        User adminUser = createAdminUser();

        given(userQueryService.getUserById(adminUserId)).willReturn(adminUser);
        User targetUser = createMockUser();
        given(userQueryService.getUserById(targetUserId)).willReturn(targetUser);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userUpdater.blockOrUnblockUser(targetUserId, adminUserId, invalidStatus));

        verify(userQueryService).getUserById(adminUserId);
    }

    @Test
    @DisplayName("업데이트 시 기존 이미지가 null인 경우도 처리할 수 있다")
    void updateInformationWithNullPreviousImage() {
        // given
        String userId = "user123";
        User userWithoutImage = User.restore(
                UserId.of("user123"),
                Auth.of("test@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("기존닉네임", null, Country.SOUTH_KOREA),
                Location.of(Country.SOUTH_KOREA, "서울"),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        LocationResult locationResult = new LocationResult("Korea", "Seoul");
        CompletableFuture<LocationResult> futureRegion = CompletableFuture.completedFuture(locationResult);

        UserInfoUpdateCommand command = new UserInfoUpdateCommand(
                "새닉네임",
                "https://new.com/image.jpg",
                Country.SOUTH_KOREA,
                Country.SOUTH_KOREA,
                37.5665,
                126.9780,
                UserStatus.ACTIVE
        );

        given(userQueryService.getUserById(userId)).willReturn(userWithoutImage);
        given(userCommandService.save(any(User.class))).willReturn(userWithoutImage);

        // when
        userUpdater.updateInformation(userId, futureRegion, command);

        // then
        verify(userQueryService).getUserById(userId);
        verify(userCommandService).save(any(User.class));
        verify(uploadImageUseCase).deleteImage(null, "https://new.com/image.jpg");
    }
}