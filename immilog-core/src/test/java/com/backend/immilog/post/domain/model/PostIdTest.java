package com.backend.immilog.post.domain.model;

import com.backend.immilog.post.domain.model.post.PostId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PostId Value Object")
class PostIdTest {

    @Test
    @DisplayName("valid string으로 PostId 생성 성공")
    void createPostIdWithValidString() {
        String validId = "test-post-id-123";
        
        PostId postId = PostId.of(validId);
        
        assertThat(postId.value()).isEqualTo(validId);
    }

    @Test
    @DisplayName("generate로 새로운 PostId 생성 성공")
    void generateNewPostId() {
        PostId postId = PostId.generate();
        
        assertThat(postId.value()).isNotNull();
        assertThat(postId.value()).isNotBlank();
        assertThat(postId.value().length()).isGreaterThan(0);
    }

    @Test
    @DisplayName("각 generate 호출마다 다른 ID 생성")
    void generateUniqueIds() {
        PostId postId1 = PostId.generate();
        PostId postId2 = PostId.generate();
        
        assertThat(postId1.value()).isNotEqualTo(postId2.value());
    }

    @Test
    @DisplayName("null value로 PostId 생성 시 예외 발생")
    void createPostIdWithNull() {
        assertThatThrownBy(() -> PostId.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("PostId cannot be null or blank");
    }

    @Test
    @DisplayName("blank value로 PostId 생성 시 예외 발생")
    void createPostIdWithBlank() {
        assertThatThrownBy(() -> PostId.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("PostId cannot be null or blank");

        assertThatThrownBy(() -> PostId.of("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("PostId cannot be null or blank");
    }

    @Test
    @DisplayName("255자를 초과하는 value로 PostId 생성 시 예외 발생")
    void createPostIdWithTooLongValue() {
        String tooLongId = "a".repeat(256);
        
        assertThatThrownBy(() -> PostId.of(tooLongId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("PostId cannot exceed 255 characters");
    }

    @Test
    @DisplayName("같은 값을 가진 PostId는 동등함")
    void equalityWithSameValue() {
        String id = "same-post-id";
        PostId postId1 = PostId.of(id);
        PostId postId2 = PostId.of(id);
        
        assertThat(postId1).isEqualTo(postId2);
        assertThat(postId1.hashCode()).isEqualTo(postId2.hashCode());
    }

    @Test
    @DisplayName("다른 값을 가진 PostId는 다름")
    void inequalityWithDifferentValues() {
        PostId postId1 = PostId.of("post-id-1");
        PostId postId2 = PostId.of("post-id-2");
        
        assertThat(postId1).isNotEqualTo(postId2);
    }

    @Test
    @DisplayName("toString은 value를 반환")
    void toStringReturnsValue() {
        String id = "test-post-id";
        PostId postId = PostId.of(id);
        
        assertThat(postId.toString()).isEqualTo(id);
    }
}