package com.backend.immilog.user.domain.service;

import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@DisplayName("UserPasswordPolicy 테스트")
@ExtendWith(MockitoExtension.class)
class UserPasswordPolicyTest {

    @Mock
    private PasswordEncryptionService passwordEncoder;

    private UserPasswordPolicy userPasswordPolicy;

    @BeforeEach
    void setUp() {
        userPasswordPolicy = new UserPasswordPolicy(passwordEncoder);
    }

    @Test
    @DisplayName("올바른 비밀번호 매칭을 검증할 수 있다")
    void validatePasswordMatchSuccessfully() {
        // given
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";

        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> userPasswordPolicy.validatePasswordMatch(rawPassword, encodedPassword));
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않을 때 예외가 발생한다")
    void validatePasswordMatchWithMismatch() {
        // given
        String rawPassword = "password123";
        String encodedPassword = "differentEncodedPassword";

        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(false);

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> userPasswordPolicy.validatePasswordMatch(rawPassword, encodedPassword));

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.PASSWORD_NOT_MATCH);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    @DisplayName("유효한 비밀번호를 인코딩할 수 있다")
    void encodeValidPassword() {
        // given
        String rawPassword = "password123";
        String expectedEncodedPassword = "encodedPassword123";

        given(passwordEncoder.encode(rawPassword)).willReturn(expectedEncodedPassword);

        // when
        String encodedPassword = userPasswordPolicy.encodePassword(rawPassword);

        // then
        assertThat(encodedPassword).isEqualTo(expectedEncodedPassword);
        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    @DisplayName("null 비밀번호 인코딩 시 예외가 발생한다")
    void encodeNullPassword() {
        // given
        String nullPassword = null;

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> userPasswordPolicy.encodePassword(nullPassword));

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_PASSWORD_FORMAT);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("빈 비밀번호 인코딩 시 예외가 발생한다")
    void encodeEmptyPassword() {
        // given
        String emptyPassword = "";

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> userPasswordPolicy.encodePassword(emptyPassword));

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_PASSWORD_FORMAT);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("공백 비밀번호 인코딩 시 예외가 발생한다")
    void encodeBlankPassword() {
        // given
        String blankPassword = "   ";

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> userPasswordPolicy.encodePassword(blankPassword));

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_PASSWORD_FORMAT);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("8자 미만 비밀번호 인코딩 시 예외가 발생한다")
    void encodeTooShortPassword() {
        // given
        String shortPassword = "1234567"; // 7자

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> userPasswordPolicy.encodePassword(shortPassword));

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_PASSWORD_FORMAT);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("50자 초과 비밀번호 인코딩 시 예외가 발생한다")
    void encodeTooLongPassword() {
        // given
        String longPassword = "a".repeat(51); // 51자

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> userPasswordPolicy.encodePassword(longPassword));

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_PASSWORD_FORMAT);
        verifyNoInteractions(passwordEncoder);
    }

    @ParameterizedTest
    @ValueSource(ints = {8, 25, 50})
    @DisplayName("유효한 길이의 비밀번호를 인코딩할 수 있다")
    void encodeValidLengthPasswords(int passwordLength) {
        // given
        String validPassword = "a".repeat(passwordLength);
        String expectedEncodedPassword = "encoded_" + validPassword;

        given(passwordEncoder.encode(validPassword)).willReturn(expectedEncodedPassword);

        // when
        String encodedPassword = userPasswordPolicy.encodePassword(validPassword);

        // then
        assertThat(encodedPassword).isEqualTo(expectedEncodedPassword);
        verify(passwordEncoder).encode(validPassword);
    }

    @Test
    @DisplayName("다양한 문자가 포함된 비밀번호를 인코딩할 수 있다")
    void encodePasswordWithVariousCharacters() {
        // given
        String complexPassword = "Password123!@#$%^&*()";
        String expectedEncodedPassword = "encodedComplexPassword";

        given(passwordEncoder.encode(complexPassword)).willReturn(expectedEncodedPassword);

        // when
        String encodedPassword = userPasswordPolicy.encodePassword(complexPassword);

        // then
        assertThat(encodedPassword).isEqualTo(expectedEncodedPassword);
        verify(passwordEncoder).encode(complexPassword);
    }

    @Test
    @DisplayName("동일한 원본 비밀번호에 대해 일관된 매칭 결과를 제공한다")
    void consistentPasswordMatching() {
        // given
        String rawPassword = "password123";
        String correctEncodedPassword = "correctEncodedPassword";
        String wrongEncodedPassword = "wrongEncodedPassword";

        given(passwordEncoder.matches(rawPassword, correctEncodedPassword)).willReturn(true);
        given(passwordEncoder.matches(rawPassword, wrongEncodedPassword)).willReturn(false);

        // when & then
        assertDoesNotThrow(() -> userPasswordPolicy.validatePasswordMatch(rawPassword, correctEncodedPassword));

        UserException exception = assertThrows(UserException.class,
                () -> userPasswordPolicy.validatePasswordMatch(rawPassword, wrongEncodedPassword));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.PASSWORD_NOT_MATCH);

        verify(passwordEncoder).matches(rawPassword, correctEncodedPassword);
        verify(passwordEncoder).matches(rawPassword, wrongEncodedPassword);
    }

    @Test
    @DisplayName("경계값 길이의 비밀번호를 정상 처리한다")
    void handleBoundaryLengthPasswords() {
        // given
        String minLengthPassword = "12345678"; // 8자
        String maxLengthPassword = "a".repeat(50); // 50자
        String expectedMinEncoded = "encodedMin";
        String expectedMaxEncoded = "encodedMax";

        given(passwordEncoder.encode(minLengthPassword)).willReturn(expectedMinEncoded);
        given(passwordEncoder.encode(maxLengthPassword)).willReturn(expectedMaxEncoded);

        // when
        String minEncoded = userPasswordPolicy.encodePassword(minLengthPassword);
        String maxEncoded = userPasswordPolicy.encodePassword(maxLengthPassword);

        // then
        assertThat(minEncoded).isEqualTo(expectedMinEncoded);
        assertThat(maxEncoded).isEqualTo(expectedMaxEncoded);
        verify(passwordEncoder).encode(minLengthPassword);
        verify(passwordEncoder).encode(maxLengthPassword);
    }
}