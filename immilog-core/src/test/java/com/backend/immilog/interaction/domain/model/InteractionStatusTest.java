package com.backend.immilog.interaction.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InteractionStatusTest {

    @Nested
    @DisplayName("InteractionStatus 기본 속성 테스트")
    class InteractionStatusBasicPropertiesTest {

        @Test
        @DisplayName("ACTIVE 상수값이 올바르게 정의되어 있다")
        void activeConstantIsCorrectlyDefined() {
            assertThat(InteractionStatus.ACTIVE).isNotNull();
            assertThat(InteractionStatus.ACTIVE.name()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("INACTIVE 상수값이 올바르게 정의되어 있다")
        void inactiveConstantIsCorrectlyDefined() {
            assertThat(InteractionStatus.INACTIVE).isNotNull();
            assertThat(InteractionStatus.INACTIVE.name()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("InteractionStatus enum 기본 동작 테스트")
    class InteractionStatusEnumBehaviorTest {

        @Test
        @DisplayName("InteractionStatus enum의 모든 값을 검증한다")
        void allInteractionStatusValues() {
            InteractionStatus[] values = InteractionStatus.values();

            assertThat(values).hasSize(2);
            assertThat(values).containsExactlyInAnyOrder(
                    InteractionStatus.ACTIVE,
                    InteractionStatus.INACTIVE
            );
        }

        @Test
        @DisplayName("valueOf 메서드로 문자열을 통해 enum 값을 가져올 수 있다")
        void valueOfMethod() {
            assertThat(InteractionStatus.valueOf("ACTIVE")).isEqualTo(InteractionStatus.ACTIVE);
            assertThat(InteractionStatus.valueOf("INACTIVE")).isEqualTo(InteractionStatus.INACTIVE);
        }

        @Test
        @DisplayName("enum 값의 순서가 올바르다")
        void enumOrderIsCorrect() {
            InteractionStatus[] values = InteractionStatus.values();

            assertThat(values[0]).isEqualTo(InteractionStatus.ACTIVE);
            assertThat(values[1]).isEqualTo(InteractionStatus.INACTIVE);
        }

        @Test
        @DisplayName("enum 값의 ordinal이 올바르다")
        void ordinalValues() {
            assertThat(InteractionStatus.ACTIVE.ordinal()).isEqualTo(0);
            assertThat(InteractionStatus.INACTIVE.ordinal()).isEqualTo(1);
        }

        @Test
        @DisplayName("enum 값의 name이 올바르다")
        void enumNames() {
            assertThat(InteractionStatus.ACTIVE.name()).isEqualTo("ACTIVE");
            assertThat(InteractionStatus.INACTIVE.name()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("InteractionStatus isActive 메서드 테스트")
    class InteractionStatusIsActiveTest {

        @Test
        @DisplayName("ACTIVE는 활성 상태이다")
        void activeIsActiveStatus() {
            boolean result = InteractionStatus.ACTIVE.isActive();

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("INACTIVE는 활성 상태가 아니다")
        void inactiveIsNotActiveStatus() {
            boolean result = InteractionStatus.INACTIVE.isActive();

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("InteractionStatus 동등성 테스트")
    class InteractionStatusEqualityTest {

        @Test
        @DisplayName("같은 InteractionStatus enum 값은 동등하다")
        void sameInteractionStatusValuesAreEqual() {
            InteractionStatus status1 = InteractionStatus.ACTIVE;
            InteractionStatus status2 = InteractionStatus.ACTIVE;

            assertThat(status1).isEqualTo(status2);
            assertThat(status1 == status2).isTrue();
            assertThat(status1.hashCode()).isEqualTo(status2.hashCode());
        }

        @Test
        @DisplayName("다른 InteractionStatus enum 값은 동등하지 않다")
        void differentInteractionStatusValuesAreNotEqual() {
            InteractionStatus status1 = InteractionStatus.ACTIVE;
            InteractionStatus status2 = InteractionStatus.INACTIVE;

            assertThat(status1).isNotEqualTo(status2);
            assertThat(status1 == status2).isFalse();
        }

        @Test
        @DisplayName("equals 메서드를 통한 비교가 올바르게 동작한다")
        void equalsMethodWorksCorrectly() {
            InteractionStatus activeStatus = InteractionStatus.ACTIVE;

            assertThat(activeStatus.equals(InteractionStatus.ACTIVE)).isTrue();
            assertThat(activeStatus.equals(InteractionStatus.INACTIVE)).isFalse();
            assertThat(activeStatus.equals(null)).isFalse();
            assertThat(activeStatus.equals("ACTIVE")).isFalse();
        }
    }

    @Nested
    @DisplayName("InteractionStatus toString 테스트")
    class InteractionStatusToStringTest {

        @Test
        @DisplayName("toString 메서드가 올바르게 동작한다")
        void toStringWorksCorrectly() {
            assertThat(InteractionStatus.ACTIVE.toString()).isEqualTo("ACTIVE");
            assertThat(InteractionStatus.INACTIVE.toString()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("InteractionStatus 비즈니스 로직 테스트")
    class InteractionStatusBusinessLogicTest {

        @Test
        @DisplayName("활성 상태 판별 로직이 정확하다")
        void activeStatusDetectionLogic() {
            for (InteractionStatus status : InteractionStatus.values()) {
                if (status == InteractionStatus.ACTIVE) {
                    assertThat(status.isActive()).isTrue();
                } else {
                    assertThat(status.isActive()).isFalse();
                }
            }
        }

        @Test
        @DisplayName("모든 InteractionStatus 값이 고유하다")
        void allInteractionStatusValuesAreUnique() {
            InteractionStatus[] values = InteractionStatus.values();

            for (int i = 0; i < values.length; i++) {
                for (int j = i + 1; j < values.length; j++) {
                    assertThat(values[i]).isNotEqualTo(values[j]);
                    assertThat(values[i].name()).isNotEqualTo(values[j].name());
                    assertThat(values[i].ordinal()).isNotEqualTo(values[j].ordinal());
                }
            }
        }

        @Test
        @DisplayName("InteractionStatus enum은 2개의 고정된 상태를 가진다")
        void interactionStatusHasTwoFixedStates() {
            InteractionStatus[] values = InteractionStatus.values();

            assertThat(values).hasSize(2);
            assertThat(values).contains(
                    InteractionStatus.ACTIVE,
                    InteractionStatus.INACTIVE
            );
        }
    }

    @Nested
    @DisplayName("InteractionStatus 상태 전환 시나리오 테스트")
    class InteractionStatusTransitionTest {

        @Test
        @DisplayName("모든 InteractionStatus 값을 순회할 수 있다")
        void canIterateOverAllInteractionStatusValues() {
            int count = 0;
            for (InteractionStatus status : InteractionStatus.values()) {
                assertThat(status).isNotNull();
                count++;
            }
            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("InteractionStatus switch 문에서 모든 케이스를 처리할 수 있다")
        void canHandleAllCasesInSwitchStatement() {
            for (InteractionStatus status : InteractionStatus.values()) {
                String result = switch (status) {
                    case ACTIVE -> "활성";
                    case INACTIVE -> "비활성";
                };
                assertThat(result).isNotNull();
                assertThat(result).isNotEmpty();
            }
        }

        @Test
        @DisplayName("InteractionStatus를 문자열로 변환하고 다시 enum으로 변환할 수 있다")
        void canConvertToStringAndBackToEnum() {
            for (InteractionStatus originalStatus : InteractionStatus.values()) {
                String statusString = originalStatus.toString();
                InteractionStatus convertedStatus = InteractionStatus.valueOf(statusString);
                
                assertThat(convertedStatus).isEqualTo(originalStatus);
            }
        }
    }
}