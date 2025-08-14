package com.backend.immilog.user.application.usecase;

import com.backend.immilog.user.application.command.UserSignUpCommand;
import com.backend.immilog.user.application.result.EmailVerificationResult;
import com.backend.immilog.user.application.result.userNicknameResult;
import com.backend.immilog.user.application.services.UserService;
import com.backend.immilog.user.application.services.command.UserCommandService;
import com.backend.immilog.user.application.services.query.UserQueryService;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.user.domain.model.*;
import com.backend.immilog.user.domain.service.EmailVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@DisplayName("SignUpUserUseCase 테스트")
@ExtendWith(MockitoExtension.class)
class SignUpUserUseCaseTest {

    @Mock
    private UserService userService;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private UserCommandService userCommandService;

    @Mock
    private EmailVerificationService emailVerificationService;

    private SignUpUserUseCase signUpUserUseCase;

    @BeforeEach
    void setUp() {
        signUpUserUseCase = new SignUpUserUseCase.UserSignUpProcessor(
                userService,
                userQueryService,
                userCommandService,
                emailVerificationService
        );
    }

    private UserSignUpCommand createValidSignUpCommand() {
        return new UserSignUpCommand(
                "테스트유저",
                "password123",
                "test@example.com",
                "SOUTH_KOREA",
                "SOUTH_KOREA",
                "서울특별시",
                "https://example.com/image.jpg"
        );
    }

