package com.backend.immilog.notice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class NoticeIdTest {

    @Test
    @DisplayName("NoticeId 생성 - 정상 케이스")
    void createNoticeIdSuccessfully() {
        //given
        String value = "noticeId123";

        //when
        NoticeId noticeId = NoticeId.of(value);

        //then
        assertThat(noticeId.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("NoticeId 생성 실패 - null 값")
    void createNoticeIdFailWhenValueIsNull() {
        //given
        String value = null;

        //when & then
        assertThatThrownBy(() -> NoticeId.of(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("NoticeId value must be not null or empty");
    }

    @Test
    @DisplayName("NoticeId 생성 실패 - 빈 문자열")
    void createNoticeIdFailWhenValueIsEmpty() {
        //given
        String value = "";

        //when & then
        assertThatThrownBy(() -> NoticeId.of(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("NoticeId value must be not null or empty");
    }

    @Test
    @DisplayName("NoticeId 생성 실패 - 공백 문자열")
    void createNoticeIdFailWhenValueIsBlank() {
        //given
        String value = "   ";

        //when & then
        assertThatThrownBy(() -> NoticeId.of(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("NoticeId value must be not null or empty");
    }

    @Test
    @DisplayName("NoticeId 생성 - generate 메서드")
    void generateNoticeId() {
        //when
        NoticeId noticeId = NoticeId.generate();

        //then
        assertThat(noticeId.value()).isNull();
    }

    @Test
    @DisplayName("NoticeId 동등성 테스트")
    void equalityTest() {
        //given
        String value = "noticeId123";
        NoticeId noticeId1 = NoticeId.of(value);
        NoticeId noticeId2 = NoticeId.of(value);
        NoticeId noticeId3 = NoticeId.of("differentId");

        //when & then
        assertThat(noticeId1).isEqualTo(noticeId2);
        assertThat(noticeId1).isNotEqualTo(noticeId3);
        assertThat(noticeId1.hashCode()).isEqualTo(noticeId2.hashCode());
    }

    @Test
    @DisplayName("다양한 값으로 NoticeId 생성")
    void createNoticeIdWithVariousValues() {
        //given
        String[] values = {"notice1", "NOTICE_2", "notice-3", "notice_id_with_underscores", "123456"};

        //when & then
        for (String value : values) {
            NoticeId noticeId = NoticeId.of(value);
            assertThat(noticeId.value()).isEqualTo(value);
        }
    }

    @Test
    @DisplayName("긴 값으로 NoticeId 생성")
    void createNoticeIdWithLongValue() {
        //given
        String longValue = "a".repeat(1000);

        //when
        NoticeId noticeId = NoticeId.of(longValue);

        //then
        assertThat(noticeId.value()).isEqualTo(longValue);
        assertThat(noticeId.value()).hasSize(1000);
    }

    @Test
    @DisplayName("특수 문자가 포함된 값으로 NoticeId 생성")
    void createNoticeIdWithSpecialCharacters() {
        //given
        String value = "notice@#$%^&*()_+";

        //when
        NoticeId noticeId = NoticeId.of(value);

        //then
        assertThat(noticeId.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("한글이 포함된 값으로 NoticeId 생성")
    void createNoticeIdWithKoreanCharacters() {
        //given
        String value = "공지사항123";

        //when
        NoticeId noticeId = NoticeId.of(value);

        //then
        assertThat(noticeId.value()).isEqualTo(value);
    }
}