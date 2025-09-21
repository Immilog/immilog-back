package com.backend.immilog.notice.domain.service;

import com.backend.immilog.notice.domain.enums.NoticeStatus;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("NoticeFactory 도메인 서비스")
class NoticeFactoryTest {

    private NoticeFactory noticeFactory;

    @BeforeEach
    void setUp() {
        noticeFactory = new NoticeFactory();
    }

    @Nested
    @DisplayName("공지사항 생성")
    class CreateNotice {

        @Test
        @DisplayName("정상적인 공지사항 생성")
        void createValidNotice() {
            var notice = noticeFactory.createNotice(
                    "user123",
                    "공지사항 제목",
                    "공지사항 내용",
                    NoticeType.NOTICE,
                    List.of("KR", "US")
            );

            assertThat(notice).isNotNull();
            assertThat(notice.getAuthorUserId()).isEqualTo("user123");
            assertThat(notice.getTitleValue()).isEqualTo("공지사항 제목");
            assertThat(notice.getContentValue()).isEqualTo("공지사항 내용");
            assertThat(notice.getType()).isEqualTo(NoticeType.NOTICE);
            assertThat(notice.getStatus()).isEqualTo(NoticeStatus.NORMAL);
            assertThat(notice.getTargetCountries()).containsExactly("KR", "US");
            assertThat(notice.isActive()).isTrue();
        }

        @Test
        @DisplayName("긴급 공지사항 생성")
        void createUrgentNotice() {
            var notice = noticeFactory.createNotice(
                    "admin",
                    "긴급 공지",
                    "긴급 내용",
                    NoticeType.NOTICE,
                    List.of("ALL")
            );

            assertThat(notice.getType()).isEqualTo(NoticeType.NOTICE);
            assertThat(notice.getTargetCountries()).containsExactly("ALL");
        }

        @Test
        @DisplayName("여러 국가 대상 공지사항 생성")
        void createMultiCountryNotice() {
            var notice = noticeFactory.createNotice(
                    "user123",
                    "다국가 공지",
                    "다국가 내용",
                    NoticeType.NOTICE,
                    List.of("KR", "US", "JP", "CN")
            );

            assertThat(notice.getTargetCountries()).contains("KR", "US", "JP", "CN");
        }
    }

    @Nested
    @DisplayName("글로벌 공지사항 생성")
    class CreateGlobalNotice {

        @Test
        @DisplayName("정상적인 글로벌 공지사항 생성")
        void createValidGlobalNotice() {
            var notice = noticeFactory.createGlobalNotice(
                    "admin",
                    "글로벌 공지",
                    "모든 사용자에게",
                    NoticeType.NOTICE
            );

            assertThat(notice).isNotNull();
            assertThat(notice.getAuthorUserId()).isEqualTo("admin");
            assertThat(notice.getTitleValue()).isEqualTo("글로벌 공지");
            assertThat(notice.getContentValue()).isEqualTo("모든 사용자에게");
            assertThat(notice.getType()).isEqualTo(NoticeType.NOTICE);
            assertThat(notice.getStatus()).isEqualTo(NoticeStatus.NORMAL);
        }

        @Test
        @DisplayName("시스템 공지사항 글로벌 생성")
        void createSystemGlobalNotice() {
            var notice = noticeFactory.createGlobalNotice(
                    "system",
                    "시스템 점검 공지",
                    "시스템 점검이 예정되어 있습니다",
                    NoticeType.NOTICE
            );

            assertThat(notice.getType()).isEqualTo(NoticeType.NOTICE);
            assertThat(notice.getAuthorUserId()).isEqualTo("system");
        }
    }

    @Nested
    @DisplayName("공지사항 내용 수정")
    class UpdateNoticeContent {

        @Test
        @DisplayName("제목만 수정")
        void updateTitleOnly() {
            var originalNotice = createTestNotice();
            var originalTitle = originalNotice.getTitleValue();
            var originalContent = originalNotice.getContentValue();
            var originalType = originalNotice.getType();

            var updatedNotice = noticeFactory.updateNoticeContent(
                    originalNotice,
                    "새로운 제목",
                    null,
                    null
            );

            assertThat(updatedNotice.getTitleValue()).isEqualTo("새로운 제목");
            assertThat(updatedNotice.getContentValue()).isEqualTo(originalContent);
            assertThat(updatedNotice.getType()).isEqualTo(originalType);
        }

