package com.backend.immilog.notice.domain.model;

import com.backend.immilog.notice.domain.enums.NoticeStatus;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.exception.NoticeErrorCode;
import com.backend.immilog.notice.exception.NoticeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Notice 도메인 모델")
class NoticeTest {

    @Nested
    @DisplayName("공지사항 생성")
    class CreateNotice {

        @Test
        @DisplayName("정상적인 공지사항 생성")
        void createValidNotice() {
            var author = NoticeAuthor.of("user123");
            var title = NoticeTitle.of("공지사항 제목");
            var content = NoticeContent.of("공지사항 내용");
            var targeting = NoticeTargeting.of(List.of("KR", "US"));

            var notice = Notice.create(author, title, content, NoticeType.NOTICE, targeting);

            assertThat(notice).isNotNull();
            assertThat(notice.getAuthorUserId()).isEqualTo("user123");
            assertThat(notice.getTitleValue()).isEqualTo("공지사항 제목");
            assertThat(notice.getContentValue()).isEqualTo("공지사항 내용");
            assertThat(notice.getType()).isEqualTo(NoticeType.NOTICE);
            assertThat(notice.getStatus()).isEqualTo(NoticeStatus.NORMAL);
            assertThat(notice.getTargetCountries()).containsExactly("KR", "US");
            assertThat(notice.isActive()).isTrue();
            assertThat(notice.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("null 작성자로 생성 시 예외 발생")
        void createNoticeWithNullAuthor() {
            var title = NoticeTitle.of("공지사항 제목");
            var content = NoticeContent.of("공지사항 내용");
            var targeting = NoticeTargeting.of(List.of("KR"));

            assertThatThrownBy(() -> Notice.create(null, title, content, NoticeType.NOTICE, targeting))
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_NOTICE_AUTHOR);
        }

        @Test
        @DisplayName("null 제목으로 생성 시 예외 발생")
        void createNoticeWithNullTitle() {
            var author = NoticeAuthor.of("user123");
            var content = NoticeContent.of("공지사항 내용");
            var targeting = NoticeTargeting.of(List.of("KR"));

            assertThatThrownBy(() -> Notice.create(author, null, content, NoticeType.NOTICE, targeting))
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_NOTICE_TITLE);
        }

        @Test
        @DisplayName("null 내용으로 생성 시 예외 발생")
        void createNoticeWithNullContent() {
            var author = NoticeAuthor.of("user123");
            var title = NoticeTitle.of("공지사항 제목");
            var targeting = NoticeTargeting.of(List.of("KR"));

            assertThatThrownBy(() -> Notice.create(author, title, null, NoticeType.NOTICE, targeting))
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_NOTICE_CONTENT);
        }

