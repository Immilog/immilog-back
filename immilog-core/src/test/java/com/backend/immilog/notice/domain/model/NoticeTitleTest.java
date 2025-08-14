package com.backend.immilog.notice.domain.model;

import com.backend.immilog.notice.exception.NoticeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class NoticeTitleTest {

    @Test
    @DisplayName("NoticeTitle 생성 - 정상 케이스")
    void createNoticeTitleSuccessfully() {
        //given
        String value = "공지사항 제목";

        //when
        NoticeTitle title = NoticeTitle.of(value);

        //then
        assertThat(title.value()).isEqualTo(value);
        assertThat(title.isEmpty()).isFalse();
        assertThat(title.length()).isEqualTo(value.length());
    }

    @Test
    @DisplayName("NoticeTitle 생성 - 공백 제거")
    void createNoticeTitleWithTrimming() {
        //given
        String value = "  공지사항 제목  ";
        String expectedValue = "공지사항 제목";

        //when
        NoticeTitle title = NoticeTitle.of(value);

        //then
        assertThat(title.value()).isEqualTo(expectedValue);
        assertThat(title.length()).isEqualTo(expectedValue.length());
    }

    @Test
    @DisplayName("NoticeTitle 생성 실패 - null 값")
    void createNoticeTitleFailWhenValueIsNull() {
        //given
        String value = null;

        //when & then
        assertThatThrownBy(() -> NoticeTitle.of(value))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("NoticeTitle 생성 실패 - 빈 문자열")
    void createNoticeTitleFailWhenValueIsEmpty() {
        //given
        String value = "";

        //when & then
        assertThatThrownBy(() -> NoticeTitle.of(value))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("NoticeTitle 생성 실패 - 공백만 있는 문자열")
    void createNoticeTitleFailWhenValueIsBlank() {
        //given
        String value = "   ";

        //when & then
        assertThatThrownBy(() -> NoticeTitle.of(value))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("NoticeTitle 생성 실패 - 최대 길이 초과")
    void createNoticeTitleFailWhenValueTooLong() {
        //given
        String value = "a".repeat(201);

        //when & then
        assertThatThrownBy(() -> NoticeTitle.of(value))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("NoticeTitle 생성 - 최대 길이 경계값")
    void createNoticeTitleWithMaxLength() {
        //given
        String value = "a".repeat(200);

        //when
        NoticeTitle title = NoticeTitle.of(value);

        //then
        assertThat(title.value()).hasSize(200);
        assertThat(title.length()).isEqualTo(200);
        assertThat(title.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("NoticeTitle 생성 - 최소 길이")
    void createNoticeTitleWithMinLength() {
        //given
        String value = "a";

        //when
        NoticeTitle title = NoticeTitle.of(value);

        //then
        assertThat(title.value()).isEqualTo(value);
        assertThat(title.length()).isEqualTo(1);
        assertThat(title.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("isEmpty 메서드 테스트 - 빈 제목")
    void isEmptyWithNullValue() {
        //given
        NoticeTitle title = new NoticeTitle(null);

        //when & then
        assertThat(title.isEmpty()).isTrue();
        assertThat(title.length()).isZero();
    }

    @Test
    @DisplayName("isEmpty 메서드 테스트 - 공백 제목")
    void isEmptyWithBlankValue() {
        //given
        NoticeTitle title = new NoticeTitle("   ");

        //when & then
        assertThat(title.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("길이 계산 테스트 - null 값")
    void lengthWithNullValue() {
        //given
        NoticeTitle title = new NoticeTitle(null);

        //when & then
        assertThat(title.length()).isZero();
    }

    @Test
    @DisplayName("한글 제목 생성")
    void createNoticeTitleWithKorean() {
        //given
        String value = "한글 공지사항 제목입니다";

        //when
        NoticeTitle title = NoticeTitle.of(value);

        //then
        assertThat(title.value()).isEqualTo(value);
        assertThat(title.length()).isEqualTo(value.length());
    }

    @Test
    @DisplayName("영문 제목 생성")
    void createNoticeTitleWithEnglish() {
        //given
        String value = "English Notice Title";

        //when
        NoticeTitle title = NoticeTitle.of(value);

        //then
        assertThat(title.value()).isEqualTo(value);
        assertThat(title.length()).isEqualTo(value.length());
    }

    @Test
    @DisplayName("숫자가 포함된 제목 생성")
    void createNoticeTitleWithNumbers() {
        //given
        String value = "공지사항 제목 123번";

        //when
        NoticeTitle title = NoticeTitle.of(value);

        //then
        assertThat(title.value()).isEqualTo(value);
        assertThat(title.length()).isEqualTo(value.length());
    }

    @Test
    @DisplayName("특수문자가 포함된 제목 생성")
    void createNoticeTitleWithSpecialCharacters() {
        //given
        String value = "공지사항 제목!@#$%^&*()";

        //when
        NoticeTitle title = NoticeTitle.of(value);

        //then
        assertThat(title.value()).isEqualTo(value);
        assertThat(title.length()).isEqualTo(value.length());
    }

    @Test
    @DisplayName("NoticeTitle 동등성 테스트")
    void equalityTest() {
        //given
        String value = "동일한 제목";
        NoticeTitle title1 = NoticeTitle.of(value);
        NoticeTitle title2 = NoticeTitle.of(value);
        NoticeTitle title3 = NoticeTitle.of("다른 제목");

        //when & then
        assertThat(title1).isEqualTo(title2);
        assertThat(title1).isNotEqualTo(title3);
        assertThat(title1.hashCode()).isEqualTo(title2.hashCode());
    }

    @Test
    @DisplayName("공백이 포함된 제목 처리")
    void createNoticeTitleWithSpaces() {
        //given
        String value = "공지사항 제목에 공백이 포함됨";

        //when
        NoticeTitle title = NoticeTitle.of(value);

        //then
        assertThat(title.value()).isEqualTo(value);
        assertThat(title.value()).contains(" ");
    }

    @Test
    @DisplayName("줄바꿈이 포함된 제목 생성")
    void createNoticeTitleWithNewlines() {
        //given
        String value = "첫 번째 줄\n두 번째 줄";

        //when
        NoticeTitle title = NoticeTitle.of(value);

        //then
        assertThat(title.value()).isEqualTo(value);
        assertThat(title.value()).contains("\n");
    }
}