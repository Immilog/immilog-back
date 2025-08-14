package com.backend.immilog.notice.domain.service;

import com.backend.immilog.notice.domain.enums.NoticeStatus;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.Notice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NoticeFactoryTest {

    private NoticeFactory noticeFactory;

    @BeforeEach
    void setUp() {
        noticeFactory = new NoticeFactory();
    }

    @Test
    @DisplayName("공지사항 생성 - 정상 케이스")
    void createNoticeSuccessfully() {
        //given
        String authorUserId = "authorId";
        String title = "공지사항 제목";
        String content = "공지사항 내용";
        NoticeType type = NoticeType.NOTICE;
        List<String> targetCountries = List.of("KR", "JP");

        //when
        Notice notice = noticeFactory.createNotice(authorUserId, title, content, type, targetCountries);

        //then
        assertThat(notice.getAuthorUserId()).isEqualTo(authorUserId);
        assertThat(notice.getTitleValue()).isEqualTo(title);
        assertThat(notice.getContentValue()).isEqualTo(content);
        assertThat(notice.getType()).isEqualTo(type);
        assertThat(notice.getStatus()).isEqualTo(NoticeStatus.NORMAL);
        assertThat(notice.getTargetCountries()).containsExactlyInAnyOrder("KR", "JP");
        assertThat(notice.isActive()).isTrue();
        assertThat(notice.getReadCount()).isZero();
    }

    @Test
    @DisplayName("글로벌 공지사항 생성 - 정상 케이스")
    void createGlobalNoticeSuccessfully() {
        //given
        String authorUserId = "authorId";
        String title = "글로벌 공지사항";
        String content = "모든 국가 대상 공지사항";
        NoticeType type = NoticeType.NOTICE;

        //when
        Notice notice = noticeFactory.createGlobalNotice(authorUserId, title, content, type);

        //then
        assertThat(notice.getAuthorUserId()).isEqualTo(authorUserId);
        assertThat(notice.getTitleValue()).isEqualTo(title);
        assertThat(notice.getContentValue()).isEqualTo(content);
        assertThat(notice.getType()).isEqualTo(type);
        assertThat(notice.getStatus()).isEqualTo(NoticeStatus.NORMAL);
        assertThat(notice.getTargetCountries()).isEmpty();
//        assertThat(notice.getTargeting().isGlobal()).isTrue();
    }

    @Test
    @DisplayName("공지사항 내용 업데이트 - 모든 필드 업데이트")
    void updateNoticeContentWithAllFields() {
        //given
        Notice existingNotice = noticeFactory.createNotice(
                "authorId",
                "기존 제목",
                "기존 내용",
                NoticeType.NOTICE,
                List.of("KR")
        );
        String newTitle = "새로운 제목";
        String newContent = "새로운 내용";
        NoticeType newType = NoticeType.EVENT;

        //when
        Notice updatedNotice = noticeFactory.updateNoticeContent(existingNotice, newTitle, newContent, newType);

        //then
        assertThat(updatedNotice.getTitleValue()).isEqualTo(newTitle);
        assertThat(updatedNotice.getContentValue()).isEqualTo(newContent);
        assertThat(updatedNotice.getType()).isEqualTo(newType);
        assertThat(updatedNotice.getAuthorUserId()).isEqualTo(existingNotice.getAuthorUserId());
    }

    @Test
    @DisplayName("공지사항 내용 업데이트 - 제목만 업데이트")
    void updateNoticeContentWithTitleOnly() {
        //given
        Notice existingNotice = noticeFactory.createNotice(
                "authorId",
                "기존 제목",
                "기존 내용",
                NoticeType.NOTICE,
                List.of("KR")
        );
        String newTitle = "새로운 제목";

        //when
        Notice updatedNotice = noticeFactory.updateNoticeContent(existingNotice, newTitle, null, null);

        //then
        assertThat(updatedNotice.getTitleValue()).isEqualTo(newTitle);
        assertThat(updatedNotice.getContentValue()).isEqualTo(existingNotice.getContentValue());
        assertThat(updatedNotice.getType()).isEqualTo(existingNotice.getType());
    }

    @Test
    @DisplayName("공지사항 내용 업데이트 - 내용만 업데이트")
    void updateNoticeContentWithContentOnly() {
        //given
        Notice existingNotice = noticeFactory.createNotice(
                "authorId",
                "기존 제목",
                "기존 내용",
                NoticeType.NOTICE,
                List.of("KR")
        );
        String newContent = "새로운 내용";

        //when
        Notice updatedNotice = noticeFactory.updateNoticeContent(existingNotice, null, newContent, null);

        //then
        assertThat(updatedNotice.getTitleValue()).isEqualTo(existingNotice.getTitleValue());
        assertThat(updatedNotice.getContentValue()).isEqualTo(newContent);
        assertThat(updatedNotice.getType()).isEqualTo(existingNotice.getType());
    }

    @Test
    @DisplayName("공지사항 내용 업데이트 - 타입만 업데이트")
    void updateNoticeContentWithTypeOnly() {
        //given
        Notice existingNotice = noticeFactory.createNotice(
                "authorId",
                "기존 제목",
                "기존 내용",
                NoticeType.NOTICE,
                List.of("KR")
        );
        NoticeType newType = NoticeType.PROMOTION;

        //when
        Notice updatedNotice = noticeFactory.updateNoticeContent(existingNotice, null, null, newType);

        //then
        assertThat(updatedNotice.getTitleValue()).isEqualTo(existingNotice.getTitleValue());
        assertThat(updatedNotice.getContentValue()).isEqualTo(existingNotice.getContentValue());
        assertThat(updatedNotice.getType()).isEqualTo(newType);
    }

    @Test
    @DisplayName("공지사항 내용 업데이트 - 빈 문자열 제목")
    void updateNoticeContentWithEmptyTitle() {
        //given
        Notice existingNotice = noticeFactory.createNotice(
                "authorId",
                "기존 제목",
                "기존 내용",
                NoticeType.NOTICE,
                List.of("KR")
        );
        String emptyTitle = "";

        //when
        Notice updatedNotice = noticeFactory.updateNoticeContent(existingNotice, emptyTitle, null, null);

        //then
        assertThat(updatedNotice.getTitleValue()).isEqualTo(existingNotice.getTitleValue());
    }

    @Test
    @DisplayName("공지사항 내용 업데이트 - 공백 문자열 제목")
    void updateNoticeContentWithBlankTitle() {
        //given
        Notice existingNotice = noticeFactory.createNotice(
                "authorId",
                "기존 제목",
                "기존 내용",
                NoticeType.NOTICE,
                List.of("KR")
        );
        String blankTitle = "   ";

        //when
        Notice updatedNotice = noticeFactory.updateNoticeContent(existingNotice, blankTitle, null, null);

        //then
        assertThat(updatedNotice.getTitleValue()).isEqualTo(existingNotice.getTitleValue());
    }

    @Test
    @DisplayName("공지사항 내용 업데이트 - 빈 문자열 내용")
    void updateNoticeContentWithEmptyContent() {
        //given
        Notice existingNotice = noticeFactory.createNotice(
                "authorId",
                "기존 제목",
                "기존 내용",
                NoticeType.NOTICE,
                List.of("KR")
        );
        String emptyContent = "";

        //when
        Notice updatedNotice = noticeFactory.updateNoticeContent(existingNotice, null, emptyContent, null);

        //then
        assertThat(updatedNotice.getContentValue()).isEqualTo(existingNotice.getContentValue());
    }

    @Test
    @DisplayName("공지사항 내용 업데이트 - 공백 문자열 내용")
    void updateNoticeContentWithBlankContent() {
        //given
        Notice existingNotice = noticeFactory.createNotice(
                "authorId",
                "기존 제목",
                "기존 내용",
                NoticeType.NOTICE,
                List.of("KR")
        );
        String blankContent = "   ";

        //when
        Notice updatedNotice = noticeFactory.updateNoticeContent(existingNotice, null, blankContent, null);

        //then
        assertThat(updatedNotice.getContentValue()).isEqualTo(existingNotice.getContentValue());
    }

    @Test
    @DisplayName("공지사항 내용 업데이트 - 아무것도 업데이트하지 않음")
    void updateNoticeContentWithNoChanges() {
        //given
        Notice existingNotice = noticeFactory.createNotice(
                "authorId",
                "기존 제목",
                "기존 내용",
                NoticeType.NOTICE,
                List.of("KR")
        );

        //when
        Notice updatedNotice = noticeFactory.updateNoticeContent(existingNotice, null, null, null);

        //then
        assertThat(updatedNotice.getTitleValue()).isEqualTo(existingNotice.getTitleValue());
        assertThat(updatedNotice.getContentValue()).isEqualTo(existingNotice.getContentValue());
        assertThat(updatedNotice.getType()).isEqualTo(existingNotice.getType());
    }

    @Test
    @DisplayName("다양한 국가로 공지사항 생성")
    void createNoticeWithVariousCountries() {
        //given
        String authorUserId = "authorId";
        String title = "다국가 공지사항";
        String content = "여러 국가 대상 공지사항";
        NoticeType type = NoticeType.EVENT;
        List<String> targetCountries = List.of("KR", "JP", "CN", "MY");

        //when
        Notice notice = noticeFactory.createNotice(authorUserId, title, content, type, targetCountries);

        //then
        assertThat(notice.getTargetCountries()).containsExactlyInAnyOrder(
                "KR", "JP", "CN", "MY"
        );
        assertThat(notice.getTargeting().getTargetCount()).isEqualTo(4);
        assertThat(notice.getTargeting().isGlobal()).isTrue();
    }

    @Test
    @DisplayName("모든 NoticeType으로 공지사항 생성")
    void createNoticeWithAllNoticeTypes() {
        //given
        String authorUserId = "authorId";
        String title = "테스트 제목";
        String content = "테스트 내용";
        List<String> targetCountries = List.of("KR");

        //when & then
        for (NoticeType type : NoticeType.values()) {
            Notice notice = noticeFactory.createNotice(authorUserId, title, content, type, targetCountries);
            assertThat(notice.getType()).isEqualTo(type);
            assertThat(notice.getStatus()).isEqualTo(NoticeStatus.NORMAL);
        }
    }

//    @Test
//    @DisplayName("글로벌 공지사항의 모든 국가 타겟팅 확인")
//    void globalNoticeTargetsAllCountries() {
//        //given
//        String authorUserId = "authorId";
//        String title = "글로벌 공지";
//        String content = "전 세계 공지";
//        NoticeType type = NoticeType.PROMOTION;
//
//        //when
//        Notice notice = noticeFactory.createGlobalNotice(authorUserId, title, content, type);
//
//        //then
//        assertThat(notice.getTargeting().isGlobal()).isTrue();
//    }

    @Test
    @DisplayName("긴 제목과 내용으로 공지사항 생성")
    void createNoticeWithLongTitleAndContent() {
        //given
        String authorUserId = "authorId";
        String longTitle = "매우 긴 제목입니다. ".repeat(10);
        String longContent = "매우 긴 내용입니다. ".repeat(100);
        NoticeType type = NoticeType.NOTICE;
        List<String> targetCountries = List.of("KR");

        //when
        Notice notice = noticeFactory.createNotice(authorUserId, longTitle, longContent, type, targetCountries);

        //then
        assertThat(notice.getTitleValue()).isEqualTo(longTitle.trim());
        assertThat(notice.getContentValue()).isEqualTo(longContent.trim());
        assertThat(notice.getTitle().length()).isGreaterThan(100);
        assertThat(notice.getContent().length()).isGreaterThan(1000);
    }

    @Test
    @DisplayName("특수 문자가 포함된 제목과 내용으로 공지사항 생성")
    void createNoticeWithSpecialCharacters() {
        //given
        String authorUserId = "authorId";
        String titleWithSpecialChars = "공지사항 제목 !@#$%^&*()";
        String contentWithSpecialChars = "공지사항 내용에 특수문자: <>&\"'";
        NoticeType type = NoticeType.NOTICE;
        List<String> targetCountries = List.of("KR");

        //when
        Notice notice = noticeFactory.createNotice(authorUserId, titleWithSpecialChars, contentWithSpecialChars, type, targetCountries);

        //then
        assertThat(notice.getTitleValue()).isEqualTo(titleWithSpecialChars);
        assertThat(notice.getContentValue()).isEqualTo(contentWithSpecialChars);
    }

    @Test
    @DisplayName("업데이트 후 시간 정보 확인")
    void updateNoticeContentUpdatesTimestamp() {
        //given
        Notice existingNotice = noticeFactory.createNotice(
                "authorId",
                "기존 제목",
                "기존 내용",
                NoticeType.NOTICE,
                List.of("KR")
        );
        LocalDateTime originalUpdatedAt = existingNotice.getUpdatedAt();
        String newTitle = "새로운 제목";

        //when
        Notice updatedNotice = noticeFactory.updateNoticeContent(existingNotice, newTitle, null, null);

        //then
        assertThat(updatedNotice.getUpdatedAt()).isAfter(originalUpdatedAt);
        assertThat(updatedNotice.getCreatedAt()).isEqualTo(existingNotice.getCreatedAt());
    }
}