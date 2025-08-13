package com.backend.immilog.user.application.services;


import com.backend.immilog.user.domain.enums.UserRole;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.user.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.*;

@DisplayName("NotificationService 테스트")
class NotificationServiceTest {

    private final EmailService emailService = mock(EmailService.class);
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(emailService);
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

    private User createMockUserWithNickname(String nickname) {
        return User.restore(
                UserId.of("user123"),
                Auth.of("test@example.com", "encodedPassword"),
                UserRole.ROLE_USER,
                Profile.of(nickname, "https://example.com/image.jpg", "KR"),
                Location.of("KR", "서울특별시"),
                UserStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("사용자 등록 알림을 정상적으로 발송할 수 있다")
    void notifyUserRegistrationSuccessfully() {
        // given
        User user = createMockUser();

        // when
        notificationService.notifyUserRegistration(user);

        // then
        verify(emailService).sendHtmlEmail(
                eq("test@example.com"),
                eq("회원가입을 환영합니다!"),
                contains("환영합니다, 테스트유저님!")
        );
    }

    @Test
    @DisplayName("비밀번호 변경 알림을 정상적으로 발송할 수 있다")
    void notifyPasswordChangedSuccessfully() {
        // given
        User user = createMockUser();

        // when
        notificationService.notifyPasswordChanged(user);

        // then
        verify(emailService).sendHtmlEmail(
                eq("test@example.com"),
                eq("비밀번호가 변경되었습니다"),
                contains("안녕하세요, 테스트유저님")
        );
    }

    @Test
    @DisplayName("프로필 업데이트 알림을 정상적으로 발송할 수 있다")
    void notifyProfileUpdatedSuccessfully() {
        // given
        User user = createMockUser();

        // when
        notificationService.notifyProfileUpdated(user);

        // then
        verify(emailService).sendHtmlEmail(
                eq("test@example.com"),
                eq("프로필이 업데이트되었습니다"),
                contains("안녕하세요, 테스트유저님")
        );
    }

    @Test
    @DisplayName("계정 차단 알림을 정상적으로 발송할 수 있다")
    void notifyAccountBlockedSuccessfully() {
        // given
        User user = createMockUser();

        // when
        notificationService.notifyAccountBlocked(user);

        // then
        verify(emailService).sendHtmlEmail(
                eq("test@example.com"),
                eq("계정이 차단되었습니다"),
                contains("계정이 일시적으로 차단되었습니다")
        );
    }

    @Test
    @DisplayName("계정 활성화 알림을 정상적으로 발송할 수 있다")
    void notifyAccountActivatedSuccessfully() {
        // given
        User user = createMockUser();

        // when
        notificationService.notifyAccountActivated(user);

        // then
        verify(emailService).sendHtmlEmail(
                eq("test@example.com"),
                eq("계정이 활성화되었습니다"),
                contains("계정이 다시 활성화되었습니다")
        );
    }

    @Test
    @DisplayName("다양한 닉네임의 사용자에게 알림을 발송할 수 있다")
    void notifyUsersWithVariousNicknames() {
        // given
        User koreanUser = createMockUserWithNickname("한국유저");
        User englishUser = createMockUserWithNickname("EnglishUser");
        User specialCharUser = createMockUserWithNickname("특수문자유저!@#");

        // when
        notificationService.notifyUserRegistration(koreanUser);
        notificationService.notifyUserRegistration(englishUser);
        notificationService.notifyUserRegistration(specialCharUser);

        // then
        verify(emailService).sendHtmlEmail(
                eq("test@example.com"),
                eq("회원가입을 환영합니다!"),
                contains("환영합니다, 한국유저님!")
        );
        verify(emailService).sendHtmlEmail(
                eq("test@example.com"),
                eq("회원가입을 환영합니다!"),
                contains("환영합니다, EnglishUser님!")
        );
        verify(emailService).sendHtmlEmail(
                eq("test@example.com"),
                eq("회원가입을 환영합니다!"),
                contains("환영합니다, 특수문자유저!@#님!")
        );
    }

    @Test
    @DisplayName("다른 이메일 주소를 가진 사용자들에게 알림을 발송할 수 있다")
    void notifyUsersWithDifferentEmails() {
        // given
        User user1 = User.restore(
                UserId.of("user1"),
                Auth.of("user1@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("유저1", null, "KR"),
                Location.of("KR", "서울"),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        User user2 = User.restore(
                UserId.of("user2"),
                Auth.of("user2@gmail.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("유저2", null, "JP"),
                Location.of("JP", "도쿄"),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // when
        notificationService.notifyUserRegistration(user1);
        notificationService.notifyUserRegistration(user2);

        // then
        verify(emailService).sendHtmlEmail(
                eq("user1@example.com"),
                eq("회원가입을 환영합니다!"),
                contains("환영합니다, 유저1님!")
        );
        verify(emailService).sendHtmlEmail(
                eq("user2@gmail.com"),
                eq("회원가입을 환영합니다!"),
                contains("환영합니다, 유저2님!")
        );
    }

    @Test
    @DisplayName("모든 알림 타입을 순차적으로 발송할 수 있다")
    void notifyAllNotificationTypesSequentially() {
        // given
        User user = createMockUser();

        // when
        notificationService.notifyUserRegistration(user);
        notificationService.notifyPasswordChanged(user);
        notificationService.notifyProfileUpdated(user);
        notificationService.notifyAccountBlocked(user);
        notificationService.notifyAccountActivated(user);

        // then
        verify(emailService, times(5)).sendHtmlEmail(
                eq("test@example.com"),
                anyString(),
                anyString()
        );
    }

    @Test
    @DisplayName("관리자 권한 사용자에게도 알림을 발송할 수 있다")
    void notifyAdminUser() {
        // given
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

        // when
        notificationService.notifyUserRegistration(adminUser);

        // then
        verify(emailService).sendHtmlEmail(
                eq("admin@example.com"),
                eq("회원가입을 환영합니다!"),
                contains("환영합니다, 관리자님!")
        );
    }

    @Test
    @DisplayName("다양한 국가의 사용자에게 알림을 발송할 수 있다")
    void notifyUsersFromDifferentCountries() {
        // given
        User koreanUser = User.restore(
                UserId.of("korean"),
                Auth.of("korean@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("한국유저", null, "KR"),
                Location.of("KR", "서울"),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        User japaneseUser = User.restore(
                UserId.of("japanese"),
                Auth.of("japanese@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("일본유저", null, "JP"),
                Location.of("JP", "도쿄"),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // when
        notificationService.notifyUserRegistration(koreanUser);
        notificationService.notifyUserRegistration(japaneseUser);

        // then
        verify(emailService).sendHtmlEmail(
                eq("korean@example.com"),
                eq("회원가입을 환영합니다!"),
                contains("환영합니다, 한국유저님!")
        );
        verify(emailService).sendHtmlEmail(
                eq("japanese@example.com"),
                eq("회원가입을 환영합니다!"),
                contains("환영합니다, 일본유저님!")
        );
    }

    @Test
    @DisplayName("비활성 상태 사용자에게도 알림을 발송할 수 있다")
    void notifyInactiveUser() {
        // given
        User inactiveUser = User.restore(
                UserId.of("inactive"),
                Auth.of("inactive@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("비활성유저", null, "KR"),
                Location.of("KR", "서울"),
                UserStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // when
        notificationService.notifyUserRegistration(inactiveUser);

        // then
        verify(emailService).sendHtmlEmail(
                eq("inactive@example.com"),
                eq("회원가입을 환영합니다!"),
                contains("환영합니다, 비활성유저님!")
        );
    }

    @Test
    @DisplayName("프로필 이미지가 없는 사용자에게도 알림을 발송할 수 있다")
    void notifyUserWithoutProfileImage() {
        // given
        User userWithoutImage = User.restore(
                UserId.of("noimage"),
                Auth.of("noimage@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("이미지없음", null, "KR"),
                Location.of("KR", "서울"),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // when
        notificationService.notifyUserRegistration(userWithoutImage);

        // then
        verify(emailService).sendHtmlEmail(
                eq("noimage@example.com"),
                eq("회원가입을 환영합니다!"),
                contains("환영합니다, 이미지없음님!")
        );
    }
}