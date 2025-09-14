package com.backend.immilog.interaction.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InteractionTypeTest {

    @Nested
    @DisplayName("InteractionType 기본 속성 테스트")
    class InteractionTypeBasicPropertiesTest {

        @Test
        @DisplayName("LIKE 상수값이 올바르게 정의되어 있다")
        void likeConstantIsCorrectlyDefined() {
            assertThat(InteractionType.LIKE).isNotNull();
            assertThat(InteractionType.LIKE.name()).isEqualTo("LIKE");
        }

        @Test
        @DisplayName("BOOKMARK 상수값이 올바르게 정의되어 있다")
        void bookmarkConstantIsCorrectlyDefined() {
            assertThat(InteractionType.BOOKMARK).isNotNull();
            assertThat(InteractionType.BOOKMARK.name()).isEqualTo("BOOKMARK");
        }
    }

    @Nested
    @DisplayName("InteractionType enum 기본 동작 테스트")
    class InteractionTypeEnumBehaviorTest {

        @Test
        @DisplayName("InteractionType enum의 모든 값을 검증한다")
        void allInteractionTypeValues() {
            InteractionType[] values = InteractionType.values();

            assertThat(values).hasSize(2);
            assertThat(values).containsExactlyInAnyOrder(
                    InteractionType.LIKE,
                    InteractionType.BOOKMARK
            );
        }

        @Test
        @DisplayName("valueOf 메서드로 문자열을 통해 enum 값을 가져올 수 있다")
        void valueOfMethod() {
            assertThat(InteractionType.valueOf("LIKE")).isEqualTo(InteractionType.LIKE);
            assertThat(InteractionType.valueOf("BOOKMARK")).isEqualTo(InteractionType.BOOKMARK);
        }

        @Test
        @DisplayName("enum 값의 순서가 올바르다")
        void enumOrderIsCorrect() {
            InteractionType[] values = InteractionType.values();

            assertThat(values[0]).isEqualTo(InteractionType.LIKE);
            assertThat(values[1]).isEqualTo(InteractionType.BOOKMARK);
        }

        @Test
        @DisplayName("enum 값의 ordinal이 올바르다")
        void ordinalValues() {
            assertThat(InteractionType.LIKE.ordinal()).isEqualTo(0);
            assertThat(InteractionType.BOOKMARK.ordinal()).isEqualTo(1);
        }

        @Test
        @DisplayName("enum 값의 name이 올바르다")
        void enumNames() {
            assertThat(InteractionType.LIKE.name()).isEqualTo("LIKE");
            assertThat(InteractionType.BOOKMARK.name()).isEqualTo("BOOKMARK");
        }
    }

    @Nested
    @DisplayName("InteractionType isLike 메서드 테스트")
    class InteractionTypeIsLikeTest {

        @Test
        @DisplayName("LIKE는 좋아요 타입이다")
        void likeIsLikeType() {
            boolean result = InteractionType.LIKE.isLike();

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("BOOKMARK는 좋아요 타입이 아니다")
        void bookmarkIsNotLikeType() {
            boolean result = InteractionType.BOOKMARK.isLike();

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("InteractionType 동등성 테스트")
    class InteractionTypeEqualityTest {

        @Test
        @DisplayName("같은 InteractionType enum 값은 동등하다")
        void sameInteractionTypeValuesAreEqual() {
            InteractionType type1 = InteractionType.LIKE;
            InteractionType type2 = InteractionType.LIKE;

            assertThat(type1).isEqualTo(type2);
            assertThat(type1 == type2).isTrue();
            assertThat(type1.hashCode()).isEqualTo(type2.hashCode());
        }

        @Test
        @DisplayName("다른 InteractionType enum 값은 동등하지 않다")
        void differentInteractionTypeValuesAreNotEqual() {
            InteractionType type1 = InteractionType.LIKE;
            InteractionType type2 = InteractionType.BOOKMARK;

            assertThat(type1).isNotEqualTo(type2);
            assertThat(type1 == type2).isFalse();
        }

        @Test
        @DisplayName("equals 메서드를 통한 비교가 올바르게 동작한다")
        void equalsMethodWorksCorrectly() {
            InteractionType likeType = InteractionType.LIKE;

            assertThat(likeType.equals(InteractionType.LIKE)).isTrue();
            assertThat(likeType.equals(InteractionType.BOOKMARK)).isFalse();
            assertThat(likeType.equals(null)).isFalse();
            assertThat(likeType.equals("LIKE")).isFalse();
        }
    }

    @Nested
    @DisplayName("InteractionType toString 테스트")
    class InteractionTypeToStringTest {

        @Test
        @DisplayName("toString 메서드가 올바르게 동작한다")
        void toStringWorksCorrectly() {
            assertThat(InteractionType.LIKE.toString()).isEqualTo("LIKE");
            assertThat(InteractionType.BOOKMARK.toString()).isEqualTo("BOOKMARK");
        }
    }

    @Nested
    @DisplayName("InteractionType 비즈니스 로직 테스트")
    class InteractionTypeBusinessLogicTest {

        @Test
        @DisplayName("모든 InteractionType 값이 고유하다")
        void allInteractionTypeValuesAreUnique() {
            InteractionType[] values = InteractionType.values();

            for (int i = 0; i < values.length; i++) {
                for (int j = i + 1; j < values.length; j++) {
                    assertThat(values[i]).isNotEqualTo(values[j]);
                    assertThat(values[i].name()).isNotEqualTo(values[j].name());
                    assertThat(values[i].ordinal()).isNotEqualTo(values[j].ordinal());
                }
            }
        }

        @Test
        @DisplayName("InteractionType enum은 2개의 고정된 타입을 가진다")
        void interactionTypeHasTwoFixedTypes() {
            InteractionType[] values = InteractionType.values();

            assertThat(values).hasSize(2);
            assertThat(values).contains(
                    InteractionType.LIKE,
                    InteractionType.BOOKMARK
            );
        }

        @Test
        @DisplayName("좋아요 타입 판별 로직이 정확하다")
        void likeTypeDetectionLogic() {
            for (InteractionType type : InteractionType.values()) {
                if (type == InteractionType.LIKE) {
                    assertThat(type.isLike()).isTrue();
                } else {
                    assertThat(type.isLike()).isFalse();
                }
            }
        }
    }

    @Nested
    @DisplayName("InteractionType 상수 전환 테스트")
    class InteractionTypeTransitionTest {

        @Test
        @DisplayName("모든 InteractionType 값을 순회할 수 있다")
        void canIterateOverAllInteractionTypeValues() {
            int count = 0;
            for (InteractionType type : InteractionType.values()) {
                assertThat(type).isNotNull();
                count++;
            }
            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("InteractionType switch 문에서 모든 케이스를 처리할 수 있다")
        void canHandleAllCasesInSwitchStatement() {
            for (InteractionType type : InteractionType.values()) {
                String result = switch (type) {
                    case LIKE -> "좋아요";
                    case BOOKMARK -> "북마크";
                };
                assertThat(result).isNotNull();
                assertThat(result).isNotEmpty();
            }
        }

        @Test
        @DisplayName("InteractionType를 문자열로 변환하고 다시 enum으로 변환할 수 있다")
        void canConvertToStringAndBackToEnum() {
            for (InteractionType originalType : InteractionType.values()) {
                String typeString = originalType.toString();
                InteractionType convertedType = InteractionType.valueOf(typeString);
                
                assertThat(convertedType).isEqualTo(originalType);
            }
        }
    }
}