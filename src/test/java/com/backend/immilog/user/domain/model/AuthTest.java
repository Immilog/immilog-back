package com.backend.immilog.user.domain.model;

import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Auth 도메인 테스트")
class AuthTest {

    @Test
    @DisplayName("정상적인 이메일과 비밀번호로 Auth를 생성할 수 있다")
    void createAuthWithValidEmailAndPassword() {
        // given
        String validEmail = "test@example.com";
        String validPassword = "password123";

        // when
        Auth auth = Auth.of(validEmail, validPassword);

        // then
        assertThat(auth.email()).isEqualTo(validEmail);
        assertThat(auth.password()).isEqualTo(validPassword);
    }

    @Test
    @DisplayName("null 이메일로 Auth 생성 시 예외가 발생한다")
    void createAuthWithNullEmail() {
        // given
        String nullEmail = null;
        String validPassword = "password123";

        // when & then
        assertThatThrownBy(() -> Auth.of(nullEmail, validPassword))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(UserErrorCode.INVALID_EMAIL_FORMAT.getMessage());
    }

    @Test
    @DisplayName("빈 이메일로 Auth 생성 시 예외가 발생한다")
    void createAuthWithEmptyEmail() {
        // given
        String emptyEmail = "";
        String validPassword = "password123";

        // when & then
        assertThatThrownBy(() -> Auth.of(emptyEmail, validPassword))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(UserErrorCode.INVALID_EMAIL_FORMAT.getMessage());
    }

    @Test
    @DisplayName("공백 이메일로 Auth 생성 시 예외가 발생한다")
    void createAuthWithBlankEmail() {
        // given
        String blankEmail = "   ";
        String validPassword = "password123";

        // when & then
        assertThatThrownBy(() -> Auth.of(blankEmail, validPassword))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(UserErrorCode.INVALID_EMAIL_FORMAT.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-email",
            "test@",
            "@example.com",
            "test.example.com",
            "test@.com",
            "test@example.",
            "test@example..com",
            "test@@example.com"
    })
    @DisplayName("잘못된 형식의 이메일로 Auth 생성 시 예외가 발생한다")
    void createAuthWithInvalidEmailFormat(String invalidEmail) {
        // given
        String validPassword = "password123";

        // when & then
        assertThatThrownBy(() -> Auth.of(invalidEmail, validPassword))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(UserErrorCode.INVALID_EMAIL_FORMAT.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test@example.com",
            "user@domain.co.kr",
            "test.email@example.com",
            "test+label@example.com",
            "test-email@example-domain.com",
            "123@example.com",
            "test@example123.com"
    })
    @DisplayName("올바른 형식의 이메일로 Auth를 생성할 수 있다")
    void createAuthWithValidEmailFormats(String validEmail) {
        // given
        String validPassword = "password123";

        // when
        Auth auth = Auth.of(validEmail, validPassword);

        // then
        assertThat(auth.email()).isEqualTo(validEmail);
        assertThat(auth.password()).isEqualTo(validPassword);
    }

    @Test
    @DisplayName("null 비밀번호로 Auth 생성 시 예외가 발생한다")
    void createAuthWithNullPassword() {
        // given
        String validEmail = "test@example.com";
        String nullPassword = null;

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> Auth.of(validEmail, nullPassword));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_PASSWORD_FORMAT);
    }

    @Test
    @DisplayName("빈 비밀번호로 Auth 생성 시 예외가 발생한다")
    void createAuthWithEmptyPassword() {
        // given
        String validEmail = "test@example.com";
        String emptyPassword = "";

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> Auth.of(validEmail, emptyPassword));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_PASSWORD_FORMAT);
    }

    @Test
    @DisplayName("공백 비밀번호로 Auth 생성 시 예외가 발생한다")
    void createAuthWithBlankPassword() {
        // given
        String validEmail = "test@example.com";
        String blankPassword = "   ";

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> Auth.of(validEmail, blankPassword));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_PASSWORD_FORMAT);
    }

    @Test
    @DisplayName("Auth record의 동등성이 정상 동작한다")
    void authEquality() {
        // given
        String email = "test@example.com";
        String password = "password123";
        Auth auth1 = Auth.of(email, password);
        Auth auth2 = Auth.of(email, password);
        Auth auth3 = Auth.of("different@example.com", password);

        // when & then
        assertThat(auth1).isEqualTo(auth2);
        assertThat(auth1).isNotEqualTo(auth3);
        assertThat(auth1.hashCode()).isEqualTo(auth2.hashCode());
    }

    @Test
    @DisplayName("Auth record의 toString이 정상 동작한다")
    void authToString() {
        // given
        String email = "test@example.com";
        String password = "password123";
        Auth auth = Auth.of(email, password);

        // when
        String toString = auth.toString();

        // then
        assertThat(toString).contains("Auth");
        assertThat(toString).contains(email);
        assertThat(toString).contains(password);
    }
}