    private User createMockUser(UserId userId) {
        return User.restore(
                userId,
                Auth.of("test@example.com", "encodedPassword"),
                com.backend.immilog.user.domain.enums.UserRole.ROLE_USER,
                Profile.of("테스트유저", "https://example.com/image.jpg", "KR"),
                Location.of("KR", "서울특별시"),
                UserStatus.PENDING,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("정상적인 사용자 가입이 가능하다")
    void signUpSuccessfully() {
        // given
        UserSignUpCommand command = createValidSignUpCommand();
        UserId expectedUserId = UserId.of("user123");
        User mockUser = createMockUser(expectedUserId);

        given(userService.registerUser(
                "test@example.com",
                "password123",
                "테스트유저",
                "https://example.com/image.jpg",
                "SOUTH_KOREA",
                "SOUTH_KOREA",
                "서울특별시"
        )).willReturn(expectedUserId);

        given(userQueryService.getUserById(expectedUserId)).willReturn(mockUser);

        // when
        userNicknameResult result = signUpUserUseCase.signUp(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo("user123");
        assertThat(result.nickName()).isEqualTo("테스트유저");

        verify(userService).registerUser(
                "test@example.com",
                "password123",
                "테스트유저",
                "https://example.com/image.jpg",
                "SOUTH_KOREA",
                "SOUTH_KOREA",
                "서울특별시"
        );
        verify(userQueryService).getUserById(expectedUserId);
    }

    @Test
    @DisplayName("관심 국가가 null일 때 기본 국가를 사용한다")
    void signUpWithNullInterestCountry() {
        // given
        UserSignUpCommand command = new UserSignUpCommand(
                "테스트유저",
                "password123",
                "test@example.com",
                "SOUTH_KOREA",
                null,
                "서울특별시",
                "https://example.com/image.jpg"
        );

        UserId expectedUserId = UserId.of("user123");
        User mockUser = createMockUser(expectedUserId);

        given(userService.registerUser(
                any(), any(), any(), any(),
                eq("SOUTH_KOREA"), // 기본 국가 사용
                eq("SOUTH_KOREA"),
                any()
        )).willReturn(expectedUserId);

        given(userQueryService.getUserById(expectedUserId)).willReturn(mockUser);

        // when
        userNicknameResult result = signUpUserUseCase.signUp(command);

        // then
        assertThat(result).isNotNull();
        verify(userService).registerUser(
                "test@example.com",
                "password123",
                "테스트유저",
                "https://example.com/image.jpg",
                "SOUTH_KOREA", // 기본 국가가 관심 국가로 설정됨
                "SOUTH_KOREA",
                "서울특별시"
        );
    }

    @Test
    @DisplayName("관심 국가가 빈 문자열일 때 기본 국가를 사용한다")
    void signUpWithEmptyInterestCountry() {
        // given
        UserSignUpCommand command = new UserSignUpCommand(
                "테스트유저",
                "password123",
                "test@example.com",
                "JAPAN",
                "",
                "도쿄",
                null
        );

        UserId expectedUserId = UserId.of("user123");
        User mockUser = User.restore(
                expectedUserId,
                Auth.of("test@example.com", "encodedPassword"),
                com.backend.immilog.user.domain.enums.UserRole.ROLE_USER,
                Profile.of("테스트유저", null, "JP"),
                Location.of("JP", "도쿄"),
                UserStatus.PENDING,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );

        given(userService.registerUser(
                any(), any(), any(), any(),
                eq("JAPAN"),
                eq("JAPAN"),
                any()
        )).willReturn(expectedUserId);

        given(userQueryService.getUserById(expectedUserId)).willReturn(mockUser);

        // when
        userNicknameResult result = signUpUserUseCase.signUp(command);

        // then
        assertThat(result).isNotNull();
        verify(userService).registerUser(
                "test@example.com",
                "password123",
                "테스트유저",
                null,
                "JAPAN",
                "JAPAN",
                "도쿄"
        );
    }

    @Test
    @DisplayName("닉네임 사용 가능 여부를 확인할 수 있다")
    void checkNicknameAvailability() {
        // given
        String nickname = "새로운닉네임";
        given(userQueryService.isNicknameAvailable(nickname)).willReturn(true);

        // when
        Boolean isAvailable = signUpUserUseCase.isNicknameAvailable(nickname);

        // then
        assertThat(isAvailable).isTrue();
        verify(userQueryService).isNicknameAvailable(nickname);
    }

    @Test
    @DisplayName("이미 사용 중인 닉네임은 사용할 수 없다")
    void checkUnavailableNickname() {
        // given
        String nickname = "사용중인닉네임";
        given(userQueryService.isNicknameAvailable(nickname)).willReturn(false);

        // when
        Boolean isAvailable = signUpUserUseCase.isNicknameAvailable(nickname);

        // then
        assertThat(isAvailable).isFalse();
        verify(userQueryService).isNicknameAvailable(nickname);
    }

    @Test
    @DisplayName("이메일 인증을 요청할 수 있다")
    void verifyEmail() {
        // given
        String userId = "user123";
        UserId userIdObj = UserId.of(userId);
        User mockUser = createMockUser(userIdObj);

        EmailVerificationService.VerificationResult mockVerificationResult =
                new EmailVerificationService.VerificationResult("인증 메시지", true);

        given(userQueryService.getUserById(userIdObj)).willReturn(mockUser);
        given(emailVerificationService.generateVerificationResult(
                mockUser.getUserStatus(),
                mockUser.getCountryId()
        )).willReturn(mockVerificationResult);

        // when
        EmailVerificationResult result = signUpUserUseCase.verifyEmail(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("인증 메시지");
        assertThat(result.isLoginAvailable()).isTrue();

        verify(userQueryService).getUserById(userIdObj);
        verify(userCommandService).save(mockUser.activate());
        verify(emailVerificationService).generateVerificationResult(UserStatus.PENDING, "KR");
    }

    @Test
    @DisplayName("다양한 국가로 사용자 가입이 가능하다")
    void signUpWithDifferentCountries() {
        // given
        UserSignUpCommand command = new UserSignUpCommand(
                "일본유저",
                "password123",
                "japan@example.com",
                "JAPAN",
                "MALAYSIA",
                "도쿄",
                null
        );

        UserId expectedUserId = UserId.of("japanUser123");
        User mockUser = User.restore(
                expectedUserId,
                Auth.of("japan@example.com", "encodedPassword"),
                com.backend.immilog.user.domain.enums.UserRole.ROLE_USER,
                Profile.of("일본유저", null, "MY"),
                Location.of("JP", "도쿄"),
                UserStatus.PENDING,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );

        given(userService.registerUser(
                "japan@example.com",
                "password123",
                "일본유저",
                null,
                "MALAYSIA", // 관심 국가
                "JAPAN",    // 거주 국가
                "도쿄"
        )).willReturn(expectedUserId);

        given(userQueryService.getUserById(expectedUserId)).willReturn(mockUser);

        // when
        userNicknameResult result = signUpUserUseCase.signUp(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo("japanUser123");
        assertThat(result.nickName()).isEqualTo("일본유저");
    }

    @Test
    @DisplayName("UserService에서 예외 발생 시 전파된다")
    void handleUserServiceException() {
        // given
        UserSignUpCommand command = createValidSignUpCommand();

        given(userService.registerUser(
                any(), any(), any(), any(), any(), any(), any()
        )).willThrow(new RuntimeException("사용자 등록 실패"));

        // when & then
        assertThatThrownBy(() -> signUpUserUseCase.signUp(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("사용자 등록 실패");
    }

    @Test
    @DisplayName("UserQueryService에서 예외 발생 시 전파된다")
    void handleUserQueryServiceException() {
        // given
        UserSignUpCommand command = createValidSignUpCommand();
        UserId expectedUserId = UserId.of("user123");

        given(userService.registerUser(
                any(), any(), any(), any(), any(), any(), any()
        )).willReturn(expectedUserId);

        given(userQueryService.getUserById(expectedUserId))
                .willThrow(new RuntimeException("사용자 조회 실패"));

        // when & then
        assertThatThrownBy(() -> signUpUserUseCase.signUp(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("사용자 조회 실패");
    }
}