        @Test
        @DisplayName("내용만 수정")
        void updateContentOnly() {
            var originalNotice = createTestNotice();
            var originalTitle = originalNotice.getTitleValue();
            var originalType = originalNotice.getType();

            var updatedNotice = noticeFactory.updateNoticeContent(
                    originalNotice,
                    null,
                    "새로운 내용",
                    null
            );

            assertThat(updatedNotice.getTitleValue()).isEqualTo(originalTitle);
            assertThat(updatedNotice.getContentValue()).isEqualTo("새로운 내용");
            assertThat(updatedNotice.getType()).isEqualTo(originalType);
        }

        @Test
        @DisplayName("타입만 수정")
        void updateTypeOnly() {
            var originalNotice = createTestNotice();
            var originalTitle = originalNotice.getTitleValue();
            var originalContent = originalNotice.getContentValue();

            var updatedNotice = noticeFactory.updateNoticeContent(
                    originalNotice,
                    null,
                    null,
                    NoticeType.NOTICE
            );

            assertThat(updatedNotice.getTitleValue()).isEqualTo(originalTitle);
            assertThat(updatedNotice.getContentValue()).isEqualTo(originalContent);
            assertThat(updatedNotice.getType()).isEqualTo(NoticeType.NOTICE);
        }

        @Test
        @DisplayName("모든 필드 수정")
        void updateAllFields() {
            var originalNotice = createTestNotice();

            var updatedNotice = noticeFactory.updateNoticeContent(
                    originalNotice,
                    "완전히 새로운 제목",
                    "완전히 새로운 내용",
                    NoticeType.NOTICE
            );

            assertThat(updatedNotice.getTitleValue()).isEqualTo("완전히 새로운 제목");
            assertThat(updatedNotice.getContentValue()).isEqualTo("완전히 새로운 내용");
            assertThat(updatedNotice.getType()).isEqualTo(NoticeType.NOTICE);
        }

        @Test
        @DisplayName("빈 문자열로 수정 시 변경되지 않음")
        void updateWithEmptyStrings() {
            var originalNotice = createTestNotice();
            var originalTitle = originalNotice.getTitleValue();
            var originalContent = originalNotice.getContentValue();
            var originalType = originalNotice.getType();

            var updatedNotice = noticeFactory.updateNoticeContent(
                    originalNotice,
                    "",
                    "   ",
                    null
            );

            assertThat(updatedNotice.getTitleValue()).isEqualTo(originalTitle);
            assertThat(updatedNotice.getContentValue()).isEqualTo(originalContent);
            assertThat(updatedNotice.getType()).isEqualTo(originalType);
        }

        @Test
        @DisplayName("공백 문자열로 수정 시 변경되지 않음")
        void updateWithWhitespaceStrings() {
            var originalNotice = createTestNotice();
            var originalTitle = originalNotice.getTitleValue();
            var originalContent = originalNotice.getContentValue();

            var updatedNotice = noticeFactory.updateNoticeContent(
                    originalNotice,
                    "   ",
                    "\t\n",
                    null
            );

            assertThat(updatedNotice.getTitleValue()).isEqualTo(originalTitle);
            assertThat(updatedNotice.getContentValue()).isEqualTo(originalContent);
        }

        @Test
        @DisplayName("null 값으로 수정 시 변경되지 않음")
        void updateWithNullValues() {
            var originalNotice = createTestNotice();
            var originalTitle = originalNotice.getTitleValue();
            var originalContent = originalNotice.getContentValue();
            var originalType = originalNotice.getType();

            var updatedNotice = noticeFactory.updateNoticeContent(
                    originalNotice,
                    null,
                    null,
                    null
            );

            assertThat(updatedNotice.getTitleValue()).isEqualTo(originalTitle);
            assertThat(updatedNotice.getContentValue()).isEqualTo(originalContent);
            assertThat(updatedNotice.getType()).isEqualTo(originalType);
        }
    }

    private Notice createTestNotice() {
        return noticeFactory.createNotice(
                "testUser",
                "테스트 제목",
                "테스트 내용",
                NoticeType.NOTICE,
                List.of("KR")
        );
    }
}