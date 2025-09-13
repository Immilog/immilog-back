package com.backend.immilog.user.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserStatusTest {

    @Nested
    @DisplayName("UserStatus 기본 속성 테스트")
    class UserStatusBasicPropertiesTest {

        @Test
        @DisplayName("ACTIVE 상수값이 올바르게 정의되어 있다")
        void activeConstantIsCorrectlyDefined() {
            assertThat(UserStatus.ACTIVE).isNotNull();
            assertThat(UserStatus.ACTIVE.name()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("INACTIVE 상수값이 올바르게 정의되어 있다")
        void inactiveConstantIsCorrectlyDefined() {
            assertThat(UserStatus.INACTIVE).isNotNull();
            assertThat(UserStatus.INACTIVE.name()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("PENDING 상수값이 올바르게 정의되어 있다")
        void pendingConstantIsCorrectlyDefined() {
            assertThat(UserStatus.PENDING).isNotNull();
            assertThat(UserStatus.PENDING.name()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("REPORTED 상수값이 올바르게 정의되어 있다")
        void reportedConstantIsCorrectlyDefined() {
            assertThat(UserStatus.REPORTED).isNotNull();
            assertThat(UserStatus.REPORTED.name()).isEqualTo("REPORTED");
        }

        @Test
        @DisplayName("BLOCKED 상수값이 올바르게 정의되어 있다")
        void blockedConstantIsCorrectlyDefined() {
            assertThat(UserStatus.BLOCKED).isNotNull();
            assertThat(UserStatus.BLOCKED.name()).isEqualTo("BLOCKED");
        }
    }

    @Nested
    @DisplayName("UserStatus enum 기본 동작 테스트")
    class UserStatusEnumBehaviorTest {

        @Test
        @DisplayName("UserStatus enum의 모든 값을 검증한다")
        void allUserStatusValues() {
            UserStatus[] values = UserStatus.values();

            assertThat(values).hasSize(5);
            assertThat(values).containsExactlyInAnyOrder(
                    UserStatus.ACTIVE,
                    UserStatus.INACTIVE,
                    UserStatus.PENDING,
                    UserStatus.REPORTED,
                    UserStatus.BLOCKED
            );
        }

        @Test
        @DisplayName("valueOf 메서드로 문자열을 통해 enum 값을 가져올 수 있다")
        void valueOfMethod() {
            assertThat(UserStatus.valueOf("ACTIVE")).isEqualTo(UserStatus.ACTIVE);
            assertThat(UserStatus.valueOf("INACTIVE")).isEqualTo(UserStatus.INACTIVE);
            assertThat(UserStatus.valueOf("PENDING")).isEqualTo(UserStatus.PENDING);
            assertThat(UserStatus.valueOf("REPORTED")).isEqualTo(UserStatus.REPORTED);
            assertThat(UserStatus.valueOf("BLOCKED")).isEqualTo(UserStatus.BLOCKED);
        }

        @Test
        @DisplayName("enum 값의 순서가 올바르다")
        void enumOrderIsCorrect() {
            UserStatus[] values = UserStatus.values();

            assertThat(values[0]).isEqualTo(UserStatus.ACTIVE);
            assertThat(values[1]).isEqualTo(UserStatus.INACTIVE);
            assertThat(values[2]).isEqualTo(UserStatus.PENDING);
            assertThat(values[3]).isEqualTo(UserStatus.REPORTED);
            assertThat(values[4]).isEqualTo(UserStatus.BLOCKED);
        }

        @Test
        @DisplayName("enum 값의 ordinal이 올바르다")
        void ordinalValues() {
            assertThat(UserStatus.ACTIVE.ordinal()).isEqualTo(0);
            assertThat(UserStatus.INACTIVE.ordinal()).isEqualTo(1);
            assertThat(UserStatus.PENDING.ordinal()).isEqualTo(2);
            assertThat(UserStatus.REPORTED.ordinal()).isEqualTo(3);
            assertThat(UserStatus.BLOCKED.ordinal()).isEqualTo(4);
        }

        @Test
        @DisplayName("enum 값의 name이 올바르다")
        void enumNames() {
            assertThat(UserStatus.ACTIVE.name()).isEqualTo("ACTIVE");
            assertThat(UserStatus.INACTIVE.name()).isEqualTo("INACTIVE");
            assertThat(UserStatus.PENDING.name()).isEqualTo("PENDING");
            assertThat(UserStatus.REPORTED.name()).isEqualTo("REPORTED");
            assertThat(UserStatus.BLOCKED.name()).isEqualTo("BLOCKED");
        }
    }

    @Nested
    @DisplayName("UserStatus 동등성 테스트")
    class UserStatusEqualityTest {

        @Test
        @DisplayName("같은 UserStatus enum 값은 동등하다")
        void sameUserStatusValuesAreEqual() {
            UserStatus status1 = UserStatus.ACTIVE;
            UserStatus status2 = UserStatus.ACTIVE;

            assertThat(status1).isEqualTo(status2);
            assertThat(status1 == status2).isTrue();
            assertThat(status1.hashCode()).isEqualTo(status2.hashCode());
        }

        @Test
        @DisplayName("다른 UserStatus enum 값은 동등하지 않다")
        void differentUserStatusValuesAreNotEqual() {
            UserStatus status1 = UserStatus.ACTIVE;
            UserStatus status2 = UserStatus.INACTIVE;

            assertThat(status1).isNotEqualTo(status2);
            assertThat(status1 == status2).isFalse();
        }

        @Test
        @DisplayName("equals 메서드를 통한 비교가 올바르게 동작한다")
        void equalsMethodWorksCorrectly() {
            UserStatus activeStatus = UserStatus.ACTIVE;

            assertThat(activeStatus.equals(UserStatus.ACTIVE)).isTrue();
            assertThat(activeStatus.equals(UserStatus.INACTIVE)).isFalse();
            assertThat(activeStatus.equals(UserStatus.PENDING)).isFalse();
            assertThat(activeStatus.equals(UserStatus.REPORTED)).isFalse();
            assertThat(activeStatus.equals(UserStatus.BLOCKED)).isFalse();
            assertThat(activeStatus.equals(null)).isFalse();
            assertThat(activeStatus.equals("ACTIVE")).isFalse();
        }
    }

    @Nested
    @DisplayName("UserStatus toString 테스트")
    class UserStatusToStringTest {

        @Test
        @DisplayName("toString 메서드가 올바르게 동작한다")
        void toStringWorksCorrectly() {
            assertThat(UserStatus.ACTIVE.toString()).isEqualTo("ACTIVE");
            assertThat(UserStatus.INACTIVE.toString()).isEqualTo("INACTIVE");
            assertThat(UserStatus.PENDING.toString()).isEqualTo("PENDING");
            assertThat(UserStatus.REPORTED.toString()).isEqualTo("REPORTED");
            assertThat(UserStatus.BLOCKED.toString()).isEqualTo("BLOCKED");
        }
    }

    @Nested
    @DisplayName("UserStatus 비즈니스 로직 테스트")
    class UserStatusBusinessLogicTest {

        @Test
        @DisplayName("모든 UserStatus 값이 고유하다")
        void allUserStatusValuesAreUnique() {
            UserStatus[] values = UserStatus.values();

            for (int i = 0; i < values.length; i++) {
                for (int j = i + 1; j < values.length; j++) {
                    assertThat(values[i]).isNotEqualTo(values[j]);
                    assertThat(values[i].name()).isNotEqualTo(values[j].name());
                    assertThat(values[i].ordinal()).isNotEqualTo(values[j].ordinal());
                }
            }
        }

        @Test
        @DisplayName("UserStatus enum은 5개의 고정된 상태를 가진다")
        void userStatusHasFiveFixedStates() {
            UserStatus[] values = UserStatus.values();

            assertThat(values).hasSize(5);
            assertThat(values).contains(
                    UserStatus.ACTIVE,
                    UserStatus.INACTIVE,
                    UserStatus.PENDING,
                    UserStatus.REPORTED,
                    UserStatus.BLOCKED
            );
        }

        @Test
        @DisplayName("각 상태가 예상되는 의미를 가진다")
        void eachStatusHasExpectedMeaning() {
            assertThat(UserStatus.ACTIVE.name()).contains("ACTIVE");
            assertThat(UserStatus.INACTIVE.name()).contains("INACTIVE");
            assertThat(UserStatus.PENDING.name()).contains("PENDING");
            assertThat(UserStatus.REPORTED.name()).contains("REPORTED");
            assertThat(UserStatus.BLOCKED.name()).contains("BLOCKED");
        }
    }

    @Nested
    @DisplayName("UserStatus 상태 전환 시나리오 테스트")
    class UserStatusTransitionTest {

        @Test
        @DisplayName("모든 UserStatus 값을 순회할 수 있다")
        void canIterateOverAllUserStatusValues() {
            int count = 0;
            for (UserStatus status : UserStatus.values()) {
                assertThat(status).isNotNull();
                count++;
            }
            assertThat(count).isEqualTo(5);
        }

        @Test
        @DisplayName("UserStatus switch 문에서 모든 케이스를 처리할 수 있다")
        void canHandleAllCasesInSwitchStatement() {
            for (UserStatus status : UserStatus.values()) {
                String result = switch (status) {
                    case ACTIVE -> "활성";
                    case INACTIVE -> "비활성";
                    case PENDING -> "대기";
                    case REPORTED -> "신고됨";
                    case BLOCKED -> "차단됨";
                };
                assertThat(result).isNotNull();
                assertThat(result).isNotEmpty();
            }
        }

        @Test
        @DisplayName("UserStatus를 문자열로 변환하고 다시 enum으로 변환할 수 있다")
        void canConvertToStringAndBackToEnum() {
            for (UserStatus originalStatus : UserStatus.values()) {
                String statusString = originalStatus.toString();
                UserStatus convertedStatus = UserStatus.valueOf(statusString);
                
                assertThat(convertedStatus).isEqualTo(originalStatus);
            }
        }
    }
}