package com.backend.immilog.user.application.services.query;


import com.backend.immilog.user.domain.enums.UserRole;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.user.domain.model.*;
import com.backend.immilog.user.domain.repositories.UserRepository;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@DisplayName("UserQueryService 테스트")
class UserQueryServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private UserQueryService userQueryService;

    @BeforeEach
    void setUp() {
        userQueryService = new UserQueryService(userRepository);
    }

    private User createMockUser() {
        return User.restore(
                UserId.of("user123"),
                Auth.of("test@example.com", "encodedPassword"),
                UserRole.ROLE_USER,
                Profile.of("테스트유저", "https://example.com/image.jpg", "KR"),
                Location.of("KR", "서울특별시"),
                UserStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("이메일로 사용자를 정상적으로 조회할 수 있다")
    void getUserByEmailSuccessfully() {
        // given
        String email = "test@example.com";
        User expectedUser = createMockUser();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(expectedUser));

        // when
        User result = userQueryService.getUserByEmail(email);

        // then
        assertThat(result).isEqualTo(expectedUser);
        assertThat(result.getEmail()).isEqualTo(email);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회 시 예외가 발생한다")
    void getUserByEmailThrowsExceptionWhenNotFound() {
        // given
        String email = "nonexistent@example.com";

        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> userQueryService.getUserByEmail(email));

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("사용자 존재 여부를 정확히 확인할 수 있다")
    void isUserExistReturnsCorrectResult() {
        // given
        String existingEmail = "existing@example.com";
        String nonExistingEmail = "nonexisting@example.com";
        User existingUser = createMockUser();

        given(userRepository.findByEmail(existingEmail)).willReturn(Optional.of(existingUser));
        given(userRepository.findByEmail(nonExistingEmail)).willReturn(Optional.empty());

        // when
        Boolean existingResult = userQueryService.isUserExist(existingEmail);
        Boolean nonExistingResult = userQueryService.isUserExist(nonExistingEmail);

        // then
        assertThat(existingResult).isTrue();
        assertThat(nonExistingResult).isFalse();

        verify(userRepository).findByEmail(existingEmail);
        verify(userRepository).findByEmail(nonExistingEmail);
    }

    @Test
    @DisplayName("닉네임 사용 가능 여부를 정확히 확인할 수 있다")
    void isNicknameAvailableReturnsCorrectResult() {
        // given
        String availableNickname = "사용가능닉네임";
        String unavailableNickname = "이미사용중";
        User existingUser = createMockUser();

        given(userRepository.findByNickname(availableNickname)).willReturn(Optional.empty());
        given(userRepository.findByNickname(unavailableNickname)).willReturn(Optional.of(existingUser));

        // when
        Boolean availableResult = userQueryService.isNicknameAvailable(availableNickname);
        Boolean unavailableResult = userQueryService.isNicknameAvailable(unavailableNickname);

        // then
        assertThat(availableResult).isTrue();
        assertThat(unavailableResult).isFalse();

        verify(userRepository).findByNickname(availableNickname);
        verify(userRepository).findByNickname(unavailableNickname);
    }

    @Test
    @DisplayName("UserId 객체로 사용자를 정상적으로 조회할 수 있다")
    void getUserByUserIdSuccessfully() {
        // given
        UserId userId = UserId.of("user123");
        User expectedUser = createMockUser();

        given(userRepository.findById(userId)).willReturn(Optional.of(expectedUser));

        // when
        User result = userQueryService.getUserById(userId);

        // then
        assertThat(result).isEqualTo(expectedUser);
        assertThat(result.getUserId()).isEqualTo(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("존재하지 않는 UserId로 조회 시 예외가 발생한다")
    void getUserByUserIdThrowsExceptionWhenNotFound() {
        // given
        UserId userId = UserId.of("nonexistent123");

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> userQueryService.getUserById(userId));

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("문자열 ID로 사용자를 정상적으로 조회할 수 있다")
    void getUserByStringIdSuccessfully() {
        // given
        String id = "user123";
        User expectedUser = createMockUser();

        given(userRepository.findById(id)).willReturn(Optional.of(expectedUser));

        // when
        User result = userQueryService.getUserById(id);

        // then
        assertThat(result).isEqualTo(expectedUser);
        verify(userRepository).findById(id);
    }

    @Test
    @DisplayName("존재하지 않는 문자열 ID로 조회 시 예외가 발생한다")
    void getUserByStringIdThrowsExceptionWhenNotFound() {
        // given
        String id = "nonexistent123";

        given(userRepository.findById(id)).willReturn(Optional.empty());

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> userQueryService.getUserById(id));

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
        verify(userRepository).findById(id);
    }

    @Test
    @DisplayName("다양한 이메일 형식으로 사용자를 조회할 수 있다")
    void getUserWithVariousEmailFormats() {
        // given
        String[] emails = {
                "test@example.com",
                "user123@gmail.com",
                "admin@company.co.kr",
                "special.user+test@domain.net"
        };

        User user = createMockUser();

        for (String email : emails) {
            given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        }

        // when & then
        for (String email : emails) {
            User result = userQueryService.getUserByEmail(email);
            assertThat(result).isEqualTo(user);
        }

        for (String email : emails) {
            verify(userRepository).findByEmail(email);
        }
    }

    @Test
    @DisplayName("다양한 상태의 사용자를 조회할 수 있다")
    void getUsersWithDifferentStatuses() {
        // given
        User pendingUser = User.restore(
                UserId.of("pending123"),
                Auth.of("pending@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("대기유저", null, "KR"),
                Location.of("KR", "서울"),
                UserStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        User activeUser = User.restore(
                UserId.of("active123"),
                Auth.of("active@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("활성유저", null, "KR"),
                Location.of("KR", "서울"),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        User blockedUser = User.restore(
                UserId.of("blocked123"),
                Auth.of("blocked@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("차단유저", null, "KR"),
                Location.of("KR", "서울"),
                UserStatus.BLOCKED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        given(userRepository.findByEmail("pending@example.com")).willReturn(Optional.of(pendingUser));
        given(userRepository.findByEmail("active@example.com")).willReturn(Optional.of(activeUser));
        given(userRepository.findByEmail("blocked@example.com")).willReturn(Optional.of(blockedUser));

        // when
        User pendingResult = userQueryService.getUserByEmail("pending@example.com");
        User activeResult = userQueryService.getUserByEmail("active@example.com");
        User blockedResult = userQueryService.getUserByEmail("blocked@example.com");

        // then
        assertThat(pendingResult.getUserStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(activeResult.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(blockedResult.getUserStatus()).isEqualTo(UserStatus.BLOCKED);
    }

    @Test
    @DisplayName("다양한 권한의 사용자를 조회할 수 있다")
    void getUsersWithDifferentRoles() {
        // given
        User regularUser = User.restore(
                UserId.of("regular123"),
                Auth.of("regular@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("일반유저", null, "KR"),
                Location.of("KR", "서울"),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        User adminUser = User.restore(
                UserId.of("admin123"),
                Auth.of("admin@example.com", "password"),
                UserRole.ROLE_ADMIN,
                Profile.of("관리자", null, "KR"),
                Location.of("KR", "서울"),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        given(userRepository.findByEmail("regular@example.com")).willReturn(Optional.of(regularUser));
        given(userRepository.findByEmail("admin@example.com")).willReturn(Optional.of(adminUser));

        // when
        User regularResult = userQueryService.getUserByEmail("regular@example.com");
        User adminResult = userQueryService.getUserByEmail("admin@example.com");

        // then
        assertThat(regularResult.getUserRole()).isEqualTo(UserRole.ROLE_USER);
        assertThat(adminResult.getUserRole()).isEqualTo(UserRole.ROLE_ADMIN);
    }

    @Test
    @DisplayName("다양한 닉네임의 사용 가능 여부를 확인할 수 있다")
    void checkVariousNicknameAvailability() {
        // given
        String[] availableNicknames = {"새로운닉네임", "Available123", "特殊文字", "emoji😊"};
        String[] unavailableNicknames = {"이미사용중", "Taken", "使用済み", "используется"};

        User existingUser = createMockUser();

        for (String nickname : availableNicknames) {
            given(userRepository.findByNickname(nickname)).willReturn(Optional.empty());
        }

        for (String nickname : unavailableNicknames) {
            given(userRepository.findByNickname(nickname)).willReturn(Optional.of(existingUser));
        }

        // when & then
        for (String nickname : availableNicknames) {
            Boolean result = userQueryService.isNicknameAvailable(nickname);
            assertThat(result).isTrue();
        }

        for (String nickname : unavailableNicknames) {
            Boolean result = userQueryService.isNicknameAvailable(nickname);
            assertThat(result).isFalse();
        }
    }

    @Test
    @DisplayName("여러 조회 메서드를 연속으로 호출할 수 있다")
    void callMultipleQueryMethodsSequentially() {
        // given
        String email = "test@example.com";
        String nickname = "테스트닉네임";
        UserId userId = UserId.of("user123");
        String stringId = "user123";

        User user = createMockUser();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(userRepository.findByNickname(nickname)).willReturn(Optional.empty());
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.findById(stringId)).willReturn(Optional.of(user));

        // when
        User emailResult = userQueryService.getUserByEmail(email);
        Boolean existResult = userQueryService.isUserExist(email);
        Boolean nicknameResult = userQueryService.isNicknameAvailable(nickname);
        User userIdResult = userQueryService.getUserById(userId);
        User stringIdResult = userQueryService.getUserById(stringId);

        // then
        assertThat(emailResult).isEqualTo(user);
        assertThat(existResult).isTrue();
        assertThat(nicknameResult).isTrue();
        assertThat(userIdResult).isEqualTo(user);
        assertThat(stringIdResult).isEqualTo(user);

        verify(userRepository, times(2)).findByEmail(email);
        verify(userRepository).findByNickname(nickname);
        verify(userRepository).findById(userId);
        verify(userRepository).findById(stringId);
    }

    @Test
    @DisplayName("다양한 국가의 사용자를 조회할 수 있다")
    void getUsersFromDifferentCountries() {
        // given
        User koreanUser = User.restore(
                UserId.of("korean123"),
                Auth.of("korean@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("한국유저", null, "KR"),
                Location.of("KR", "서울"),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        User japaneseUser = User.restore(
                UserId.of("japanese123"),
                Auth.of("japanese@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("日本ユーザー", null, "JP"),
                Location.of("JP", "도쿄"),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        given(userRepository.findByEmail("korean@example.com")).willReturn(Optional.of(koreanUser));
        given(userRepository.findByEmail("japanese@example.com")).willReturn(Optional.of(japaneseUser));

        // when
        User koreanResult = userQueryService.getUserByEmail("korean@example.com");
        User japaneseResult = userQueryService.getUserByEmail("japanese@example.com");

        // then
        assertThat(koreanResult.getCountryId()).isEqualTo("KR");
        assertThat(japaneseResult.getCountryId()).isEqualTo("JP");
        assertThat(koreanResult.getRegion()).isEqualTo("서울");
        assertThat(japaneseResult.getRegion()).isEqualTo("도쿄");
    }
}