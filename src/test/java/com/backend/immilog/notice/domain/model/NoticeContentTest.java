package com.backend.immilog.notice.domain.model;

import com.backend.immilog.notice.exception.NoticeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class NoticeContentTest {

    @Test
    @DisplayName("NoticeContent 생성 - 정상 케이스")
    void createNoticeContentSuccessfully() {
        //given
        String value = "공지사항 내용입니다.";

        //when
        NoticeContent content = NoticeContent.of(value);

        //then
        assertThat(content.value()).isEqualTo(value);
        assertThat(content.isEmpty()).isFalse();
        assertThat(content.length()).isEqualTo(value.length());
    }

    @Test
    @DisplayName("NoticeContent 생성 - 공백 제거")
    void createNoticeContentWithTrimming() {
        //given
        String value = "  공지사항 내용입니다.  ";
        String expectedValue = "공지사항 내용입니다.";

        //when
        NoticeContent content = NoticeContent.of(value);

        //then
        assertThat(content.value()).isEqualTo(expectedValue);
        assertThat(content.length()).isEqualTo(expectedValue.length());
    }

    @Test
    @DisplayName("NoticeContent 생성 실패 - null 값")
    void createNoticeContentFailWhenValueIsNull() {
        //given
        String value = null;

        //when & then
        assertThatThrownBy(() -> NoticeContent.of(value))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("NoticeContent 생성 실패 - 빈 문자열")
    void createNoticeContentFailWhenValueIsEmpty() {
        //given
        String value = "";

        //when & then
        assertThatThrownBy(() -> NoticeContent.of(value))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("NoticeContent 생성 실패 - 공백만 있는 문자열")
    void createNoticeContentFailWhenValueIsBlank() {
        //given
        String value = "   ";

        //when & then
        assertThatThrownBy(() -> NoticeContent.of(value))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("NoticeContent 생성 실패 - 최대 길이 초과")
    void createNoticeContentFailWhenValueTooLong() {
        //given
        String value = "a".repeat(5001);

        //when & then
        assertThatThrownBy(() -> NoticeContent.of(value))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("NoticeContent 생성 - 최대 길이 경계값")
    void createNoticeContentWithMaxLength() {
        //given
        String value = "a".repeat(5000);

        //when
        NoticeContent content = NoticeContent.of(value);

        //then
        assertThat(content.value()).hasSize(5000);
        assertThat(content.length()).isEqualTo(5000);
        assertThat(content.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("NoticeContent 생성 - 최소 길이")
    void createNoticeContentWithMinLength() {
        //given
        String value = "a";

        //when
        NoticeContent content = NoticeContent.of(value);

        //then
        assertThat(content.value()).isEqualTo(value);
        assertThat(content.length()).isEqualTo(1);
        assertThat(content.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("isEmpty 메서드 테스트 - 빈 내용")
    void isEmptyWithNullValue() {
        //given
        NoticeContent content = new NoticeContent(null);

        //when & then
        assertThat(content.isEmpty()).isTrue();
        assertThat(content.length()).isZero();
    }

    @Test
    @DisplayName("isEmpty 메서드 테스트 - 공백 내용")
    void isEmptyWithBlankValue() {
        //given
        NoticeContent content = new NoticeContent("   ");

        //when & then
        assertThat(content.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("길이 계산 테스트 - null 값")
    void lengthWithNullValue() {
        //given
        NoticeContent content = new NoticeContent(null);

        //when & then
        assertThat(content.length()).isZero();
    }

    @Test
    @DisplayName("한글 내용 생성")
    void createNoticeContentWithKorean() {
        //given
        String value = "한글로 작성된 공지사항 내용입니다. 여러 줄에 걸쳐 작성할 수 있습니다.";

        //when
        NoticeContent content = NoticeContent.of(value);

        //then
        assertThat(content.value()).isEqualTo(value);
        assertThat(content.length()).isEqualTo(value.length());
    }

    @Test
    @DisplayName("영문 내용 생성")
    void createNoticeContentWithEnglish() {
        //given
        String value = "This is an English notice content. It can be written in multiple lines.";

        //when
        NoticeContent content = NoticeContent.of(value);

        //then
        assertThat(content.value()).isEqualTo(value);
        assertThat(content.length()).isEqualTo(value.length());
    }

    @Test
    @DisplayName("HTML 태그가 포함된 내용 생성")
    void createNoticeContentWithHtmlTags() {
        //given
        String value = "<p>HTML 태그가 포함된 <strong>공지사항</strong> 내용입니다.</p>";

        //when
        NoticeContent content = NoticeContent.of(value);

        //then
        assertThat(content.value()).isEqualTo(value);
        assertThat(content.value()).contains("<p>");
        assertThat(content.value()).contains("<strong>");
    }

    @Test
    @DisplayName("줄바꿈이 포함된 내용 생성")
    void createNoticeContentWithNewlines() {
        //given
        String value = "첫 번째 줄입니다.\n두 번째 줄입니다.\n세 번째 줄입니다.";

        //when
        NoticeContent content = NoticeContent.of(value);

        //then
        assertThat(content.value()).isEqualTo(value);
        assertThat(content.value()).contains("\n");
    }

    @Test
    @DisplayName("특수문자가 포함된 내용 생성")
    void createNoticeContentWithSpecialCharacters() {
        //given
        String value = "특수문자가 포함된 내용: !@#$%^&*()_+-=[]{}|;':\",./<>?";

        //when
        NoticeContent content = NoticeContent.of(value);

        //then
        assertThat(content.value()).isEqualTo(value);
        assertThat(content.length()).isEqualTo(value.length());
    }

    @Test
    @DisplayName("긴 내용 생성")
    void createNoticeContentWithLongText() {
        //given
        String value = "이것은 매우 긴 공지사항 내용입니다.".repeat(100);

        //when
        NoticeContent content = NoticeContent.of(value);

        //then
        assertThat(content.length()).isEqualTo(value.length());
    }

    @Test
    @DisplayName("NoticeContent 동등성 테스트")
    void equalityTest() {
        //given
        String value = "동일한 내용";
        NoticeContent content1 = NoticeContent.of(value);
        NoticeContent content2 = NoticeContent.of(value);
        NoticeContent content3 = NoticeContent.of("다른 내용");

        //when & then
        assertThat(content1).isEqualTo(content2);
        assertThat(content1).isNotEqualTo(content3);
        assertThat(content1.hashCode()).isEqualTo(content2.hashCode());
    }

    @Test
    @DisplayName("공백과 탭이 포함된 내용 처리")
    void createNoticeContentWithSpacesAndTabs() {
        //given
        String value = "공백과\t탭이\t포함된 내용입니다.";

        //when
        NoticeContent content = NoticeContent.of(value);

        //then
        assertThat(content.value()).isEqualTo(value);
        assertThat(content.value()).contains("\t");
    }

    @Test
    @DisplayName("유니코드 문자가 포함된 내용 생성")
    void createNoticeContentWithUnicodeCharacters() {
        //given
        String value = "유니코드 문자: 😀 🎉 ❤️ ★ ♠ ♥ ♦ ♣";

        //when
        NoticeContent content = NoticeContent.of(value);

        //then
        assertThat(content.value()).isEqualTo(value);
        assertThat(content.value()).contains("😀");
        assertThat(content.value()).contains("🎉");
    }

    @Test
    @DisplayName("JSON 형태의 내용 생성")
    void createNoticeContentWithJsonFormat() {
        //given
        String value = "{\"title\": \"공지사항\", \"content\": \"내용\", \"author\": \"관리자\"}";

        //when
        NoticeContent content = NoticeContent.of(value);

        //then
        assertThat(content.value()).isEqualTo(value);
        assertThat(content.value()).contains("{");
        assertThat(content.value()).contains("}");
    }
}