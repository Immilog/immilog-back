package com.backend.immilog.user.domain.service;

import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserPasswordPolicyTest {

    private final PasswordEncryptionService mockPasswordEncryptionService = mock(PasswordEncryptionService.class);
    private final UserPasswordPolicy userPasswordPolicy = new UserPasswordPolicy(mockPasswordEncryptionService);

    @Nested
    @DisplayName("비밀번호 일치 검증 테스트")
    class ValidatePasswordMatchTest {

        @Test
        @DisplayName("일치하는 비밀번호 검증이 성공한다")
        void validatePasswordMatchSucceeds() {
            String rawPassword = "password123";
            String encodedPassword = "encodedPassword123";
            when(mockPasswordEncryptionService.matches(rawPassword, encodedPassword)).thenReturn(true);

            userPasswordPolicy.validatePasswordMatch(rawPassword, encodedPassword);

            verify(mockPasswordEncryptionService).matches(rawPassword, encodedPassword);
        }

        @Test
        @DisplayName("일치하지 않는 비밀번호 검증이 실패한다")
        void validatePasswordMatchFails() {
            String rawPassword = "password123";
            String encodedPassword = "wrongEncodedPassword";
            when(mockPasswordEncryptionService.matches(rawPassword, encodedPassword)).thenReturn(false);

            assertThatThrownBy(() -> userPasswordPolicy.validatePasswordMatch(rawPassword, encodedPassword))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.PASSWORD_NOT_MATCH.getMessage());

            verify(mockPasswordEncryptionService).matches(rawPassword, encodedPassword);
        }

        @Test
        @DisplayName("빈 원본 비밀번호로 검증하면 false를 반환한다")
        void validatePasswordMatchWithEmptyRawPassword() {
            String rawPassword = "";
            String encodedPassword = "encodedPassword123";
            when(mockPasswordEncryptionService.matches(rawPassword, encodedPassword)).thenReturn(false);

            assertThatThrownBy(() -> userPasswordPolicy.validatePasswordMatch(rawPassword, encodedPassword))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.PASSWORD_NOT_MATCH.getMessage());

            verify(mockPasswordEncryptionService).matches(rawPassword, encodedPassword);
        }

        @Test
        @DisplayName("null 원본 비밀번호로 검증하면 false를 반환한다")
        void validatePasswordMatchWithNullRawPassword() {
            String rawPassword = null;
            String encodedPassword = "encodedPassword123";
            when(mockPasswordEncryptionService.matches(rawPassword, encodedPassword)).thenReturn(false);

            assertThatThrownBy(() -> userPasswordPolicy.validatePasswordMatch(rawPassword, encodedPassword))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.PASSWORD_NOT_MATCH.getMessage());

            verify(mockPasswordEncryptionService).matches(rawPassword, encodedPassword);
        }

        @Test
        @DisplayName("null 암호화된 비밀번호로 검증하면 false를 반환한다")
        void validatePasswordMatchWithNullEncodedPassword() {
            String rawPassword = "password123";
            String encodedPassword = null;
            when(mockPasswordEncryptionService.matches(rawPassword, encodedPassword)).thenReturn(false);

            assertThatThrownBy(() -> userPasswordPolicy.validatePasswordMatch(rawPassword, encodedPassword))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.PASSWORD_NOT_MATCH.getMessage());

            verify(mockPasswordEncryptionService).matches(rawPassword, encodedPassword);
        }
    }

    @Nested
    @DisplayName("비밀번호 암호화 테스트")
    class EncodePasswordTest {

        @Test
        @DisplayName("유효한 비밀번호를 암호화할 수 있다")
        void encodeValidPassword() {
            String rawPassword = "password123";
            String expectedEncodedPassword = "encodedPassword123";
            when(mockPasswordEncryptionService.encode(rawPassword)).thenReturn(expectedEncodedPassword);

            String result = userPasswordPolicy.encodePassword(rawPassword);

            assertThat(result).isEqualTo(expectedEncodedPassword);
            verify(mockPasswordEncryptionService).encode(rawPassword);
        }

        @Test
        @DisplayName("최소 길이(8자) 비밀번호를 암호화할 수 있다")
        void encodeMinimumLengthPassword() {
            String rawPassword = "12345678";
            String expectedEncodedPassword = "encoded12345678";
            when(mockPasswordEncryptionService.encode(rawPassword)).thenReturn(expectedEncodedPassword);

            String result = userPasswordPolicy.encodePassword(rawPassword);

            assertThat(result).isEqualTo(expectedEncodedPassword);
            verify(mockPasswordEncryptionService).encode(rawPassword);
        }

        @Test
        @DisplayName("최대 길이(50자) 비밀번호를 암호화할 수 있다")
        void encodeMaximumLengthPassword() {
            String rawPassword = "a".repeat(50);
            String expectedEncodedPassword = "encoded" + rawPassword;
            when(mockPasswordEncryptionService.encode(rawPassword)).thenReturn(expectedEncodedPassword);

            String result = userPasswordPolicy.encodePassword(rawPassword);

            assertThat(result).isEqualTo(expectedEncodedPassword);
            verify(mockPasswordEncryptionService).encode(rawPassword);
        }

        @Test
        @DisplayName("특수문자가 포함된 비밀번호를 암호화할 수 있다")
        void encodePasswordWithSpecialCharacters() {
            String rawPassword = "password123!@#$%";
            String expectedEncodedPassword = "encodedSpecialPassword";
            when(mockPasswordEncryptionService.encode(rawPassword)).thenReturn(expectedEncodedPassword);

            String result = userPasswordPolicy.encodePassword(rawPassword);

            assertThat(result).isEqualTo(expectedEncodedPassword);
            verify(mockPasswordEncryptionService).encode(rawPassword);
        }
    }

    @Nested
    @DisplayName("원본 비밀번호 검증 테스트")
    class ValidateRawPasswordTest {

        @Test
        @DisplayName("null 비밀번호로 암호화 시 예외가 발생한다")
        void encodeNullPasswordThrowsException() {
            assertThatThrownBy(() -> userPasswordPolicy.encodePassword(null))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_PASSWORD_FORMAT.getMessage());
        }

        @Test
        @DisplayName("빈 비밀번호로 암호화 시 예외가 발생한다")
        void encodeEmptyPasswordThrowsException() {
            assertThatThrownBy(() -> userPasswordPolicy.encodePassword(""))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_PASSWORD_FORMAT.getMessage());
        }

        @Test
        @DisplayName("공백 비밀번호로 암호화 시 예외가 발생한다")
        void encodeBlankPasswordThrowsException() {
            assertThatThrownBy(() -> userPasswordPolicy.encodePassword("   "))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_PASSWORD_FORMAT.getMessage());
        }

        @Test
        @DisplayName("8자 미만의 비밀번호로 암호화 시 예외가 발생한다")
        void encodeTooShortPasswordThrowsException() {
            String shortPassword = "1234567";

            assertThatThrownBy(() -> userPasswordPolicy.encodePassword(shortPassword))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_PASSWORD_FORMAT.getMessage());
        }

        @Test
        @DisplayName("50자를 초과하는 비밀번호로 암호화 시 예외가 발생한다")
        void encodeTooLongPasswordThrowsException() {
            String longPassword = "a".repeat(51);

            assertThatThrownBy(() -> userPasswordPolicy.encodePassword(longPassword))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_PASSWORD_FORMAT.getMessage());
        }

        @Test
        @DisplayName("탭 문자가 포함된 비밀번호를 암호화할 수 있다")
        void encodePasswordWithTabCharacterSuccessfully() {
            String passwordWithTab = "password\t123";
            String expectedEncodedPassword = "encodedTabPassword";
            when(mockPasswordEncryptionService.encode(passwordWithTab)).thenReturn(expectedEncodedPassword);

            String result = userPasswordPolicy.encodePassword(passwordWithTab);

            assertThat(result).isEqualTo(expectedEncodedPassword);
        }

        @Test
        @DisplayName("개행 문자가 포함된 비밀번호를 암호화할 수 있다")
        void encodePasswordWithNewlineCharacterSuccessfully() {
            String passwordWithNewline = "password\n123";
            String expectedEncodedPassword = "encodedNewlinePassword";
            when(mockPasswordEncryptionService.encode(passwordWithNewline)).thenReturn(expectedEncodedPassword);

            String result = userPasswordPolicy.encodePassword(passwordWithNewline);

            assertThat(result).isEqualTo(expectedEncodedPassword);
        }
    }

    @Nested
    @DisplayName("비밀번호 정책 경계값 테스트")
    class PasswordPolicyBoundaryTest {

        @Test
        @DisplayName("정확히 8자인 비밀번호는 유효하다")
        void exactlyEightCharacterPasswordIsValid() {
            String password = "12345678";
            String expectedEncodedPassword = "encoded12345678";
            when(mockPasswordEncryptionService.encode(password)).thenReturn(expectedEncodedPassword);

            String result = userPasswordPolicy.encodePassword(password);

            assertThat(result).isEqualTo(expectedEncodedPassword);
        }

        @Test
        @DisplayName("정확히 50자인 비밀번호는 유효하다")
        void exactlyFiftyCharacterPasswordIsValid() {
            String password = "a".repeat(50);
            String expectedEncodedPassword = "encodedFiftyChars";
            when(mockPasswordEncryptionService.encode(password)).thenReturn(expectedEncodedPassword);

            String result = userPasswordPolicy.encodePassword(password);

            assertThat(result).isEqualTo(expectedEncodedPassword);
        }

        @Test
        @DisplayName("정확히 7자인 비밀번호는 무효하다")
        void exactlySevenCharacterPasswordIsInvalid() {
            String password = "1234567";

            assertThatThrownBy(() -> userPasswordPolicy.encodePassword(password))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_PASSWORD_FORMAT.getMessage());
        }

        @Test
        @DisplayName("정확히 51자인 비밀번호는 무효하다")
        void exactlyFiftyOneCharacterPasswordIsInvalid() {
            String password = "a".repeat(51);

            assertThatThrownBy(() -> userPasswordPolicy.encodePassword(password))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_PASSWORD_FORMAT.getMessage());
        }
    }

    @Nested
    @DisplayName("비밀번호 패턴 테스트")
    class PasswordPatternTest {

        @Test
        @DisplayName("숫자만으로 구성된 비밀번호를 암호화할 수 있다")
        void encodeNumericPassword() {
            String numericPassword = "12345678";
            String expectedEncodedPassword = "encodedNumeric";
            when(mockPasswordEncryptionService.encode(numericPassword)).thenReturn(expectedEncodedPassword);

            String result = userPasswordPolicy.encodePassword(numericPassword);

            assertThat(result).isEqualTo(expectedEncodedPassword);
        }

        @Test
        @DisplayName("문자만으로 구성된 비밀번호를 암호화할 수 있다")
        void encodeAlphabeticPassword() {
            String alphabeticPassword = "abcdefgh";
            String expectedEncodedPassword = "encodedAlphabetic";
            when(mockPasswordEncryptionService.encode(alphabeticPassword)).thenReturn(expectedEncodedPassword);

            String result = userPasswordPolicy.encodePassword(alphabeticPassword);

            assertThat(result).isEqualTo(expectedEncodedPassword);
        }

        @Test
        @DisplayName("대소문자 혼합 비밀번호를 암호화할 수 있다")
        void encodeMixedCasePassword() {
            String mixedCasePassword = "AbCdEfGh";
            String expectedEncodedPassword = "encodedMixedCase";
            when(mockPasswordEncryptionService.encode(mixedCasePassword)).thenReturn(expectedEncodedPassword);

            String result = userPasswordPolicy.encodePassword(mixedCasePassword);

            assertThat(result).isEqualTo(expectedEncodedPassword);
        }

        @Test
        @DisplayName("문자, 숫자, 특수문자 혼합 비밀번호를 암호화할 수 있다")
        void encodeComplexPassword() {
            String complexPassword = "AbC123!@#";
            String expectedEncodedPassword = "encodedComplex";
            when(mockPasswordEncryptionService.encode(complexPassword)).thenReturn(expectedEncodedPassword);

            String result = userPasswordPolicy.encodePassword(complexPassword);

            assertThat(result).isEqualTo(expectedEncodedPassword);
        }

        @Test
        @DisplayName("유니코드 문자가 포함된 비밀번호로 암호화 시 예외가 발생한다")
        void encodeUnicodePasswordThrowsException() {
            String unicodePassword = "비밀번호123";

            assertThatThrownBy(() -> userPasswordPolicy.encodePassword(unicodePassword))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.INVALID_PASSWORD_FORMAT.getMessage());
        }
    }

    @Nested
    @DisplayName("의존성 주입 테스트")
    class DependencyInjectionTest {

        @Test
        @DisplayName("생성자를 통해 PasswordEncryptionService가 주입된다")
        void constructorInjectsPasswordEncryptionService() {
            PasswordEncryptionService testService = mock(PasswordEncryptionService.class);
            
            UserPasswordPolicy policy = new UserPasswordPolicy(testService);
            
            assertThat(policy).isNotNull();
        }
    }
}