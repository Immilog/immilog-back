package com.backend.immilog.user.application.usecase;


import com.backend.immilog.user.application.command.UserSignInCommand;
import com.backend.immilog.user.application.result.LocationResult;
import com.backend.immilog.user.application.result.UserSignInResult;
import com.backend.immilog.user.application.services.command.TokenCommandService;
import com.backend.immilog.user.application.services.query.UserQueryService;
import com.backend.immilog.user.domain.enums.UserRole;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.user.domain.model.*;
import com.backend.immilog.user.domain.service.UserPasswordPolicy;
import com.backend.immilog.user.domain.service.UserTokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@DisplayName("LoginUserUseCase 테스트")
@ExtendWith(MockitoExtension.class)
class LoginUserUseCaseTest {

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private TokenCommandService tokenCommandService;

    @Mock
    private UserTokenGenerator userTokenGenerator;

    @Mock
    private UserPasswordPolicy userPasswordPolicy;

    private LoginUserUseCase loginUserUseCase;

    @BeforeEach
    void setUp() {
        loginUserUseCase = new LoginUserUseCase.UserLoginProcessor(
                userQueryService,
                tokenCommandService,
                userTokenGenerator,
                userPasswordPolicy
        );
    }

    private UserSignInCommand createValidSignInCommand() {
        return new UserSignInCommand(
                "test@example.com",
                "password123",
                37.5665,
                126.9780
        );
    }

