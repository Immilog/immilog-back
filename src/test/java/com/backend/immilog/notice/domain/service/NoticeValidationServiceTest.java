package com.backend.immilog.notice.domain.service;

import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.exception.NoticeException;
import com.backend.immilog.shared.enums.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class NoticeValidationServiceTest {

    private NoticeValidationService noticeValidationService;

    @BeforeEach
    void setUp() {
        noticeValidationService = new NoticeValidationService();
    }

    @Test
    @DisplayName("공지사항 생성 검증 - 정상 케이스")
    void validateNoticeCreationSuccessfully() {
        //given
        String title = "공지사항 제목";
        String content = "공지사항 내용";
        NoticeType type = NoticeType.NOTICE;
        List<Country> targetCountries = List.of(Country.SOUTH_KOREA);

        //when & then
        assertThatCode(() -> noticeValidationService.validateNoticeCreation(title, content, type, targetCountries))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("공지사항 생성 검증 실패 - null 제목")
    void validateNoticeCreationFailWithNullTitle() {
        //given
        String title = null;
        String content = "공지사항 내용";
        NoticeType type = NoticeType.NOTICE;
        List<Country> targetCountries = List.of(Country.SOUTH_KOREA);

        //when & then
        assertThatThrownBy(() -> noticeValidationService.validateNoticeCreation(title, content, type, targetCountries))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 생성 검증 실패 - 빈 제목")
    void validateNoticeCreationFailWithEmptyTitle() {
        //given
        String title = "";
        String content = "공지사항 내용";
        NoticeType type = NoticeType.NOTICE;
        List<Country> targetCountries = List.of(Country.SOUTH_KOREA);

        //when & then
        assertThatThrownBy(() -> noticeValidationService.validateNoticeCreation(title, content, type, targetCountries))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 생성 검증 실패 - null 내용")
    void validateNoticeCreationFailWithNullContent() {
        //given
        String title = "공지사항 제목";
        String content = null;
        NoticeType type = NoticeType.NOTICE;
        List<Country> targetCountries = List.of(Country.SOUTH_KOREA);

        //when & then
        assertThatThrownBy(() -> noticeValidationService.validateNoticeCreation(title, content, type, targetCountries))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 생성 검증 실패 - 빈 내용")
    void validateNoticeCreationFailWithEmptyContent() {
        //given
        String title = "공지사항 제목";
        String content = "";
        NoticeType type = NoticeType.NOTICE;
        List<Country> targetCountries = List.of(Country.SOUTH_KOREA);

        //when & then
        assertThatThrownBy(() -> noticeValidationService.validateNoticeCreation(title, content, type, targetCountries))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 생성 검증 실패 - null 타입")
    void validateNoticeCreationFailWithNullType() {
        //given
        String title = "공지사항 제목";
        String content = "공지사항 내용";
        NoticeType type = null;
        List<Country> targetCountries = List.of(Country.SOUTH_KOREA);

        //when & then
        assertThatThrownBy(() -> noticeValidationService.validateNoticeCreation(title, content, type, targetCountries))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 생성 검증 실패 - null 타겟 국가")
    void validateNoticeCreationFailWithNullTargetCountries() {
        //given
        String title = "공지사항 제목";
        String content = "공지사항 내용";
        NoticeType type = NoticeType.NOTICE;
        List<Country> targetCountries = null;

        //when & then
        assertThatThrownBy(() -> noticeValidationService.validateNoticeCreation(title, content, type, targetCountries))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 생성 검증 실패 - 빈 타겟 국가")
    void validateNoticeCreationFailWithEmptyTargetCountries() {
        //given
        String title = "공지사항 제목";
        String content = "공지사항 내용";
        NoticeType type = NoticeType.NOTICE;
        List<Country> targetCountries = List.of();

        //when & then
        assertThatThrownBy(() -> noticeValidationService.validateNoticeCreation(title, content, type, targetCountries))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 업데이트 검증 - 정상 케이스")
    void validateNoticeUpdateSuccessfully() {
        //given
        Notice notice = createTestNotice();
        String title = "새로운 제목";
        String content = "새로운 내용";

        //when & then
        assertThatCode(() -> noticeValidationService.validateNoticeUpdate(notice, title, content))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("공지사항 업데이트 검증 - 제목만 업데이트")
    void validateNoticeUpdateWithTitleOnly() {
        //given
        Notice notice = createTestNotice();
        String title = "새로운 제목";
        String content = null;

        //when & then
        assertThatCode(() -> noticeValidationService.validateNoticeUpdate(notice, title, content))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("공지사항 업데이트 검증 - 내용만 업데이트")
    void validateNoticeUpdateWithContentOnly() {
        //given
        Notice notice = createTestNotice();
        String title = null;
        String content = "새로운 내용";

        //when & then
        assertThatCode(() -> noticeValidationService.validateNoticeUpdate(notice, title, content))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("공지사항 업데이트 검증 실패 - null 공지사항")
    void validateNoticeUpdateFailWithNullNotice() {
        //given
        Notice notice = null;
        String title = "새로운 제목";
        String content = "새로운 내용";

        //when & then
        assertThatThrownBy(() -> noticeValidationService.validateNoticeUpdate(notice, title, content))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 업데이트 검증 실패 - 삭제된 공지사항")
    void validateNoticeUpdateFailWithDeletedNotice() {
        //given
        Notice notice = createTestNotice();
        notice.delete();
        String title = "새로운 제목";
        String content = "새로운 내용";

        //when & then
        assertThatThrownBy(() -> noticeValidationService.validateNoticeUpdate(notice, title, content))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 업데이트 검증 실패 - 잘못된 제목")
    void validateNoticeUpdateFailWithInvalidTitle() {
        //given
        Notice notice = createTestNotice();
        String title = "";
        String content = "새로운 내용";

        //when & then
        assertThatThrownBy(() -> noticeValidationService.validateNoticeUpdate(notice, title, content))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 업데이트 검증 실패 - 잘못된 내용")
    void validateNoticeUpdateFailWithInvalidContent() {
        //given
        Notice notice = createTestNotice();
        String title = "새로운 제목";
        String content = "";

        //when & then
        assertThatThrownBy(() -> noticeValidationService.validateNoticeUpdate(notice, title, content))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 접근 검증 - 정상 케이스")
    void validateNoticeAccessSuccessfully() {
        //given
        Notice notice = createTestNotice();
        String userId = "userId";

        //when & then
        assertThatCode(() -> noticeValidationService.validateNoticeAccess(notice, userId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("공지사항 접근 검증 실패 - null 공지사항")
    void validateNoticeAccessFailWithNullNotice() {
        //given
        Notice notice = null;
        String userId = "userId";

        //when & then
        assertThatThrownBy(() -> noticeValidationService.validateNoticeAccess(notice, userId))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 접근 검증 실패 - null 사용자 ID")
    void validateNoticeAccessFailWithNullUserId() {
        //given
        Notice notice = createTestNotice();
        String userId = null;

        //when & then
        assertThatThrownBy(() -> noticeValidationService.validateNoticeAccess(notice, userId))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("공지사항 접근 검증 실패 - 빈 사용자 ID")
    void validateNoticeAccessFailWithBlankUserId() {
        //given
        Notice notice = createTestNotice();
        String userId = "";

        //when & then
        assertThatThrownBy(() -> noticeValidationService.validateNoticeAccess(notice, userId))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("작성자 권한 검증 - 정상 케이스")
    void validateAuthorPermissionSuccessfully() {
        //given
        String authorId = "authorId";
        Notice notice = createTestNoticeWithAuthor(authorId);

        //when & then
        assertThatCode(() -> noticeValidationService.validateAuthorPermission(notice, authorId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("작성자 권한 검증 실패 - 작성자가 아닌 경우")
    void validateAuthorPermissionFailWhenNotAuthor() {
        //given
        String authorId = "authorId";
        String otherUserId = "otherUserId";
        Notice notice = createTestNoticeWithAuthor(authorId);

        //when & then
        assertThatThrownBy(() -> noticeValidationService.validateAuthorPermission(notice, otherUserId))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("작성자 권한 검증 실패 - null 공지사항")
    void validateAuthorPermissionFailWithNullNotice() {
        //given
        Notice notice = null;
        String userId = "userId";

        //when & then
        assertThatThrownBy(() -> noticeValidationService.validateAuthorPermission(notice, userId))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("긴 제목 검증 - 최대 길이 초과")
    void validateLongTitleExceedsMaxLength() {
        //given
        String longTitle = "a".repeat(201);
        String content = "공지사항 내용";
        NoticeType type = NoticeType.NOTICE;
        List<Country> targetCountries = List.of(Country.SOUTH_KOREA);

        //when & then
        assertThatThrownBy(() -> noticeValidationService.validateNoticeCreation(longTitle, content, type, targetCountries))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("긴 내용 검증 - 최대 길이 초과")
    void validateLongContentExceedsMaxLength() {
        //given
        String title = "공지사항 제목";
        String longContent = "a".repeat(5001);
        NoticeType type = NoticeType.NOTICE;
        List<Country> targetCountries = List.of(Country.SOUTH_KOREA);

        //when & then
        assertThatThrownBy(() -> noticeValidationService.validateNoticeCreation(title, longContent, type, targetCountries))
                .isInstanceOf(NoticeException.class);
    }

    @Test
    @DisplayName("모든 NoticeType 검증")
    void validateAllNoticeTypes() {
        //given
        String title = "공지사항 제목";
        String content = "공지사항 내용";
        List<Country> targetCountries = List.of(Country.SOUTH_KOREA);

        //when & then
        for (NoticeType type : NoticeType.values()) {
            assertThatCode(() -> noticeValidationService.validateNoticeCreation(title, content, type, targetCountries))
                    .doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("모든 국가 타겟팅 검증")
    void validateAllCountriesTargeting() {
        //given
        String title = "공지사항 제목";
        String content = "공지사항 내용";
        NoticeType type = NoticeType.NOTICE;
        List<Country> allCountries = List.of(Country.values());

        //when & then
        assertThatCode(() -> noticeValidationService.validateNoticeCreation(title, content, type, allCountries))
                .doesNotThrowAnyException();
    }

    private Notice createTestNotice() {
        return com.backend.immilog.notice.domain.model.Notice.create(
                com.backend.immilog.notice.domain.model.NoticeAuthor.of("authorId"),
                com.backend.immilog.notice.domain.model.NoticeTitle.of("테스트 제목"),
                com.backend.immilog.notice.domain.model.NoticeContent.of("테스트 내용"),
                NoticeType.NOTICE,
                com.backend.immilog.notice.domain.model.NoticeTargeting.of(List.of(Country.SOUTH_KOREA))
        );
    }

    private Notice createTestNoticeWithAuthor(String authorId) {
        return com.backend.immilog.notice.domain.model.Notice.create(
                com.backend.immilog.notice.domain.model.NoticeAuthor.of(authorId),
                com.backend.immilog.notice.domain.model.NoticeTitle.of("테스트 제목"),
                com.backend.immilog.notice.domain.model.NoticeContent.of("테스트 내용"),
                NoticeType.NOTICE,
                com.backend.immilog.notice.domain.model.NoticeTargeting.of(List.of(Country.SOUTH_KOREA))
        );
    }
}