package com.backend.immilog.user.domain.model;

import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserIdTest {

    @Nested
    @DisplayName("UserId 생성 테스트")
    class UserIdCreationTest {

        @Test
        @DisplayName("유효한 값으로 UserId를 생성할 수 있다")
        void createUserIdWithValidValue() {
            String validValue = "user123";

            UserId userId = new UserId(validValue);

            assertThat(userId.value()).isEqualTo(validValue);
        }

        @Test
        @DisplayName("of 팩토리 메서드로 UserId를 생성할 수 있다")
        void createUserIdWithFactoryMethod() {
            String validValue = "user456";

            UserId userId = UserId.of(validValue);

            assertThat(userId.value()).isEqualTo(validValue);
        }

        @Test
        @DisplayName("null 값으로 생성 시 예외가 발생한다")
        void createUserIdWithNullThrowsException() {
            assertThatThrownBy(() -> new UserId(null))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.ENTITY_TO_DOMAIN_ERROR.getMessage());
        }

        @Test
        @DisplayName("빈 문자열로 생성 시 예외가 발생한다")
        void createUserIdWithEmptyStringThrowsException() {
            assertThatThrownBy(() -> new UserId(""))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.ENTITY_TO_DOMAIN_ERROR.getMessage());
        }

        @Test
        @DisplayName("공백 문자열로 생성 시 예외가 발생한다")
        void createUserIdWithBlankStringThrowsException() {
            assertThatThrownBy(() -> new UserId("   "))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.ENTITY_TO_DOMAIN_ERROR.getMessage());
        }
    }

    @Nested
    @DisplayName("UserId 동등성 테스트")
    class UserIdEqualityTest {

        @Test
        @DisplayName("같은 값을 가진 UserId는 동등하다")
        void userIdsWithSameValueAreEqual() {
            String value = "user123";
            UserId userId1 = UserId.of(value);
            UserId userId2 = UserId.of(value);

            boolean result = userId1.equals(userId2);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("다른 값을 가진 UserId는 동등하지 않다")
        void userIdsWithDifferentValueAreNotEqual() {
            UserId userId1 = UserId.of("user123");
            UserId userId2 = UserId.of("user456");

            boolean result = userId1.equals(userId2);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("null과 비교하면 동등하지 않다")
        void userIdIsNotEqualToNull() {
            UserId userId = UserId.of("user123");

            boolean result = userId.equals(null);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("record의 기본 equals 메서드도 올바르게 동작한다")
        void recordEqualsWorksCorrectly() {
            UserId userId1 = UserId.of("user123");
            UserId userId2 = UserId.of("user123");
            UserId userId3 = UserId.of("user456");

            assertThat(userId1).isEqualTo(userId2);
            assertThat(userId1).isNotEqualTo(userId3);
            assertThat(userId1.hashCode()).isEqualTo(userId2.hashCode());
        }
    }

    @Nested
    @DisplayName("UserId 특수 케이스 테스트")
    class UserIdSpecialCasesTest {

        @Test
        @DisplayName("매우 긴 문자열로도 UserId를 생성할 수 있다")
        void createUserIdWithVeryLongString() {
            String longValue = "a".repeat(1000);

            UserId userId = UserId.of(longValue);

            assertThat(userId.value()).isEqualTo(longValue);
            assertThat(userId.value()).hasSize(1000);
        }

        @Test
        @DisplayName("특수 문자가 포함된 문자열로도 UserId를 생성할 수 있다")
        void createUserIdWithSpecialCharacters() {
            String specialValue = "user-123_@#$%";

            UserId userId = UserId.of(specialValue);

            assertThat(userId.value()).isEqualTo(specialValue);
        }

        @Test
        @DisplayName("숫자로만 구성된 문자열로도 UserId를 생성할 수 있다")
        void createUserIdWithNumericString() {
            String numericValue = "123456789";

            UserId userId = UserId.of(numericValue);

            assertThat(userId.value()).isEqualTo(numericValue);
        }

        @Test
        @DisplayName("유니코드 문자가 포함된 문자열로도 UserId를 생성할 수 있다")
        void createUserIdWithUnicodeCharacters() {
            String unicodeValue = "사용자123";

            UserId userId = UserId.of(unicodeValue);

            assertThat(userId.value()).isEqualTo(unicodeValue);
        }
    }

    @Nested
    @DisplayName("UserId toString 테스트")
    class UserIdToStringTest {

        @Test
        @DisplayName("toString 메서드가 올바르게 동작한다")
        void toStringWorksCorrectly() {
            String value = "user123";
            UserId userId = UserId.of(value);

            String result = userId.toString();

            assertThat(result).contains(value);
            assertThat(result).contains("UserId");
        }
    }
}