    private User createMockUser() {
        return User.restore(
                UserId.of("user123"),
                Auth.of("test@example.com", "encodedPassword123"),
                UserRole.ROLE_USER,
                Profile.of("테스트유저", "https://example.com/image.jpg", "KR"),
                Location.of("KR", "서울특별시"),
                UserStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("정상적인 로그인이 가능하다")
    void signInSuccessfully() {
        // given
        UserSignInCommand command = createValidSignInCommand();
        User mockUser = createMockUser();
        LocationResult locationResult = new LocationResult("KR", "서울특별시");
        CompletableFuture<LocationResult> locationFuture = CompletableFuture.completedFuture(locationResult);

        given(userQueryService.getUserByEmail("test@example.com")).willReturn(mockUser);
        given(userTokenGenerator.generate("user123", "test@example.com", UserRole.ROLE_USER, "KR"))
                .willReturn("accessToken123");
        given(userTokenGenerator.generateRefreshToken()).willReturn("refreshToken123");

        // when
        UserSignInResult result = loginUserUseCase.signIn(command, locationFuture);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo("user123");
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.nickname()).isEqualTo("테스트유저");
        assertThat(result.accessToken()).isEqualTo("accessToken123");
        assertThat(result.refreshToken()).isEqualTo("refreshToken123");
        assertThat(result.country()).isEqualTo("KR");
        assertThat(result.region()).isEqualTo("서울특별시");
        assertThat(result.isLocationMatch()).isTrue();

        verify(userQueryService).getUserByEmail("test@example.com");
        verify(userPasswordPolicy).validatePasswordMatch("password123", "encodedPassword123");
        verify(userTokenGenerator).generate("user123", "test@example.com", UserRole.ROLE_USER, "KR");
        verify(userTokenGenerator).generateRefreshToken();
        verify(tokenCommandService).saveKeyAndValue("Refresh: refreshToken123", "test@example.com", 5 * 29 * 24 * 60);
    }

    @Test
    @DisplayName("위치가 일치하지 않는 경우를 처리한다")
    void signInWithLocationMismatch() {
        // given
        UserSignInCommand command = createValidSignInCommand();
        User mockUser = createMockUser();
        LocationResult locationResult = new LocationResult("JP", "도쿄"); // 다른 위치
        CompletableFuture<LocationResult> locationFuture = CompletableFuture.completedFuture(locationResult);

        given(userQueryService.getUserByEmail("test@example.com")).willReturn(mockUser);
        given(userTokenGenerator.generate(any(), any(), any(), any())).willReturn("accessToken123");
        given(userTokenGenerator.generateRefreshToken()).willReturn("refreshToken123");

        // when
        UserSignInResult result = loginUserUseCase.signIn(command, locationFuture);

        // then
        assertThat(result.isLocationMatch()).isFalse();
        assertThat(result.userId()).isEqualTo("user123");
    }

    @Test
    @DisplayName("위치 정보 조회 타임아웃을 처리한다")
    void signInWithLocationTimeout() {
        // given
        UserSignInCommand command = createValidSignInCommand();
        User mockUser = createMockUser();
        CompletableFuture<LocationResult> locationFuture = new CompletableFuture<>();

        given(userQueryService.getUserByEmail("test@example.com")).willReturn(mockUser);
        given(userTokenGenerator.generate(any(), any(), any(), any())).willReturn("accessToken123");
        given(userTokenGenerator.generateRefreshToken()).willReturn("refreshToken123");

        // when
        UserSignInResult result = loginUserUseCase.signIn(command, locationFuture);

        // then
        assertThat(result).isNotNull();
        assertThat(result.isLocationMatch()).isFalse(); // 타임아웃 시 false
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 예외가 발생한다")
    void signInWithInvalidPassword() {
        // given
        UserSignInCommand command = createValidSignInCommand();
        User mockUser = createMockUser();
        LocationResult locationResult = new LocationResult("KR", "서울특별시");
        CompletableFuture<LocationResult> locationFuture = CompletableFuture.completedFuture(locationResult);

        given(userQueryService.getUserByEmail("test@example.com")).willReturn(mockUser);
        willThrow(new RuntimeException("비밀번호 불일치"))
                .given(userPasswordPolicy).validatePasswordMatch("password123", "encodedPassword123");

        // when & then
        assertThatThrownBy(() -> loginUserUseCase.signIn(command, locationFuture))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("비밀번호 불일치");

        verify(userQueryService).getUserByEmail("test@example.com");
        verify(userPasswordPolicy).validatePasswordMatch("password123", "encodedPassword123");
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 로그인 시 예외가 발생한다")
    void signInWithNonExistentUser() {
        // given
        UserSignInCommand command = createValidSignInCommand();
        LocationResult locationResult = new LocationResult("KR", "서울특별시");
        CompletableFuture<LocationResult> locationFuture = CompletableFuture.completedFuture(locationResult);

        given(userQueryService.getUserByEmail("test@example.com"))
                .willThrow(new RuntimeException("사용자를 찾을 수 없습니다"));

        // when & then
        assertThatThrownBy(() -> loginUserUseCase.signIn(command, locationFuture))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("사용자를 찾을 수 없습니다");

        verify(userQueryService).getUserByEmail("test@example.com");
    }

    @Test
    @DisplayName("사용자 ID로 로그인 정보를 조회할 수 있다")
    void getUserSignInDTO() {
        // given
        String userId = "user123";
        User mockUser = createMockUser();
        LocationResult locationResult = new LocationResult("KR", "서울특별시");

        given(userQueryService.getUserById(userId)).willReturn(mockUser);
        given(userTokenGenerator.generate("user123", "test@example.com", UserRole.ROLE_USER, "KR"))
                .willReturn("accessToken123");
        given(userTokenGenerator.generateRefreshToken()).willReturn("refreshToken123");

        // when
        UserSignInResult result = loginUserUseCase.getUserSignInDTO(userId, locationResult);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo("user123");
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.isLocationMatch()).isTrue();

        verify(userQueryService).getUserById(userId);
        verify(userTokenGenerator).generate("user123", "test@example.com", UserRole.ROLE_USER, "KR");
        verify(userTokenGenerator).generateRefreshToken();
        verify(tokenCommandService).saveKeyAndValue("Refresh: refreshToken123", "test@example.com", 5 * 29 * 24 * 60);
    }

    @Test
    @DisplayName("관리자 권한 사용자의 로그인을 처리한다")
    void signInAsAdmin() {
        // given
        UserSignInCommand command = createValidSignInCommand();
        User adminUser = User.restore(
                UserId.of("admin123"),
                Auth.of("admin@example.com", "encodedPassword123"),
                UserRole.ROLE_ADMIN,
                Profile.of("관리자", null, "KR"),
                Location.of("KR", "서울특별시"),
                UserStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
        LocationResult locationResult = new LocationResult("KR", "서울특별시");
        CompletableFuture<LocationResult> locationFuture = CompletableFuture.completedFuture(locationResult);

        given(userQueryService.getUserByEmail("test@example.com")).willReturn(adminUser);
        given(userTokenGenerator.generate("admin123", "admin@example.com", UserRole.ROLE_ADMIN, "KR"))
                .willReturn("adminAccessToken123");
        given(userTokenGenerator.generateRefreshToken()).willReturn("adminRefreshToken123");

        // when
        UserSignInResult result = loginUserUseCase.signIn(command, locationFuture);

        // then
        assertThat(result.userId()).isEqualTo("admin123");
        assertThat(result.email()).isEqualTo("admin@example.com");
        assertThat(result.nickname()).isEqualTo("관리자");
        assertThat(result.accessToken()).isEqualTo("adminAccessToken123");
        assertThat(result.refreshToken()).isEqualTo("adminRefreshToken123");

        verify(userTokenGenerator).generate("admin123", "admin@example.com", UserRole.ROLE_ADMIN, "KR");
    }

    @Test
    @DisplayName("다양한 국가 사용자의 로그인을 처리한다")
    void signInWithDifferentCountries() {
        // given
        UserSignInCommand command = createValidSignInCommand();
        User japanUser = User.restore(
                UserId.of("japanUser123"),
                Auth.of("japan@example.com", "encodedPassword123"),
                UserRole.ROLE_USER,
                Profile.of("일본유저", null, "JP"),
                Location.of("JP", "도쿄"),
                UserStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
        LocationResult locationResult = new LocationResult("JP", "도쿄");
        CompletableFuture<LocationResult> locationFuture = CompletableFuture.completedFuture(locationResult);

        given(userQueryService.getUserByEmail("test@example.com")).willReturn(japanUser);
        given(userTokenGenerator.generate("japanUser123", "japan@example.com", UserRole.ROLE_USER, "JP"))
                .willReturn("japanAccessToken123");
        given(userTokenGenerator.generateRefreshToken()).willReturn("japanRefreshToken123");

        // when
        UserSignInResult result = loginUserUseCase.signIn(command, locationFuture);

        // then
        assertThat(result.userId()).isEqualTo("japanUser123");
        assertThat(result.country()).isEqualTo("JP");
        assertThat(result.region()).isEqualTo("도쿄");
        assertThat(result.isLocationMatch()).isTrue();
    }
}