        @Test
        @DisplayName("null 타입으로 생성 시 예외 발생")
        void createNoticeWithNullType() {
            var author = NoticeAuthor.of("user123");
            var title = NoticeTitle.of("공지사항 제목");
            var content = NoticeContent.of("공지사항 내용");
            var targeting = NoticeTargeting.of(List.of("KR"));

            assertThatThrownBy(() -> Notice.create(author, title, content, null, targeting))
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_NOTICE_TYPE);
        }

        @Test
        @DisplayName("null 타겟팅으로 생성 시 예외 발생")
        void createNoticeWithNullTargeting() {
            var author = NoticeAuthor.of("user123");
            var title = NoticeTitle.of("공지사항 제목");
            var content = NoticeContent.of("공지사항 내용");

            assertThatThrownBy(() -> Notice.create(author, title, content, NoticeType.NOTICE, null))
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_NOTICE_TARGET_COUNTRIES);
        }
    }

    @Nested
    @DisplayName("공지사항 수정")
    class UpdateNotice {

        @Test
        @DisplayName("제목 수정")
        void updateTitle() {
            var notice = createTestNotice();
            var newTitle = NoticeTitle.of("새로운 제목");

            var updatedNotice = notice.updateTitle(newTitle);

            assertThat(updatedNotice.getTitleValue()).isEqualTo("새로운 제목");
        }

        @Test
        @DisplayName("동일한 제목으로 수정 시 변경되지 않음")
        void updateTitleWithSameValue() {
            var notice = createTestNotice();
            var sameTitle = NoticeTitle.of(notice.getTitleValue());
            var originalUpdatedAt = notice.getUpdatedAt();

            var updatedNotice = notice.updateTitle(sameTitle);

            assertThat(updatedNotice.getTitleValue()).isEqualTo(notice.getTitleValue());
            assertThat(updatedNotice.getUpdatedAt()).isEqualTo(originalUpdatedAt);
        }

        @Test
        @DisplayName("null 제목으로 수정 시 변경되지 않음")
        void updateTitleWithNull() {
            var notice = createTestNotice();
            var originalTitle = notice.getTitleValue();
            var originalUpdatedAt = notice.getUpdatedAt();

            var updatedNotice = notice.updateTitle(null);

            assertThat(updatedNotice.getTitleValue()).isEqualTo(originalTitle);
            assertThat(updatedNotice.getUpdatedAt()).isEqualTo(originalUpdatedAt);
        }

        @Test
        @DisplayName("내용 수정")
        void updateContent() {
            var notice = createTestNotice();
            var newContent = NoticeContent.of("새로운 내용");

            var updatedNotice = notice.updateContent(newContent);

            assertThat(updatedNotice.getContentValue()).isEqualTo("새로운 내용");
        }

        @Test
        @DisplayName("동일한 내용으로 수정 시 변경되지 않음")
        void updateContentWithSameValue() {
            var notice = createTestNotice();
            var sameContent = NoticeContent.of(notice.getContentValue());
            var originalUpdatedAt = notice.getUpdatedAt();

            var updatedNotice = notice.updateContent(sameContent);

            assertThat(updatedNotice.getContentValue()).isEqualTo(notice.getContentValue());
            assertThat(updatedNotice.getUpdatedAt()).isEqualTo(originalUpdatedAt);
        }

        @Test
        @DisplayName("타입 수정")
        void updateType() {
            var notice = createTestNotice();

            var updatedNotice = notice.updateType(NoticeType.NOTICE);

            assertThat(updatedNotice.getType()).isEqualTo(NoticeType.NOTICE);
        }

        @Test
        @DisplayName("삭제된 공지사항 수정 시 예외 발생")
        void updateDeletedNotice() {
            var notice = createTestNotice().delete();

            assertThatThrownBy(() -> notice.updateTitle(NoticeTitle.of("새 제목")))
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.NOTICE_ALREADY_DELETED);

            assertThatThrownBy(() -> notice.updateContent(NoticeContent.of("새 내용")))
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.NOTICE_ALREADY_DELETED);

            assertThatThrownBy(() -> notice.updateType(NoticeType.NOTICE))
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.NOTICE_ALREADY_DELETED);
        }
    }

    @Nested
    @DisplayName("읽음 처리")
    class MarkAsRead {

        @Test
        @DisplayName("정상적인 읽음 처리")
        void markAsReadValid() {
            var notice = createTestNotice();

            var updatedNotice = notice.markAsRead("user456");

            assertThat(updatedNotice.isReadBy("user456")).isTrue();
            assertThat(updatedNotice.getReadUsers()).contains("user456");
            assertThat(updatedNotice.getReadCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("null 사용자로 읽음 처리 시 예외 발생")
        void markAsReadWithNullUser() {
            var notice = createTestNotice();

            assertThatThrownBy(() -> notice.markAsRead(null))
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_USER_SEQ);
        }

        @Test
        @DisplayName("빈 문자열 사용자로 읽음 처리 시 예외 발생")
        void markAsReadWithBlankUser() {
            var notice = createTestNotice();

            assertThatThrownBy(() -> notice.markAsRead(""))
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_USER_SEQ);

            assertThatThrownBy(() -> notice.markAsRead("   "))
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_USER_SEQ);
        }
    }

    @Nested
    @DisplayName("공지사항 삭제")
    class DeleteNotice {

        @Test
        @DisplayName("정상적인 삭제")
        void deleteValidNotice() {
            var notice = createTestNotice();

            var deletedNotice = notice.delete();

            assertThat(deletedNotice.isDeleted()).isTrue();
            assertThat(deletedNotice.isActive()).isFalse();
            assertThat(deletedNotice.getStatus()).isEqualTo(NoticeStatus.DELETED);
        }

        @Test
        @DisplayName("이미 삭제된 공지사항 삭제 시 예외 발생")
        void deleteAlreadyDeletedNotice() {
            var notice = createTestNotice().delete();

            assertThatThrownBy(() -> notice.delete())
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.NOTICE_ALREADY_DELETED);
        }
    }

    @Nested
    @DisplayName("공지사항 활성화")
    class ActivateNotice {

        @Test
        @DisplayName("삭제된 공지사항 활성화")
        void activateDeletedNotice() {
            var notice = createTestNotice().delete();

            var activatedNotice = notice.activate();

            assertThat(activatedNotice.isActive()).isTrue();
            assertThat(activatedNotice.isDeleted()).isFalse();
            assertThat(activatedNotice.getStatus()).isEqualTo(NoticeStatus.NORMAL);
        }

        @Test
        @DisplayName("이미 활성화된 공지사항 활성화 시 변경되지 않음")
        void activateAlreadyActiveNotice() {
            var notice = createTestNotice();
            var originalStatus = notice.getStatus();

            var activatedNotice = notice.activate();

            assertThat(activatedNotice.isActive()).isTrue();
            assertThat(activatedNotice.getStatus()).isEqualTo(originalStatus);
        }
    }

    @Nested
    @DisplayName("타겟팅 확인")
    class TargetingCheck {

        @Test
        @DisplayName("타겟 국가에 포함된 경우")
        void isTargetedToIncludedCountry() {
            var notice = createTestNotice();

            assertThat(notice.isTargetedTo("KR")).isTrue();
            assertThat(notice.isTargetedTo("US")).isTrue();
        }

        @Test
        @DisplayName("타겟 국가에 포함되지 않은 경우")
        void isTargetedToNotIncludedCountry() {
            var notice = createTestNotice();

            assertThat(notice.isTargetedTo("JP")).isFalse();
            assertThat(notice.isTargetedTo("CN")).isFalse();
        }
    }

    @Nested
    @DisplayName("작성자 확인")
    class AuthorCheck {

        @Test
        @DisplayName("작성자인 경우")
        void isAuthorValid() {
            var notice = createTestNotice();

            assertThat(notice.isAuthor("user123")).isTrue();
        }

        @Test
        @DisplayName("작성자가 아닌 경우")
        void isNotAuthor() {
            var notice = createTestNotice();

            assertThat(notice.isAuthor("user456")).isFalse();
        }
    }

    private Notice createTestNotice() {
        var author = NoticeAuthor.of("user123");
        var title = NoticeTitle.of("테스트 공지사항");
        var content = NoticeContent.of("테스트 내용");
        var targeting = NoticeTargeting.of(List.of("KR", "US"));

        return Notice.create(author, title, content, NoticeType.NOTICE, targeting);
    }
}