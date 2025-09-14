package com.backend.immilog.post.domain.model;

import com.backend.immilog.post.domain.model.post.CommentCount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CommentCount Value Object")
class CommentCountTest {

    @Test
    @DisplayName("valid value로 CommentCount 생성 성공")
    void createCommentCountWithValidValue() {
        CommentCount commentCount = CommentCount.of(5L);
        
        assertThat(commentCount.value()).isEqualTo(5L);
    }

    @Test
    @DisplayName("zero static method로 0 CommentCount 생성")
    void createZeroCommentCount() {
        CommentCount commentCount = CommentCount.zero();
        
        assertThat(commentCount.value()).isEqualTo(0L);
    }

    @Test
    @DisplayName("null value로 CommentCount 생성 시 예외 발생")
    void createCommentCountWithNull() {
        assertThatThrownBy(() -> CommentCount.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Comment count cannot be null");
    }

    @Test
    @DisplayName("음수 value로 CommentCount 생성 시 예외 발생")
    void createCommentCountWithNegativeValue() {
        assertThatThrownBy(() -> CommentCount.of(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Comment count cannot be negative");
    }

    @Test
    @DisplayName("increment는 1 증가된 새 CommentCount 반환")
    void incrementReturnsNewCommentCount() {
        CommentCount original = CommentCount.of(5L);
        
        CommentCount incremented = original.increment();
        
        assertThat(incremented.value()).isEqualTo(6L);
        assertThat(original.value()).isEqualTo(5L);
        assertThat(incremented).isNotSameAs(original);
    }

    @Test
    @DisplayName("0에서 increment 시 1이 됨")
    void incrementFromZero() {
        CommentCount zero = CommentCount.zero();
        
        CommentCount incremented = zero.increment();
        
        assertThat(incremented.value()).isEqualTo(1L);
    }

    @Test
    @DisplayName("decrement는 1 감소된 새 CommentCount 반환")
    void decrementReturnsNewCommentCount() {
        CommentCount original = CommentCount.of(5L);
        
        CommentCount decremented = original.decrement();
        
        assertThat(decremented.value()).isEqualTo(4L);
        assertThat(original.value()).isEqualTo(5L);
        assertThat(decremented).isNotSameAs(original);
    }

    @Test
    @DisplayName("0에서 decrement 시 0 유지")
    void decrementFromZeroStaysZero() {
        CommentCount zero = CommentCount.zero();
        
        CommentCount decremented = zero.decrement();
        
        assertThat(decremented.value()).isEqualTo(0L);
    }

    @Test
    @DisplayName("1에서 decrement 시 0이 됨")
    void decrementFromOne() {
        CommentCount one = CommentCount.of(1L);
        
        CommentCount decremented = one.decrement();
        
        assertThat(decremented.value()).isEqualTo(0L);
    }

    @Test
    @DisplayName("isEmpty는 value가 0일 때 true 반환")
    void isEmptyWhenZero() {
        assertThat(CommentCount.zero().isEmpty()).isTrue();
        assertThat(CommentCount.of(0L).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("isEmpty는 value가 0이 아닐 때 false 반환")
    void isEmptyWhenNotZero() {
        assertThat(CommentCount.of(1L).isEmpty()).isFalse();
        assertThat(CommentCount.of(10L).isEmpty()).isFalse();
    }

    @Test
    @DisplayName("hasComments는 value가 0보다 클 때 true 반환")
    void hasCommentsWhenGreaterThanZero() {
        assertThat(CommentCount.of(1L).hasComments()).isTrue();
        assertThat(CommentCount.of(10L).hasComments()).isTrue();
    }

    @Test
    @DisplayName("hasComments는 value가 0일 때 false 반환")
    void hasCommentsWhenZero() {
        assertThat(CommentCount.zero().hasComments()).isFalse();
        assertThat(CommentCount.of(0L).hasComments()).isFalse();
    }

    @Test
    @DisplayName("toString은 value를 문자열로 반환")
    void toStringReturnsValueAsString() {
        assertThat(CommentCount.of(42L).toString()).isEqualTo("42");
        assertThat(CommentCount.zero().toString()).isEqualTo("0");
    }

    @Test
    @DisplayName("같은 값을 가진 CommentCount는 동등함")
    void equalityWithSameValue() {
        CommentCount count1 = CommentCount.of(5L);
        CommentCount count2 = CommentCount.of(5L);
        
        assertThat(count1).isEqualTo(count2);
        assertThat(count1.hashCode()).isEqualTo(count2.hashCode());
    }

    @Test
    @DisplayName("다른 값을 가진 CommentCount는 다름")
    void inequalityWithDifferentValues() {
        CommentCount count1 = CommentCount.of(5L);
        CommentCount count2 = CommentCount.of(10L);
        
        assertThat(count1).isNotEqualTo(count2);
    }
}