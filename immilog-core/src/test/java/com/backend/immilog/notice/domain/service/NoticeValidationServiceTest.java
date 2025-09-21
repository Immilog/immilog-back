package com.backend.immilog.notice.domain.service;

import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.*;
import com.backend.immilog.notice.exception.NoticeErrorCode;
import com.backend.immilog.notice.exception.NoticeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("NoticeValidationService 도메인 서비스")
class NoticeValidationServiceTest {

    private NoticeValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new NoticeValidationService();
    }

    @Nested
    @DisplayName("공지사항 생성 검증")
    class ValidateNoticeCreation {

        @Test
        @DisplayName("정상적인 공지사항 생성 검증")
        void validateValidNoticeCreation() {
            assertThatNoException().isThrownBy(() ->
                    validationService.validateNoticeCreation(
                            "유효한 제목",
                            "유효한 내용",
                            NoticeType.NOTICE,
                            List.of("KR", "US")
                    )
            );
        }

        @Test
        @DisplayName("null 제목으로 생성 검증 시 예외 발생")
        void validateCreationWithNullTitle() {
            assertThatThrownBy(() ->
                    validationService.validateNoticeCreation(
                            null,
                            "유효한 내용",
                            NoticeType.NOTICE,
                            List.of("KR")
                    )
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_NOTICE_TITLE);
        }

        @Test
        @DisplayName("빈 제목으로 생성 검증 시 예외 발생")
        void validateCreationWithEmptyTitle() {
            assertThatThrownBy(() ->
                    validationService.validateNoticeCreation(
                            "",
                            "유효한 내용",
                            NoticeType.NOTICE,
                            List.of("KR")
                    )
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_NOTICE_TITLE);
        }

        @Test
        @DisplayName("null 내용으로 생성 검증 시 예외 발생")
        void validateCreationWithNullContent() {
            assertThatThrownBy(() ->
                    validationService.validateNoticeCreation(
                            "유효한 제목",
                            null,
                            NoticeType.NOTICE,
                            List.of("KR")
                    )
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_NOTICE_CONTENT);
        }

        @Test
        @DisplayName("빈 내용으로 생성 검증 시 예외 발생")
        void validateCreationWithEmptyContent() {
            assertThatThrownBy(() ->
                    validationService.validateNoticeCreation(
                            "유효한 제목",
                            "",
                            NoticeType.NOTICE,
                            List.of("KR")
                    )
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_NOTICE_CONTENT);
        }

        @Test
        @DisplayName("null 타입으로 생성 검증 시 예외 발생")
        void validateCreationWithNullType() {
            assertThatThrownBy(() ->
                    validationService.validateNoticeCreation(
                            "유효한 제목",
                            "유효한 내용",
                            null,
                            List.of("KR")
                    )
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_NOTICE_TYPE);
        }

        @Test
        @DisplayName("null 타겟 국가로 생성 검증 시 예외 발생")
        void validateCreationWithNullTargetCountries() {
            assertThatThrownBy(() ->
                    validationService.validateNoticeCreation(
                            "유효한 제목",
                            "유효한 내용",
                            NoticeType.NOTICE,
                            null
                    )
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_NOTICE_TARGET_COUNTRIES);
        }

        @Test
        @DisplayName("빈 타겟 국가 리스트로 생성 검증 시 예외 발생")
        void validateCreationWithEmptyTargetCountries() {
            assertThatThrownBy(() ->
                    validationService.validateNoticeCreation(
                            "유효한 제목",
                            "유효한 내용",
                            NoticeType.NOTICE,
                            List.of()
                    )
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_NOTICE_TARGET_COUNTRIES);
        }
    }

    @Nested
    @DisplayName("공지사항 수정 검증")
    class ValidateNoticeUpdate {

        @Test
        @DisplayName("정상적인 공지사항 수정 검증")
        void validateValidNoticeUpdate() {
            var notice = createTestNotice();

            assertThatNoException().isThrownBy(() ->
                    validationService.validateNoticeUpdate(
                            notice,
                            "새로운 제목",
                            "새로운 내용"
                    )
            );
        }

        @Test
        @DisplayName("제목만 수정하는 경우")
        void validateUpdateTitleOnly() {
            var notice = createTestNotice();

            assertThatNoException().isThrownBy(() ->
                    validationService.validateNoticeUpdate(
                            notice,
                            "새로운 제목",
                            null
                    )
            );
        }

        @Test
        @DisplayName("내용만 수정하는 경우")
        void validateUpdateContentOnly() {
            var notice = createTestNotice();

            assertThatNoException().isThrownBy(() ->
                    validationService.validateNoticeUpdate(
                            notice,
                            null,
                            "새로운 내용"
                    )
            );
        }

        @Test
        @DisplayName("null 공지사항 수정 검증 시 예외 발생")
        void validateUpdateWithNullNotice() {
            assertThatThrownBy(() ->
                    validationService.validateNoticeUpdate(
                            null,
                            "새로운 제목",
                            "새로운 내용"
                    )
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.NOTICE_NOT_FOUND);
        }

        @Test
        @DisplayName("삭제된 공지사항 수정 검증 시 예외 발생")
        void validateUpdateDeletedNotice() {
            var notice = createTestNotice().delete();

            assertThatThrownBy(() ->
                    validationService.validateNoticeUpdate(
                            notice,
                            "새로운 제목",
                            "새로운 내용"
                    )
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.NOTICE_ALREADY_DELETED);
        }

        @Test
        @DisplayName("유효하지 않은 제목으로 수정 검증 시 예외 발생")
        void validateUpdateWithInvalidTitle() {
            var notice = createTestNotice();

            assertThatThrownBy(() ->
                    validationService.validateNoticeUpdate(
                            notice,
                            "",
                            "새로운 내용"
                    )
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_NOTICE_TITLE);
        }

        @Test
        @DisplayName("유효하지 않은 내용으로 수정 검증 시 예외 발생")
        void validateUpdateWithInvalidContent() {
            var notice = createTestNotice();

            assertThatThrownBy(() ->
                    validationService.validateNoticeUpdate(
                            notice,
                            "새로운 제목",
                            ""
                    )
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_NOTICE_CONTENT);
        }
    }

    @Nested
    @DisplayName("공지사항 접근 검증")
    class ValidateNoticeAccess {

        @Test
        @DisplayName("정상적인 공지사항 접근 검증")
        void validateValidNoticeAccess() {
            var notice = createTestNotice();

            assertThatNoException().isThrownBy(() ->
                    validationService.validateNoticeAccess(notice, "user123")
            );
        }

        @Test
        @DisplayName("null 공지사항 접근 검증 시 예외 발생")
        void validateAccessWithNullNotice() {
            assertThatThrownBy(() ->
                    validationService.validateNoticeAccess(null, "user123")
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.NOTICE_NOT_FOUND);
        }

        @Test
        @DisplayName("null 사용자로 접근 검증 시 예외 발생")
        void validateAccessWithNullUser() {
            var notice = createTestNotice();

            assertThatThrownBy(() ->
                    validationService.validateNoticeAccess(notice, null)
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_USER_SEQ);
        }

        @Test
        @DisplayName("빈 사용자 ID로 접근 검증 시 예외 발생")
        void validateAccessWithBlankUser() {
            var notice = createTestNotice();

            assertThatThrownBy(() ->
                    validationService.validateNoticeAccess(notice, "")
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_USER_SEQ);

            assertThatThrownBy(() ->
                    validationService.validateNoticeAccess(notice, "   ")
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_USER_SEQ);
        }
    }

    @Nested
    @DisplayName("작성자 권한 검증")
    class ValidateAuthorPermission {

        @Test
        @DisplayName("정상적인 작성자 권한 검증")
        void validateValidAuthorPermission() {
            var notice = createTestNotice();

            assertThatNoException().isThrownBy(() ->
                    validationService.validateAuthorPermission(notice, "user123")
            );
        }

        @Test
        @DisplayName("작성자가 아닌 사용자 권한 검증 시 예외 발생")
        void validateNonAuthorPermission() {
            var notice = createTestNotice();

            assertThatThrownBy(() ->
                    validationService.validateAuthorPermission(notice, "otherUser")
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.NOT_AN_ADMIN_USER);
        }

        @Test
        @DisplayName("null 공지사항으로 작성자 권한 검증 시 예외 발생")
        void validateAuthorPermissionWithNullNotice() {
            assertThatThrownBy(() ->
                    validationService.validateAuthorPermission(null, "user123")
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.NOTICE_NOT_FOUND);
        }

        @Test
        @DisplayName("null 사용자로 작성자 권한 검증 시 예외 발생")
        void validateAuthorPermissionWithNullUser() {
            var notice = createTestNotice();

            assertThatThrownBy(() ->
                    validationService.validateAuthorPermission(notice, null)
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_USER_SEQ);
        }
    }

    private Notice createTestNotice() {
        var author = NoticeAuthor.of("user123");
        var title = NoticeTitle.of("테스트 공지사항");
        var content = NoticeContent.of("테스트 내용");
        var targeting = NoticeTargeting.of(List.of("KR"));

        return Notice.create(author, title, content, NoticeType.NOTICE, targeting);
    }
}