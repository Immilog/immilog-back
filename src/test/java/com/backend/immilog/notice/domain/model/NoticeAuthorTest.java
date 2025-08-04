package com.backend.immilog.notice.domain.model;

import com.backend.immilog.notice.exception.NoticeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class NoticeAuthorTest {

    @Test
    @DisplayName("NoticeAuthor 생성 - 정상 케이스")
    void createNoticeAuthorSuccessfully() {
        //given
        String userId = "authorUserId";

        //when
        NoticeAuthor author = NoticeAuthor.of(userId);

        //then
        assertThat(author.userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("NoticeAuthor 생성 실패 - null 사용자 ID")
    void createNoticeAuthorFailWhenUserIdIsNull() {
        //given
        String userId = null;

        //when & then
        assertThatThrownBy(() -> NoticeAuthor.of(userId))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("NoticeAuthor 생성 실패 - 빈 문자열 사용자 ID")
    void createNoticeAuthorFailWhenUserIdIsEmpty() {
        //given
        String userId = "";

        //when & then
        assertThatThrownBy(() -> NoticeAuthor.of(userId))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("NoticeAuthor 생성 실패 - 공백 문자열 사용자 ID")
    void createNoticeAuthorFailWhenUserIdIsBlank() {
        //given
        String userId = "   ";

        //when & then
        assertThatThrownBy(() -> NoticeAuthor.of(userId))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("NoticeAuthor 동등성 테스트")
    void equalityTest() {
        //given
        String userId = "authorUserId";
        NoticeAuthor author1 = NoticeAuthor.of(userId);
        NoticeAuthor author2 = NoticeAuthor.of(userId);
        NoticeAuthor author3 = NoticeAuthor.of("differentUserId");

        //when & then
        assertThat(author1).isEqualTo(author2);
        assertThat(author1).isNotEqualTo(author3);
        assertThat(author1.hashCode()).isEqualTo(author2.hashCode());
    }

    @Test
    @DisplayName("다양한 사용자 ID로 NoticeAuthor 생성")
    void createNoticeAuthorWithVariousUserIds() {
        //given
        String[] userIds = {"user1", "USER_2", "user-3", "user_id_with_underscores", "123456"};

        //when & then
        for (String userId : userIds) {
            NoticeAuthor author = NoticeAuthor.of(userId);
            assertThat(author.userId()).isEqualTo(userId);
        }
    }

    @Test
    @DisplayName("긴 사용자 ID로 NoticeAuthor 생성")
    void createNoticeAuthorWithLongUserId() {
        //given
        String longUserId = "user" + "a".repeat(100);

        //when
        NoticeAuthor author = NoticeAuthor.of(longUserId);

        //then
        assertThat(author.userId()).isEqualTo(longUserId);
        assertThat(author.userId()).hasSize(104);
    }

    @Test
    @DisplayName("특수 문자가 포함된 사용자 ID로 NoticeAuthor 생성")
    void createNoticeAuthorWithSpecialCharacters() {
        //given
        String userId = "user@domain.com";

        //when
        NoticeAuthor author = NoticeAuthor.of(userId);

        //then
        assertThat(author.userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("숫자로만 구성된 사용자 ID로 NoticeAuthor 생성")
    void createNoticeAuthorWithNumericUserId() {
        //given
        String userId = "123456789";

        //when
        NoticeAuthor author = NoticeAuthor.of(userId);

        //then
        assertThat(author.userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("UUID 형태의 사용자 ID로 NoticeAuthor 생성")
    void createNoticeAuthorWithUuidUserId() {
        //given
        String userId = "550e8400-e29b-41d4-a716-446655440000";

        //when
        NoticeAuthor author = NoticeAuthor.of(userId);

        //then
        assertThat(author.userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("한글이 포함된 사용자 ID로 NoticeAuthor 생성")
    void createNoticeAuthorWithKoreanUserId() {
        //given
        String userId = "사용자123";

        //when
        NoticeAuthor author = NoticeAuthor.of(userId);

        //then
        assertThat(author.userId()).isEqualTo(userId);
    }
}