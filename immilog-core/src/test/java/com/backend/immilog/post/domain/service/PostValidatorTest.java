package com.backend.immilog.post.domain.service;

import com.backend.immilog.post.domain.model.post.*;
import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PostValidator")
class PostValidatorTest {

    private PostValidator postValidator;
    private Post testPost;

    @BeforeEach
    void setUp() {
        postValidator = new PostValidator();
        testPost = createTestPost("user123");
    }

    @Nested
    @DisplayName("게시물 접근 권한 검증")
    class ValidatePostAccess {

        @Test
        @DisplayName("올바른 사용자 ID로 접근 시 성공")
        void validatePostAccessSuccess() {
            assertThatNoException()
                    .isThrownBy(() -> postValidator.validatePostAccess(testPost, "user123"));
        }

        @Test
        @DisplayName("다른 사용자 ID로 접근 시 예외 발생")
        void validatePostAccessWithDifferentUser() {
            assertThatThrownBy(() -> postValidator.validatePostAccess(testPost, "user456"))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.NO_AUTHORITY);
        }

        @Test
        @DisplayName("null 사용자 ID로 접근 시 예외 발생")
        void validatePostAccessWithNullUser() {
            assertThatThrownBy(() -> postValidator.validatePostAccess(testPost, null))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.NO_AUTHORITY);
        }

        @Test
        @DisplayName("빈 사용자 ID로 접근 시 예외 발생")
        void validatePostAccessWithEmptyUser() {
            assertThatThrownBy(() -> postValidator.validatePostAccess(testPost, ""))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.NO_AUTHORITY);
        }
    }

    @Nested
    @DisplayName("게시물 업데이트 검증")
    class ValidatePostUpdate {

        @Test
        @DisplayName("제목과 내용이 있을 때 성공")
        void validatePostUpdateWithTitleAndContent() {
            assertThatNoException()
                    .isThrownBy(() -> postValidator.validatePostUpdate(testPost, "New Title", "New Content"));
        }

        @Test
        @DisplayName("제목만 있을 때 성공")
        void validatePostUpdateWithTitleOnly() {
            assertThatNoException()
                    .isThrownBy(() -> postValidator.validatePostUpdate(testPost, "New Title", null));
        }

        @Test
        @DisplayName("내용만 있을 때 성공")
        void validatePostUpdateWithContentOnly() {
            assertThatNoException()
                    .isThrownBy(() -> postValidator.validatePostUpdate(testPost, null, "New Content"));
        }

        @Test
        @DisplayName("제목과 내용이 모두 null일 때 예외 발생")
        void validatePostUpdateWithBothNull() {
            assertThatThrownBy(() -> postValidator.validatePostUpdate(testPost, null, null))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.INVALID_POST_DATA);
        }

        @Test
        @DisplayName("빈 제목과 빈 내용일 때도 성공")
        void validatePostUpdateWithEmptyStrings() {
            assertThatNoException()
                    .isThrownBy(() -> postValidator.validatePostUpdate(testPost, "", ""));
        }
    }

    @Nested
    @DisplayName("공개 상태 검증")
    class ValidatePostPublicStatus {

        @Test
        @DisplayName("true 공개 상태 검증 성공")
        void validatePublicStatusTrue() {
            assertThatNoException()
                    .isThrownBy(() -> postValidator.validatePostPublicStatus(true));
        }

        @Test
        @DisplayName("false 공개 상태 검증 성공")
        void validatePublicStatusFalse() {
            assertThatNoException()
                    .isThrownBy(() -> postValidator.validatePostPublicStatus(false));
        }

        @Test
        @DisplayName("null 공개 상태 검증 시 예외 발생")
        void validatePublicStatusWithNull() {
            assertThatThrownBy(() -> postValidator.validatePostPublicStatus(null))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.INVALID_PUBLIC_STATUS);
        }
    }

    private Post createTestPost(String userId) {
        return new Post(
                PostId.generate(),
                new PostUserInfo(userId),
                PostInfo.of("Test Title", "Test Content", "US", "CA"),
                Categories.QNA,
                PublicStatus.PUBLIC,
                null,
                CommentCount.zero(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}