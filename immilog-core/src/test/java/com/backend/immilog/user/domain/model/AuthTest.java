package com.backend.immilog.user.domain.model;

import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthTest {

    @Nested
    @DisplayName("Auth 생성 테스트")
    class AuthCreationTest {

        @Test
        @DisplayName("유효한 이메일과 비밀번호로 Auth를 생성할 수 있다")
        void createAuthWithValidEmailAndPassword() {
            String email = "test@example.com";
            String password = "password123";

            Auth auth = new Auth(email, password);

            assertThat(auth.email()).isEqualTo(email);
            assertThat(auth.password()).isEqualTo(password);
        }

        @Test
        @DisplayName("of 팩토리 메서드로 Auth를 생성할 수 있다")
        void createAuthWithFactoryMethod() {
            String email = "user@domain.com";
            String password = "securePassword";

            Auth auth = Auth.of(email, password);

            assertThat(auth.email()).isEqualTo(email);
            assertThat(auth.password()).isEqualTo(password);
        }
    }

    @Nested
    @DisplayName("이메일 검증 테스트")
    class EmailValidationTest {

        @Test
        @DisplayName("null 이메일로 생성 시 예외가 발생한다")
        void createAuthWithNullEmailThrowsException() {
            assertThatThrownBy(() -> new Auth(null, "password"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_EMAIL_FORMAT.getMessage());
        }

        @Test
        @DisplayName("빈 이메일로 생성 시 예외가 발생한다")
        void createAuthWithEmptyEmailThrowsException() {
            assertThatThrownBy(() -> new Auth("", "password"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_EMAIL_FORMAT.getMessage());
        }

        @Test
        @DisplayName("공백 이메일로 생성 시 예외가 발생한다")
        void createAuthWithBlankEmailThrowsException() {
            assertThatThrownBy(() -> new Auth("   ", "password"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_EMAIL_FORMAT.getMessage());
        }

        @Test
        @DisplayName("@ 기호가 없는 이메일로 생성 시 예외가 발생한다")
        void createAuthWithEmailWithoutAtSymbolThrowsException() {
            assertThatThrownBy(() -> new Auth("testexample.com", "password"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_EMAIL_FORMAT.getMessage());
        }

        @Test
        @DisplayName("도메인이 없는 이메일로 생성 시 예외가 발생한다")
        void createAuthWithEmailWithoutDomainThrowsException() {
            assertThatThrownBy(() -> new Auth("test@", "password"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_EMAIL_FORMAT.getMessage());
        }

        @Test
        @DisplayName("로컬 파트가 없는 이메일로 생성 시 예외가 발생한다")
        void createAuthWithEmailWithoutLocalPartThrowsException() {
            assertThatThrownBy(() -> new Auth("@example.com", "password"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_EMAIL_FORMAT.getMessage());
        }

        @Test
        @DisplayName("연속된 점이 있는 이메일로 생성 시 예외가 발생한다")
        void createAuthWithEmailWithConsecutiveDotsThrowsException() {
            assertThatThrownBy(() -> new Auth("test..user@example.com", "password"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_EMAIL_FORMAT.getMessage());
        }

        @Test
        @DisplayName("점으로 시작하는 이메일로 생성 시 예외가 발생한다")
        void createAuthWithEmailStartingWithDotThrowsException() {
            assertThatThrownBy(() -> new Auth(".test@example.com", "password"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_EMAIL_FORMAT.getMessage());
        }

        @Test
        @DisplayName("점으로 끝나는 이메일로 생성 시 예외가 발생한다")
        void createAuthWithEmailEndingWithDotThrowsException() {
            assertThatThrownBy(() -> new Auth("test.@example.com", "password"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_EMAIL_FORMAT.getMessage());
        }

        @Test
        @DisplayName("연속된 @ 기호가 있는 이메일로 생성 시 예외가 발생한다")
        void createAuthWithEmailWithConsecutiveAtSymbolsThrowsException() {
            assertThatThrownBy(() -> new Auth("test@@example.com", "password"))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_EMAIL_FORMAT.getMessage());
        }

        @Test
        @DisplayName("유효한 이메일 형식들이 정상적으로 생성된다")
        void createAuthWithValidEmailFormats() {
            String[] validEmails = {
                    "test@example.com",
                    "user.name@domain.co.kr",
                    "test123@example.org",
                    "a@b.co",
                    "test+tag@example.com",
                    "test_user@example-domain.com"
            };

            for (String email : validEmails) {
                Auth auth = Auth.of(email, "password");
                assertThat(auth.email()).isEqualTo(email);
            }
        }
    }

    @Nested
    @DisplayName("비밀번호 검증 테스트")
    class PasswordValidationTest {

        @Test
        @DisplayName("null 비밀번호로 생성 시 예외가 발생한다")
        void createAuthWithNullPasswordThrowsException() {
            assertThatThrownBy(() -> new Auth("test@example.com", null))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_PASSWORD_FORMAT.getMessage());
        }

        @Test
        @DisplayName("빈 비밀번호로 생성 시 예외가 발생한다")
        void createAuthWithEmptyPasswordThrowsException() {
            assertThatThrownBy(() -> new Auth("test@example.com", ""))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_PASSWORD_FORMAT.getMessage());
        }

        @Test
        @DisplayName("공백 비밀번호로 생성 시 예외가 발생한다")
        void createAuthWithBlankPasswordThrowsException() {
            assertThatThrownBy(() -> new Auth("test@example.com", "   "))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_PASSWORD_FORMAT.getMessage());
        }

        @Test
        @DisplayName("유효한 비밀번호로 Auth를 생성할 수 있다")
        void createAuthWithValidPassword() {
            String email = "test@example.com";
            String password = "validPassword123!@#";

            Auth auth = Auth.of(email, password);

            assertThat(auth.password()).isEqualTo(password);
        }
    }

    @Nested
    @DisplayName("Auth 동등성 테스트")
    class AuthEqualityTest {

        @Test
        @DisplayName("같은 이메일과 비밀번호를 가진 Auth는 동등하다")
        void authsWithSameCredentialsAreEqual() {
            String email = "test@example.com";
            String password = "password123";
            Auth auth1 = Auth.of(email, password);
            Auth auth2 = Auth.of(email, password);

            assertThat(auth1).isEqualTo(auth2);
            assertThat(auth1.hashCode()).isEqualTo(auth2.hashCode());
        }

        @Test
        @DisplayName("다른 이메일을 가진 Auth는 동등하지 않다")
        void authsWithDifferentEmailsAreNotEqual() {
            Auth auth1 = Auth.of("test1@example.com", "password");
            Auth auth2 = Auth.of("test2@example.com", "password");

            assertThat(auth1).isNotEqualTo(auth2);
        }

        @Test
        @DisplayName("다른 비밀번호를 가진 Auth는 동등하지 않다")
        void authsWithDifferentPasswordsAreNotEqual() {
            Auth auth1 = Auth.of("test@example.com", "password1");
            Auth auth2 = Auth.of("test@example.com", "password2");

            assertThat(auth1).isNotEqualTo(auth2);
        }
    }

    @Nested
    @DisplayName("Auth 특수 케이스 테스트")
    class AuthSpecialCasesTest {

        @Test
        @DisplayName("매우 긴 이메일과 비밀번호로도 Auth를 생성할 수 있다")
        void createAuthWithVeryLongCredentials() {
            String longEmail = "a".repeat(50) + "@" + "b".repeat(50) + ".com";
            String longPassword = "password".repeat(50);

            Auth auth = Auth.of(longEmail, longPassword);

            assertThat(auth.email()).isEqualTo(longEmail);
            assertThat(auth.password()).isEqualTo(longPassword);
        }

        @Test
        @DisplayName("특수문자가 포함된 비밀번호로도 Auth를 생성할 수 있다")
        void createAuthWithSpecialCharacterPassword() {
            String email = "test@example.com";
            String specialPassword = "!@#$%^&*()_+-={}[]|\\:;\"'<>,.?/";

            Auth auth = Auth.of(email, specialPassword);

            assertThat(auth.password()).isEqualTo(specialPassword);
        }

        @Test
        @DisplayName("유니코드 문자가 포함된 이메일로 Auth 생성 시 예외가 발생한다")
        void createAuthWithUnicodeEmailThrowsException() {
            String unicodeEmail = "테스트@example.com";
            String password = "password123";

            assertThatThrownBy(() -> Auth.of(unicodeEmail, password))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_EMAIL_FORMAT.getMessage());
        }
    }

    @Nested
    @DisplayName("Auth toString 테스트")
    class AuthToStringTest {

        @Test
        @DisplayName("toString 메서드가 올바르게 동작한다")
        void toStringWorksCorrectly() {
            Auth auth = Auth.of("test@example.com", "password123");

            String result = auth.toString();

            assertThat(result).contains("test@example.com");
            assertThat(result).contains("Auth");
        }
    }
}