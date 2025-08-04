package com.backend.immilog.notice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class NoticeReadStatusTest {

    @Test
    @DisplayName("빈 NoticeReadStatus 생성")
    void createEmptyNoticeReadStatus() {
        //when
        NoticeReadStatus readStatus = NoticeReadStatus.empty();

        //then
        assertThat(readStatus.getReadCount()).isZero();
        assertThat(readStatus.getReadUsersList()).isEmpty();
    }

    @Test
    @DisplayName("사용자 ID 목록으로 NoticeReadStatus 생성")
    void createNoticeReadStatusWithUserIds() {
        //given
        List<String> userIds = List.of("user1", "user2", "user3");

        //when
        NoticeReadStatus readStatus = NoticeReadStatus.of(userIds);

        //then
        assertThat(readStatus.getReadCount()).isEqualTo(3);
        assertThat(readStatus.getReadUsersList()).containsExactlyInAnyOrder("user1", "user2", "user3");
    }

    @Test
    @DisplayName("null 사용자 ID 목록으로 NoticeReadStatus 생성")
    void createNoticeReadStatusWithNullUserIds() {
        //given
        List<String> userIds = null;

        //when
        NoticeReadStatus readStatus = NoticeReadStatus.of(userIds);

        //then
        assertThat(readStatus.getReadCount()).isZero();
        assertThat(readStatus.getReadUsersList()).isEmpty();
    }

    @Test
    @DisplayName("빈 사용자 ID 목록으로 NoticeReadStatus 생성")
    void createNoticeReadStatusWithEmptyUserIds() {
        //given
        List<String> userIds = List.of();

        //when
        NoticeReadStatus readStatus = NoticeReadStatus.of(userIds);

        //then
        assertThat(readStatus.getReadCount()).isZero();
        assertThat(readStatus.getReadUsersList()).isEmpty();
    }

    @Test
    @DisplayName("중복 사용자 ID로 NoticeReadStatus 생성")
    void createNoticeReadStatusWithDuplicateUserIds() {
        //given
        List<String> userIds = List.of("user1", "user2", "user1", "user3", "user2");

        //when
        NoticeReadStatus readStatus = NoticeReadStatus.of(userIds);

        //then
        assertThat(readStatus.getReadCount()).isEqualTo(3);
        assertThat(readStatus.getReadUsersList()).containsExactlyInAnyOrder("user1", "user2", "user3");
    }

    @Test
    @DisplayName("사용자를 읽음 처리 - 정상 케이스")
    void markAsReadSuccessfully() {
        //given
        NoticeReadStatus readStatus = NoticeReadStatus.empty();
        String userId = "user1";

        //when
        NoticeReadStatus updatedStatus = readStatus.markAsRead(userId);

        //then
        assertThat(updatedStatus.getReadCount()).isEqualTo(1);
        assertThat(updatedStatus.isReadBy(userId)).isTrue();
        assertThat(updatedStatus.getReadUsersList()).containsExactly(userId);
    }

    @Test
    @DisplayName("사용자를 읽음 처리 - 이미 읽은 사용자")
    void markAsReadForAlreadyReadUser() {
        //given
        List<String> userIds = List.of("user1");
        NoticeReadStatus readStatus = NoticeReadStatus.of(userIds);

        //when
        NoticeReadStatus updatedStatus = readStatus.markAsRead("user1");

        //then
        assertThat(updatedStatus.getReadCount()).isEqualTo(1);
        assertThat(updatedStatus.isReadBy("user1")).isTrue();
        assertThat(updatedStatus.getReadUsersList()).containsExactly("user1");
    }

    @Test
    @DisplayName("사용자를 읽음 처리 - null 사용자 ID")
    void markAsReadWithNullUserId() {
        //given
        NoticeReadStatus readStatus = NoticeReadStatus.empty();
        String userId = null;

        //when
        NoticeReadStatus updatedStatus = readStatus.markAsRead(userId);

        //then
        assertThat(updatedStatus.getReadCount()).isZero();
        assertThat(updatedStatus.getReadUsersList()).isEmpty();
        assertThat(updatedStatus).isEqualTo(readStatus);
    }

    @Test
    @DisplayName("사용자를 읽음 처리 - 빈 문자열 사용자 ID")
    void markAsReadWithBlankUserId() {
        //given
        NoticeReadStatus readStatus = NoticeReadStatus.empty();
        String userId = "";

        //when
        NoticeReadStatus updatedStatus = readStatus.markAsRead(userId);

        //then
        assertThat(updatedStatus.getReadCount()).isZero();
        assertThat(updatedStatus.getReadUsersList()).isEmpty();
        assertThat(updatedStatus).isEqualTo(readStatus);
    }

    @Test
    @DisplayName("사용자를 읽음 처리 - 공백 문자열 사용자 ID")
    void markAsReadWithWhitespaceUserId() {
        //given
        NoticeReadStatus readStatus = NoticeReadStatus.empty();
        String userId = "   ";

        //when
        NoticeReadStatus updatedStatus = readStatus.markAsRead(userId);

        //then
        assertThat(updatedStatus.getReadCount()).isZero();
        assertThat(updatedStatus.getReadUsersList()).isEmpty();
        assertThat(updatedStatus).isEqualTo(readStatus);
    }

    @Test
    @DisplayName("사용자 읽음 상태 확인 - 읽은 사용자")
    void isReadByForReadUser() {
        //given
        List<String> userIds = List.of("user1", "user2");
        NoticeReadStatus readStatus = NoticeReadStatus.of(userIds);

        //when & then
        assertThat(readStatus.isReadBy("user1")).isTrue();
        assertThat(readStatus.isReadBy("user2")).isTrue();
    }

    @Test
    @DisplayName("사용자 읽음 상태 확인 - 읽지 않은 사용자")
    void isReadByForUnreadUser() {
        //given
        List<String> userIds = List.of("user1", "user2");
        NoticeReadStatus readStatus = NoticeReadStatus.of(userIds);

        //when & then
        assertThat(readStatus.isReadBy("user3")).isFalse();
    }

    @Test
    @DisplayName("사용자 읽음 상태 확인 - null 사용자 ID")
    void isReadByWithNullUserId() {
        //given
        List<String> userIds = List.of("user1");
        NoticeReadStatus readStatus = NoticeReadStatus.of(userIds);

        //when & then
        assertThat(readStatus.isReadBy(null)).isFalse();
    }

    @Test
    @DisplayName("여러 사용자를 순차적으로 읽음 처리")
    void markMultipleUsersAsReadSequentially() {
        //given
        NoticeReadStatus readStatus = NoticeReadStatus.empty();

        //when
        NoticeReadStatus status1 = readStatus.markAsRead("user1");
        NoticeReadStatus status2 = status1.markAsRead("user2");
        NoticeReadStatus status3 = status2.markAsRead("user3");

        //then
        assertThat(status3.getReadCount()).isEqualTo(3);
        assertThat(status3.isReadBy("user1")).isTrue();
        assertThat(status3.isReadBy("user2")).isTrue();
        assertThat(status3.isReadBy("user3")).isTrue();
        assertThat(status3.getReadUsersList()).containsExactlyInAnyOrder("user1", "user2", "user3");
    }

    @Test
    @DisplayName("읽은 사용자 수 확인")
    void getReadCount() {
        //given
        List<String> userIds = List.of("user1", "user2", "user3", "user4", "user5");
        NoticeReadStatus readStatus = NoticeReadStatus.of(userIds);

        //when & then
        assertThat(readStatus.getReadCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("읽은 사용자 목록 불변성 확인")
    void getReadUsersListImmutability() {
        //given
        List<String> userIds = List.of("user1", "user2");
        NoticeReadStatus readStatus = NoticeReadStatus.of(userIds);

        //when
        List<String> readUsersList = readStatus.getReadUsersList();

        //then
        assertThat(readUsersList).isNotSameAs(userIds);
        assertThatCode(() -> readUsersList.add("user3")).doesNotThrowAnyException();
        assertThat(readStatus.getReadCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("NoticeReadStatus 동등성 테스트")
    void equalityTest() {
        //given
        List<String> userIds = List.of("user1", "user2");
        NoticeReadStatus readStatus1 = NoticeReadStatus.of(userIds);
        NoticeReadStatus readStatus2 = NoticeReadStatus.of(userIds);
        NoticeReadStatus readStatus3 = NoticeReadStatus.of(List.of("user3"));

        //when & then
        assertThat(readStatus1).isEqualTo(readStatus2);
        assertThat(readStatus1).isNotEqualTo(readStatus3);
        assertThat(readStatus1.hashCode()).isEqualTo(readStatus2.hashCode());
    }

    @Test
    @DisplayName("빈 상태에서 불변성 확인")
    void immutabilityFromEmptyState() {
        //given
        NoticeReadStatus emptyStatus = NoticeReadStatus.empty();

        //when
        NoticeReadStatus updatedStatus = emptyStatus.markAsRead("user1");

        //then
        assertThat(emptyStatus.getReadCount()).isZero();
        assertThat(updatedStatus.getReadCount()).isEqualTo(1);
        assertThat(emptyStatus).isNotEqualTo(updatedStatus);
    }

    @Test
    @DisplayName("대량의 사용자 읽음 처리")
    void markLargeNumberOfUsersAsRead() {
        //given
        NoticeReadStatus readStatus = NoticeReadStatus.empty();

        //when
        for (int i = 1; i <= 1000; i++) {
            readStatus = readStatus.markAsRead("user" + i);
        }

        //then
        assertThat(readStatus.getReadCount()).isEqualTo(1000);
        assertThat(readStatus.isReadBy("user1")).isTrue();
        assertThat(readStatus.isReadBy("user500")).isTrue();
        assertThat(readStatus.isReadBy("user1000")).isTrue();
        assertThat(readStatus.isReadBy("user1001")).isFalse();
    }
}