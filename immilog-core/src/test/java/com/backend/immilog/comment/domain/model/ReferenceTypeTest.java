package com.backend.immilog.comment.domain.model;

import com.backend.immilog.comment.exception.CommentErrorCode;
import com.backend.immilog.comment.exception.CommentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ReferenceType Enum")
class ReferenceTypeTest {

    @Nested
    @DisplayName("문자열로부터 변환")
    class GetByString {

        @Test
        @DisplayName("COMMENT 타입 변환 성공")
        void getCommentType() {
            var result = ReferenceType.getByString("COMMENT");

            assertThat(result).isEqualTo(ReferenceType.COMMENT);
        }

        @Test
        @DisplayName("POST 타입 변환 성공")
        void getPostType() {
            var result = ReferenceType.getByString("POST");

            assertThat(result).isEqualTo(ReferenceType.POST);
        }

        @Test
        @DisplayName("소문자로 변환 성공")
        void getLowercase() {
            var result1 = ReferenceType.getByString("comment");
            var result2 = ReferenceType.getByString("post");

            assertThat(result1).isEqualTo(ReferenceType.COMMENT);
            assertThat(result2).isEqualTo(ReferenceType.POST);
        }

        @Test
        @DisplayName("대소문자 혼합으로 변환 성공")
        void getMixedCase() {
            var result1 = ReferenceType.getByString("Comment");
            var result2 = ReferenceType.getByString("Post");

            assertThat(result1).isEqualTo(ReferenceType.COMMENT);
            assertThat(result2).isEqualTo(ReferenceType.POST);
        }

        @Test
        @DisplayName("존재하지 않는 타입으로 예외 발생")
        void getInvalidType() {
            assertThatThrownBy(() -> ReferenceType.getByString("INVALID"))
                    .isInstanceOf(CommentException.class)
                    .hasFieldOrPropertyWithValue("errorCode", CommentErrorCode.INVALID_REFERENCE_TYPE);
        }

        @Test
        @DisplayName("null 입력으로 예외 발생")
        void getNullType() {
            assertThatThrownBy(() -> ReferenceType.getByString(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("빈 문자열로 예외 발생")
        void getEmptyString() {
            assertThatThrownBy(() -> ReferenceType.getByString(""))
                    .isInstanceOf(CommentException.class)
                    .hasFieldOrPropertyWithValue("errorCode", CommentErrorCode.INVALID_REFERENCE_TYPE);
        }
    }

    @Nested
    @DisplayName("Enum 기본 기능")
    class EnumBasics {

        @Test
        @DisplayName("모든 타입 확인")
        void allTypes() {
            var values = ReferenceType.values();

            assertThat(values).hasSize(2);
            assertThat(values).containsExactly(ReferenceType.COMMENT, ReferenceType.POST);
        }

        @Test
        @DisplayName("valueOf 메서드")
        void valueOfMethod() {
            assertThat(ReferenceType.valueOf("COMMENT")).isEqualTo(ReferenceType.COMMENT);
            assertThat(ReferenceType.valueOf("POST")).isEqualTo(ReferenceType.POST);
        }
    }
}