package com.backend.immilog.user.domain.model;

import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserId 도메인 테스트")
class UserIdTest {

    @Test
    @DisplayName("정상적인 값으로 UserId를 생성할 수 있다")
    void createUserIdWithValidValue() {
        // given
        String validValue = "user123";

        // when
        UserId userId = UserId.of(validValue);

        // then
        assertThat(userId.value()).isEqualTo(validValue);
    }

    @Test
    @DisplayName("null 값으로 UserId 생성 시 예외가 발생한다")
    void createUserIdWithNullValue() {
        // given
        String nullValue = null;

        // when & then
        UserException exception = assertThrows(UserException.class, () -> UserId.of(nullValue));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.ENTITY_TO_DOMAIN_ERROR);
    }

    @Test
    @DisplayName("빈 문자열로 UserId 생성 시 예외가 발생한다")
    void createUserIdWithEmptyValue() {
        // given
        String emptyValue = "";

        // when & then
        UserException exception = assertThrows(UserException.class, () -> UserId.of(emptyValue));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.ENTITY_TO_DOMAIN_ERROR);
    }

    @Test
    @DisplayName("공백 문자열로 UserId 생성 시 예외가 발생한다")
    void createUserIdWithBlankValue() {
        // given
        String blankValue = "   ";

        // when & then
        UserException exception = assertThrows(UserException.class, () -> UserId.of(blankValue));
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.ENTITY_TO_DOMAIN_ERROR);
    }

    @Test
    @DisplayName("같은 값을 가진 UserId끼리 동등성 비교가 가능하다")
    void equalsWithSameValue() {
        // given
        String value = "user123";
        UserId userId1 = UserId.of(value);
        UserId userId2 = UserId.of(value);

        // when & then
        assertTrue(userId1.equals(userId2));
        assertTrue(userId2.equals(userId1));
    }

    @Test
    @DisplayName("다른 값을 가진 UserId끼리 동등성 비교가 정상 동작한다")
    void equalsWithDifferentValue() {
        // given
        UserId userId1 = UserId.of("user123");
        UserId userId2 = UserId.of("user456");

        // when & then
        assertFalse(userId1.equals(userId2));
        assertFalse(userId2.equals(userId1));
    }

    @Test
    @DisplayName("null과의 동등성 비교가 정상 동작한다")
    void equalsWithNull() {
        // given
        UserId userId = UserId.of("user123");

        // when & then
        assertFalse(userId.equals(null));
    }

    @Test
    @DisplayName("Record의 기본 equals 메서드도 정상 동작한다")
    void recordEqualsMethod() {
        // given
        UserId userId1 = UserId.of("user123");
        UserId userId2 = UserId.of("user123");
        UserId userId3 = UserId.of("user456");

        // when & then
        assertThat(userId1).isEqualTo(userId2);
        assertThat(userId1).isNotEqualTo(userId3);
        assertThat(userId1).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Record의 hashCode가 정상 동작한다")
    void recordHashCode() {
        // given
        UserId userId1 = UserId.of("user123");
        UserId userId2 = UserId.of("user123");

        // when & then
        assertThat(userId1.hashCode()).isEqualTo(userId2.hashCode());
    }

    @Test
    @DisplayName("Record의 toString이 정상 동작한다")
    void recordToString() {
        // given
        UserId userId = UserId.of("user123");

        // when
        String toString = userId.toString();

        // then
        assertThat(toString).contains("user123");
        assertThat(toString).contains("UserId");
    }
}