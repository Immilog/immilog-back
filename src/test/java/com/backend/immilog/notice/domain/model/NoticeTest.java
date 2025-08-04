package com.backend.immilog.notice.domain.model;

import com.backend.immilog.notice.domain.enums.NoticeStatus;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.exception.NoticeException;
import com.backend.immilog.shared.enums.Country;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class NoticeTest {

    @Test
    @DisplayName("공지사항 생성 - 정상 케이스")
    void createNoticeSuccessfully() {
        //given
        NoticeAuthor author = NoticeAuthor.of("authorId");
        NoticeTitle title = NoticeTitle.of("공지사항 제목");
        NoticeContent content = NoticeContent.of("공지사항 내용");
        NoticeType type = NoticeType.NOTICE;
        NoticeTargeting targeting = NoticeTargeting.of(List.of(Country.SOUTH_KOREA));

        //when
        Notice notice = Notice.create(author, title, content, type, targeting);

        //then
        assertThat(notice.getAuthor()).isEqualTo(author);
        assertThat(notice.getTitle()).isEqualTo(title);
        assertThat(notice.getContent()).isEqualTo(content);
        assertThat(notice.getType()).isEqualTo(type);
        assertThat(notice.getStatus()).isEqualTo(NoticeStatus.NORMAL);
        assertThat(notice.getTargeting()).isEqualTo(targeting);
        assertThat(notice.isActive()).isTrue();
        assertThat(notice.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("공지사항 생성 실패 - null 작성자")
    void createNoticeFailWhenAuthorIsNull() {
        //given
        NoticeAuthor author = null;
        NoticeTitle title = NoticeTitle.of("공지사항 제목");
        NoticeContent content = NoticeContent.of("공지사항 내용");
        NoticeType type = NoticeType.NOTICE;
        NoticeTargeting targeting = NoticeTargeting.of(List.of(Country.SOUTH_KOREA));

        //when & then
        assertThatThrownBy(() -> Notice.create(author, title, content, type, targeting))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 생성 실패 - null 제목")
    void createNoticeFailWhenTitleIsNull() {
        //given
        NoticeAuthor author = NoticeAuthor.of("authorId");
        NoticeTitle title = null;
        NoticeContent content = NoticeContent.of("공지사항 내용");
        NoticeType type = NoticeType.NOTICE;
        NoticeTargeting targeting = NoticeTargeting.of(List.of(Country.SOUTH_KOREA));

        //when & then
        assertThatThrownBy(() -> Notice.create(author, title, content, type, targeting))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 생성 실패 - null 내용")
    void createNoticeFailWhenContentIsNull() {
        //given
        NoticeAuthor author = NoticeAuthor.of("authorId");
        NoticeTitle title = NoticeTitle.of("공지사항 제목");
        NoticeContent content = null;
        NoticeType type = NoticeType.NOTICE;
        NoticeTargeting targeting = NoticeTargeting.of(List.of(Country.SOUTH_KOREA));

        //when & then
        assertThatThrownBy(() -> Notice.create(author, title, content, type, targeting))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 생성 실패 - null 타입")
    void createNoticeFailWhenTypeIsNull() {
        //given
        NoticeAuthor author = NoticeAuthor.of("authorId");
        NoticeTitle title = NoticeTitle.of("공지사항 제목");
        NoticeContent content = NoticeContent.of("공지사항 내용");
        NoticeType type = null;
        NoticeTargeting targeting = NoticeTargeting.of(List.of(Country.SOUTH_KOREA));

        //when & then
        assertThatThrownBy(() -> Notice.create(author, title, content, type, targeting))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 생성 실패 - null 타겟팅")
    void createNoticeFailWhenTargetingIsNull() {
        //given
        NoticeAuthor author = NoticeAuthor.of("authorId");
        NoticeTitle title = NoticeTitle.of("공지사항 제목");
        NoticeContent content = NoticeContent.of("공지사항 내용");
        NoticeType type = NoticeType.NOTICE;
        NoticeTargeting targeting = null;

        //when & then
        assertThatThrownBy(() -> Notice.create(author, title, content, type, targeting))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 제목 업데이트 - 정상 케이스")
    void updateTitleSuccessfully() {
        //given
        Notice notice = createTestNotice();
        NoticeTitle newTitle = NoticeTitle.of("새로운 제목");

        //when
        notice.updateTitle(newTitle);

        //then
        assertThat(notice.getTitle()).isEqualTo(newTitle);
    }

    @Test
    @DisplayName("공지사항 제목 업데이트 - 같은 제목")
    void updateTitleWithSameTitle() {
        //given
        Notice notice = createTestNotice();
        NoticeTitle originalTitle = notice.getTitle();

        //when
        notice.updateTitle(originalTitle);

        //then
        assertThat(notice.getTitle()).isEqualTo(originalTitle);
    }

    @Test
    @DisplayName("공지사항 제목 업데이트 실패 - 삭제된 공지사항")
    void updateTitleFailWhenDeleted() {
        //given
        Notice notice = createTestNotice();
        notice.delete();
        NoticeTitle newTitle = NoticeTitle.of("새로운 제목");

        //when & then
        assertThatThrownBy(() -> notice.updateTitle(newTitle))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 내용 업데이트 - 정상 케이스")
    void updateContentSuccessfully() {
        //given
        Notice notice = createTestNotice();
        NoticeContent newContent = NoticeContent.of("새로운 내용");

        //when
        notice.updateContent(newContent);

        //then
        assertThat(notice.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("공지사항 내용 업데이트 - 같은 내용")
    void updateContentWithSameContent() {
        //given
        Notice notice = createTestNotice();
        NoticeContent originalContent = notice.getContent();

        //when
        notice.updateContent(originalContent);

        //then
        assertThat(notice.getContent()).isEqualTo(originalContent);
    }

    @Test
    @DisplayName("공지사항 내용 업데이트 실패 - 삭제된 공지사항")
    void updateContentFailWhenDeleted() {
        //given
        Notice notice = createTestNotice();
        notice.delete();
        NoticeContent newContent = NoticeContent.of("새로운 내용");

        //when & then
        assertThatThrownBy(() -> notice.updateContent(newContent))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 타입 업데이트 - 정상 케이스")
    void updateTypeSuccessfully() {
        //given
        Notice notice = createTestNotice();
        NoticeType newType = NoticeType.EVENT;

        //when
        notice.updateType(newType);

        //then
        assertThat(notice.getType()).isEqualTo(newType);
    }

    @Test
    @DisplayName("공지사항 타입 업데이트 실패 - 삭제된 공지사항")
    void updateTypeFailWhenDeleted() {
        //given
        Notice notice = createTestNotice();
        notice.delete();
        NoticeType newType = NoticeType.EVENT;

        //when & then
        assertThatThrownBy(() -> notice.updateType(newType))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 읽음 처리 - 정상 케이스")
    void markAsReadSuccessfully() {
        //given
        Notice notice = createTestNotice();
        String userId = "userId";

        //when
        notice.markAsRead(userId);

        //then
        assertThat(notice.isReadBy(userId)).isTrue();
        assertThat(notice.getReadCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("공지사항 읽음 처리 - 중복 읽음")
    void markAsReadDuplicately() {
        //given
        Notice notice = createTestNotice();
        String userId = "userId";

        //when
        notice.markAsRead(userId);
        notice.markAsRead(userId);

        //then
        assertThat(notice.isReadBy(userId)).isTrue();
        assertThat(notice.getReadCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("공지사항 읽음 처리 실패 - null 사용자 ID")
    void markAsReadFailWithNullUserId() {
        //given
        Notice notice = createTestNotice();
        String userId = null;

        //when & then
        assertThatThrownBy(() -> notice.markAsRead(userId))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 읽음 처리 실패 - 빈 문자열 사용자 ID")
    void markAsReadFailWithBlankUserId() {
        //given
        Notice notice = createTestNotice();
        String userId = "";

        //when & then
        assertThatThrownBy(() -> notice.markAsRead(userId))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 삭제 - 정상 케이스")
    void deleteNoticeSuccessfully() {
        //given
        Notice notice = createTestNotice();

        //when
        notice.delete();

        //then
        assertThat(notice.isDeleted()).isTrue();
        assertThat(notice.isActive()).isFalse();
        assertThat(notice.getStatus()).isEqualTo(NoticeStatus.DELETED);
    }

    @Test
    @DisplayName("공지사항 삭제 실패 - 이미 삭제된 공지사항")
    void deleteNoticeFailWhenAlreadyDeleted() {
        //given
        Notice notice = createTestNotice();
        notice.delete();

        //when & then
        assertThatThrownBy(notice::delete)
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 활성화 - 정상 케이스")
    void activateNoticeSuccessfully() {
        //given
        Notice notice = createTestNotice();
        notice.delete();

        //when
        notice.activate();

        //then
        assertThat(notice.isActive()).isTrue();
        assertThat(notice.isDeleted()).isFalse();
        assertThat(notice.getStatus()).isEqualTo(NoticeStatus.NORMAL);
    }

    @Test
    @DisplayName("공지사항 활성화 - 이미 활성화된 공지사항")
    void activateNoticeWhenAlreadyActive() {
        //given
        Notice notice = createTestNotice();

        //when
        notice.activate();

        //then
        assertThat(notice.isActive()).isTrue();
        assertThat(notice.getStatus()).isEqualTo(NoticeStatus.NORMAL);
    }

    @Test
    @DisplayName("타겟 국가 확인 - 포함된 경우")
    void isTargetedToCountryWhenIncluded() {
        //given
        Notice notice = createTestNotice();

        //when & then
        assertThat(notice.isTargetedTo(Country.SOUTH_KOREA)).isTrue();
    }

    @Test
    @DisplayName("타겟 국가 확인 - 포함되지 않은 경우")
    void isTargetedToCountryWhenNotIncluded() {
        //given
        Notice notice = createTestNotice();

        //when & then
        assertThat(notice.isTargetedTo(Country.JAPAN)).isFalse();
    }

    @Test
    @DisplayName("작성자 확인 - 맞는 경우")
    void isAuthorWhenCorrect() {
        //given
        String authorId = "authorId";
        Notice notice = createTestNoticeWithAuthor(authorId);

        //when & then
        assertThat(notice.isAuthor(authorId)).isTrue();
    }

    @Test
    @DisplayName("작성자 확인 - 틀린 경우")
    void isAuthorWhenIncorrect() {
        //given
        String authorId = "authorId";
        String otherUserId = "otherUserId";
        Notice notice = createTestNoticeWithAuthor(authorId);

        //when & then
        assertThat(notice.isAuthor(otherUserId)).isFalse();
    }

    @Test
    @DisplayName("공지사항 복원 - 모든 필드 포함")
    void restoreNotice() {
        //given
        NoticeId id = NoticeId.of("noticeId");
        NoticeAuthor author = NoticeAuthor.of("authorId");
        NoticeTitle title = NoticeTitle.of("복원된 제목");
        NoticeContent content = NoticeContent.of("복원된 내용");
        NoticeType type = NoticeType.EVENT;
        NoticeStatus status = NoticeStatus.DELETED;
        NoticeTargeting targeting = NoticeTargeting.of(List.of(Country.JAPAN));
        NoticeReadStatus readStatus = NoticeReadStatus.of(List.of("user1", "user2"));
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        //when
        Notice notice = Notice.restore(
                id, author, title, content, type, status, targeting, readStatus, createdAt, updatedAt
        );

        //then
        assertThat(notice.getId()).isEqualTo(id);
        assertThat(notice.getAuthor()).isEqualTo(author);
        assertThat(notice.getTitle()).isEqualTo(title);
        assertThat(notice.getContent()).isEqualTo(content);
        assertThat(notice.getType()).isEqualTo(type);
        assertThat(notice.getStatus()).isEqualTo(status);
        assertThat(notice.getTargeting()).isEqualTo(targeting);
        assertThat(notice.getReadStatus()).isEqualTo(readStatus);
        assertThat(notice.getCreatedAt()).isEqualTo(createdAt);
        assertThat(notice.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("게터 메서드 테스트")
    void getterMethods() {
        //given
        Notice notice = createTestNotice();
        
        //when & then
        assertThat(notice.getIdValue()).isNull();
        assertThat(notice.getAuthorUserId()).isEqualTo("authorId");
        assertThat(notice.getTitleValue()).isEqualTo("공지사항 제목");
        assertThat(notice.getContentValue()).isEqualTo("공지사항 내용");
        assertThat(notice.getTargetCountries()).containsExactly(Country.SOUTH_KOREA);
        assertThat(notice.getReadUsers()).isEmpty();
        assertThat(notice.getCreatedAt()).isNotNull();
        assertThat(notice.getUpdatedAt()).isNotNull();
        assertThat(notice.getReadCount()).isZero();
    }

    @Test
    @DisplayName("ID가 있는 공지사항의 게터 메서드 테스트")
    void getterMethodsWithId() {
        //given
        NoticeId id = NoticeId.of("noticeId");
        Notice notice = Notice.restore(
                id,
                NoticeAuthor.of("authorId"),
                NoticeTitle.of("제목"),
                NoticeContent.of("내용"),
                NoticeType.NOTICE,
                NoticeStatus.NORMAL,
                NoticeTargeting.of(List.of(Country.SOUTH_KOREA)),
                NoticeReadStatus.empty(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        
        //when & then
        assertThat(notice.getIdValue()).isEqualTo("noticeId");
    }

    @Test
    @DisplayName("여러 사용자가 읽은 공지사항")
    void noticeReadByMultipleUsers() {
        //given
        Notice notice = createTestNotice();
        String user1 = "user1";
        String user2 = "user2";
        String user3 = "user3";

        //when
        notice.markAsRead(user1);
        notice.markAsRead(user2);
        notice.markAsRead(user3);

        //then
        assertThat(notice.isReadBy(user1)).isTrue();
        assertThat(notice.isReadBy(user2)).isTrue();
        assertThat(notice.isReadBy(user3)).isTrue();
        assertThat(notice.getReadCount()).isEqualTo(3);
        assertThat(notice.getReadUsers()).containsExactlyInAnyOrder(user1, user2, user3);
    }

    private Notice createTestNotice() {
        return Notice.create(
                NoticeAuthor.of("authorId"),
                NoticeTitle.of("공지사항 제목"),
                NoticeContent.of("공지사항 내용"),
                NoticeType.NOTICE,
                NoticeTargeting.of(List.of(Country.SOUTH_KOREA))
        );
    }

    private Notice createTestNoticeWithAuthor(String authorId) {
        return Notice.create(
                NoticeAuthor.of(authorId),
                NoticeTitle.of("공지사항 제목"),
                NoticeContent.of("공지사항 내용"),
                NoticeType.NOTICE,
                NoticeTargeting.of(List.of(Country.SOUTH_KOREA))
        );
    }
}