package com.backend.immilog.comment.domain.model;

import com.backend.immilog.comment.exception.CommentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReferenceTypeTest {

    @Test
    @DisplayName("문자열로 ReferenceType 조회 - COMMENT")
    void getByStringComment() {
        //given
        String referenceType = "COMMENT";

        //when
        ReferenceType result = ReferenceType.getByString(referenceType);

        //then
        assertThat(result).isEqualTo(ReferenceType.COMMENT);
    }

    @Test
    @DisplayName("문자열로 ReferenceType 조회 - POST")
    void getByStringPost() {
        //given
        String referenceType = "POST";

        //when
        ReferenceType result = ReferenceType.getByString(referenceType);

        //then
        assertThat(result).isEqualTo(ReferenceType.POST);
    }


    @Test
    @DisplayName("문자열로 ReferenceType 조회 - 소문자")
    void getByStringLowerCase() {
        //given
        String referenceType = "comment";

        //when
        ReferenceType result = ReferenceType.getByString(referenceType);

        //then
        assertThat(result).isEqualTo(ReferenceType.COMMENT);
    }

    @Test
    @DisplayName("문자열로 ReferenceType 조회 - 대소문자 혼합")
    void getByStringMixedCase() {
        //given
        String referenceType = "PoSt";

        //when
        ReferenceType result = ReferenceType.getByString(referenceType);

        //then
        assertThat(result).isEqualTo(ReferenceType.POST);
    }

    @Test
    @DisplayName("문자열로 ReferenceType 조회 실패 - 존재하지 않는 타입")
    void getByStringFailWithInvalidType() {
        //given
        String referenceType = "INVALID_TYPE";

        //when & then
        assertThatThrownBy(() -> ReferenceType.getByString(referenceType))
                .isInstanceOf(CommentException.class);
    }

    @Test
    @DisplayName("문자열로 ReferenceType 조회 실패 - null 입력")
    void getByStringFailWithNull() {
        //given
        String referenceType = null;

        //when & then
        assertThatThrownBy(() -> ReferenceType.getByString(referenceType))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("문자열로 ReferenceType 조회 실패 - 빈 문자열")
    void getByStringFailWithEmptyString() {
        //given
        String referenceType = "";

        //when & then
        assertThatThrownBy(() -> ReferenceType.getByString(referenceType))
                .isInstanceOf(CommentException.class);
    }

    @Test
    @DisplayName("모든 ReferenceType 값들 검증")
    void verifyAllReferenceTypeValues() {
        //given
        ReferenceType[] expectedValues = {
                ReferenceType.COMMENT,
                ReferenceType.POST
        };

        //when
        ReferenceType[] actualValues = ReferenceType.values();

        //then
        assertThat(actualValues).containsExactly(expectedValues);
    }

    @Test
    @DisplayName("ReferenceType name() 메서드 검증")
    void verifyReferenceTypeNames() {
        //when & then
        assertThat(ReferenceType.COMMENT.name()).isEqualTo("COMMENT");
        assertThat(ReferenceType.POST.name()).isEqualTo("POST");
    }

    @Test
    @DisplayName("모든 유효한 문자열로 ReferenceType 조회")
    void getByStringForAllValidTypes() {
        //given & when & then
        for (ReferenceType type : ReferenceType.values()) {
            ReferenceType result = ReferenceType.getByString(type.name());
            assertThat(result).isEqualTo(type);
        }
    }

    @Test
    @DisplayName("문자열로 ReferenceType 조회 - 공백 포함")
    void getByStringFailWithWhitespace() {
        //given
        String referenceType = " COMMENT ";

        //when & then
        assertThatThrownBy(() -> ReferenceType.getByString(referenceType))
                .isInstanceOf(CommentException.class);
    }

    @Test
    @DisplayName("문자열로 ReferenceType 조회 - 특수문자 포함")
    void getByStringFailWithSpecialCharacters() {
        //given
        String referenceType = "COMMENT@#$";

        //when & then
        assertThatThrownBy(() -> ReferenceType.getByString(referenceType)).isInstanceOf(CommentException.class);
    }

    @Test
    @DisplayName("ReferenceType enum 순서 검증")
    void verifyReferenceTypeOrder() {
        //given
        ReferenceType[] values = ReferenceType.values();

        //when & then
        assertThat(values[0]).isEqualTo(ReferenceType.COMMENT);
        assertThat(values[1]).isEqualTo(ReferenceType.POST);
    }
}