package com.backend.immilog.notice.application.service;

import com.backend.immilog.notice.application.dto.NoticeModelResult;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.*;
import com.backend.immilog.notice.domain.repository.NoticeRepository;
import com.backend.immilog.notice.domain.service.NoticeAuthorizationService;
import com.backend.immilog.notice.domain.service.NoticeFactory;
import com.backend.immilog.notice.domain.service.NoticeValidationService;
import com.backend.immilog.notice.exception.NoticeErrorCode;
import com.backend.immilog.notice.exception.NoticeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("NoticeService 애플리케이션 서비스")
class NoticeServiceTest {

    private final NoticeRepository noticeRepository = mock(NoticeRepository.class);

    private final NoticeAuthorizationService authorizationService = mock(NoticeAuthorizationService.class);

    private final NoticeValidationService validationService = mock(NoticeValidationService.class);

    private final NoticeFactory noticeFactory = mock(NoticeFactory.class);

    private NoticeService noticeService;

    @BeforeEach
    void setUp() {
        noticeService = new NoticeService(
                noticeRepository,
                authorizationService,
                validationService,
                noticeFactory
        );
    }

    @Nested
    @DisplayName("공지사항 생성")
    class CreateNotice {

        @Test
        @DisplayName("정상적인 공지사항 생성")
        void createValidNotice() {
            var token = "valid-token";
            var title = "공지사항 제목";
            var content = "공지사항 내용";
            var type = NoticeType.NOTICE;
            var targetCountries = List.of("KR", "US");

            var author = NoticeAuthor.of("user123");
            var notice = createTestNotice();
            var savedNotice = createTestNoticeWithId();

            when(authorizationService.validateAndGetAuthor(token)).thenReturn(author);
            when(noticeFactory.createNotice("user123", title, content, type, targetCountries))
                    .thenReturn(notice);
            when(noticeRepository.save(notice)).thenReturn(savedNotice);
            doNothing().when(validationService).validateNoticeCreation(title, content, type, targetCountries);

            var result = noticeService.createNotice(token, title, content, type, targetCountries);

            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo("notice-id-123");
            verify(authorizationService).validateAndGetAuthor(token);
            verify(validationService).validateNoticeCreation(title, content, type, targetCountries);
            verify(noticeFactory).createNotice("user123", title, content, type, targetCountries);
            verify(noticeRepository).save(notice);
        }

        @Test
        @DisplayName("인증 실패 시 예외 발생")
        void createNoticeWithInvalidToken() {
            var token = "invalid-token";

            when(authorizationService.validateAndGetAuthor(token))
                    .thenThrow(new NoticeException(NoticeErrorCode.NOT_AN_ADMIN_USER));

            assertThatThrownBy(() ->
                    noticeService.createNotice(token, "제목", "내용", NoticeType.NOTICE, List.of("KR"))
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.NOT_AN_ADMIN_USER);

            verify(authorizationService).validateAndGetAuthor(token);
            verifyNoInteractions(validationService, noticeFactory, noticeRepository);
        }

        @Test
        @DisplayName("검증 실패 시 예외 발생")
        void createNoticeWithValidationFailure() {
            var token = "valid-token";
            var author = NoticeAuthor.of("user123");

            when(authorizationService.validateAndGetAuthor(token)).thenReturn(author);
            doThrow(new NoticeException(NoticeErrorCode.INVALID_NOTICE_TITLE))
                    .when(validationService).validateNoticeCreation(any(), any(), any(), any());

            assertThatThrownBy(() ->
                    noticeService.createNotice(token, "", "내용", NoticeType.NOTICE, List.of("KR"))
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.INVALID_NOTICE_TITLE);

            verify(authorizationService).validateAndGetAuthor(token);
            verify(validationService).validateNoticeCreation(any(), any(), any(), any());
            verifyNoInteractions(noticeFactory, noticeRepository);
        }
    }

    @Nested
    @DisplayName("공지사항 수정")
    class UpdateNotice {

        @Test
        @DisplayName("정상적인 공지사항 수정")
        void updateValidNotice() {
            var token = "valid-token";
            var noticeId = NoticeId.of("notice123");
            var title = "새로운 제목";
            var content = "새로운 내용";
            var type = NoticeType.NOTICE;

            var notice = createTestNotice();
            var updatedNotice = createTestNotice();

            when(noticeRepository.findById(noticeId.value())).thenReturn(Optional.of(notice));
            when(noticeFactory.updateNoticeContent(notice, title, content, type))
                    .thenReturn(updatedNotice);
            when(noticeRepository.save(updatedNotice)).thenReturn(updatedNotice);
            doNothing().when(authorizationService).validateNoticeModificationAccess(notice, token);
            doNothing().when(validationService).validateNoticeUpdate(notice, title, content);

            assertThatNoException().isThrownBy(() ->
                    noticeService.updateNotice(token, noticeId, title, content, type)
            );

            verify(noticeRepository).findById(noticeId.value());
            verify(authorizationService).validateNoticeModificationAccess(notice, token);
            verify(validationService).validateNoticeUpdate(notice, title, content);
            verify(noticeFactory).updateNoticeContent(notice, title, content, type);
            verify(noticeRepository).save(updatedNotice);
        }

        @Test
        @DisplayName("존재하지 않는 공지사항 수정 시 예외 발생")
        void updateNonExistentNotice() {
            var token = "valid-token";
            var noticeId = NoticeId.of("nonexistent");

            when(noticeRepository.findById(noticeId.value())).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    noticeService.updateNotice(token, noticeId, "제목", "내용", NoticeType.NOTICE)
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.NOTICE_NOT_FOUND);

            verify(noticeRepository).findById(noticeId.value());
            verifyNoInteractions(authorizationService, validationService, noticeFactory);
        }

        @Test
        @DisplayName("권한 없는 사용자가 수정 시 예외 발생")
        void updateNoticeWithoutPermission() {
            var token = "invalid-token";
            var noticeId = NoticeId.of("notice123");
            var notice = createTestNotice();

            when(noticeRepository.findById(noticeId.value())).thenReturn(Optional.of(notice));
            doThrow(new NoticeException(NoticeErrorCode.NOT_AN_ADMIN_USER))
                    .when(authorizationService).validateNoticeModificationAccess(notice, token);

            assertThatThrownBy(() ->
                    noticeService.updateNotice(token, noticeId, "제목", "내용", NoticeType.NOTICE)
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.NOT_AN_ADMIN_USER);

            verify(authorizationService).validateNoticeModificationAccess(notice, token);
            verifyNoInteractions(validationService, noticeFactory);
        }
    }

    @Nested
    @DisplayName("공지사항 삭제")
    class DeleteNotice {

        @Test
        @DisplayName("정상적인 공지사항 삭제")
        void deleteValidNotice() {
            var token = "valid-token";
            var noticeId = NoticeId.of("notice123");
            var notice = createTestNotice(); // 삭제되지 않은 상태의 공지사항

            when(noticeRepository.findById(noticeId.value())).thenReturn(Optional.of(notice));
            when(noticeRepository.save(any(Notice.class))).thenAnswer(invocation -> invocation.getArgument(0));
            doNothing().when(authorizationService).validateNoticeModificationAccess(notice, token);

            assertThatNoException().isThrownBy(() ->
                    noticeService.deleteNotice(token, noticeId)
            );

            verify(noticeRepository).findById(noticeId.value());
            verify(authorizationService).validateNoticeModificationAccess(notice, token);
            verify(noticeRepository).save(any(Notice.class));
        }

        @Test
        @DisplayName("존재하지 않는 공지사항 삭제 시 예외 발생")
        void deleteNonExistentNotice() {
            var token = "valid-token";
            var noticeId = NoticeId.of("nonexistent");

            when(noticeRepository.findById(noticeId.value())).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    noticeService.deleteNotice(token, noticeId)
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.NOTICE_NOT_FOUND);

            verify(noticeRepository).findById(noticeId.value());
            verifyNoInteractions(authorizationService);
        }
    }

    @Nested
    @DisplayName("읽음 처리")
    class MarkAsRead {

        @Test
        @DisplayName("정상적인 읽음 처리")
        void markAsReadValid() {
            var noticeId = NoticeId.of("notice123");
            var userId = "user456";
            var userCountryId = "KR";

            var notice = createTestNotice();
            var readNotice = notice.markAsRead(userId);

            when(noticeRepository.findById(noticeId.value())).thenReturn(Optional.of(notice));
            when(noticeRepository.save(any(Notice.class))).thenReturn(readNotice);
            doNothing().when(authorizationService).validateNoticeReadAccess(notice, userId, userCountryId);

            assertThatNoException().isThrownBy(() ->
                    noticeService.markAsRead(noticeId, userId, userCountryId)
            );

            verify(noticeRepository).findById(noticeId.value());
            verify(authorizationService).validateNoticeReadAccess(notice, userId, userCountryId);
            verify(noticeRepository).save(any(Notice.class));
        }

        @Test
        @DisplayName("존재하지 않는 공지사항 읽음 처리 시 예외 발생")
        void markAsReadNonExistentNotice() {
            var noticeId = NoticeId.of("nonexistent");
            var userId = "user456";
            var userCountryId = "KR";

            when(noticeRepository.findById(noticeId.value())).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    noticeService.markAsRead(noticeId, userId, userCountryId)
            )
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.NOTICE_NOT_FOUND);

            verify(noticeRepository).findById(noticeId.value());
            verifyNoInteractions(authorizationService);
        }
    }

    @Nested
    @DisplayName("공지사항 조회")
    class GetNotices {

        @Test
        @DisplayName("페이지네이션으로 공지사항 조회")
        void getNoticesWithPagination() {
            var userId = "user123";
            var pageable = PageRequest.of(0, 20);
            var mockResults = List.of(mock(NoticeModelResult.class));
            var mockPage = new PageImpl<>(mockResults, pageable, 1);

            when(noticeRepository.getNotices(userId, pageable)).thenReturn(mockPage);

            var result = noticeService.getNotices(userId, pageable);

            assertThat(result).isEqualTo(mockPage);
            assertThat(result.getContent()).hasSize(1);
            verify(noticeRepository).getNotices(userId, pageable);
        }

        @Test
        @DisplayName("ID로 공지사항 조회")
        void getNoticeById() {
            var noticeId = NoticeId.of("notice123");
            var notice = createTestNotice();

            when(noticeRepository.findById(noticeId.value())).thenReturn(Optional.of(notice));

            var result = noticeService.getById(noticeId);

            assertThat(result).isEqualTo(notice);
            verify(noticeRepository).findById(noticeId.value());
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외 발생")
        void getNoticeByNonExistentId() {
            var noticeId = NoticeId.of("nonexistent");

            when(noticeRepository.findById(noticeId.value())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> noticeService.getById(noticeId))
                    .isInstanceOf(NoticeException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NoticeErrorCode.NOTICE_NOT_FOUND);

            verify(noticeRepository).findById(noticeId.value());
        }

        @Test
        @DisplayName("문자열 ID로 공지사항 조회")
        void getNoticeByStringId() {
            var noticeId = "notice123";
            var notice = createTestNotice();

            when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(notice));

            var result = noticeService.getNoticeById(noticeId);

            assertThat(result).isEqualTo(notice);
            verify(noticeRepository).findById(noticeId);
        }

        @Test
        @DisplayName("국가별 공지사항 조회")
        void getNoticesForCountry() {
            var countryId = "KR";
            var notices = List.of(createTestNotice(), createTestNotice());

            when(noticeRepository.findActiveNoticesForCountryId(countryId)).thenReturn(notices);

            var result = noticeService.getNoticesForCountry(countryId);

            assertThat(result).hasSize(2);
            verify(noticeRepository).findActiveNoticesForCountryId(countryId);
        }

        @Test
        @DisplayName("모든 활성 공지사항 조회")
        void getAllActiveNotices() {
            var notices = List.of(createTestNotice(), createTestNotice());

            when(noticeRepository.findAllActiveNotices()).thenReturn(notices);

            var result = noticeService.getAllActiveNotices();

            assertThat(result).hasSize(2);
            verify(noticeRepository).findAllActiveNotices();
        }

        @Test
        @DisplayName("타입별 공지사항 조회")
        void getNoticesByType() {
            var type = NoticeType.NOTICE;
            var notices = List.of(createTestNotice());

            when(noticeRepository.findByType(type)).thenReturn(notices);

            var result = noticeService.getNoticesByType(type);

            assertThat(result).hasSize(1);
            verify(noticeRepository).findByType(type);
        }

        @Test
        @DisplayName("작성자별 공지사항 조회")
        void getNoticesByAuthor() {
            var authorUserId = "user123";
            var notices = List.of(createTestNotice());

            when(noticeRepository.findByAuthorUserId(authorUserId)).thenReturn(notices);

            var result = noticeService.getNoticesByAuthor(authorUserId);

            assertThat(result).hasSize(1);
            verify(noticeRepository).findByAuthorUserId(authorUserId);
        }
    }

    @Nested
    @DisplayName("공지사항 상태 확인")
    class CheckNoticeStatus {

        @Test
        @DisplayName("읽음 상태 확인")
        void isNoticeReadBy() {
            var noticeId = NoticeId.of("notice123");
            var userId = "user456";
            var notice = createTestNotice();
            var readNotice = notice.markAsRead(userId);

            when(noticeRepository.findById(noticeId.value())).thenReturn(Optional.of(readNotice));

            var result = noticeService.isNoticeReadBy(noticeId, userId);

            assertThat(result).isTrue();
            verify(noticeRepository).findById(noticeId.value());
        }

        @Test
        @DisplayName("공지사항 존재 여부 확인")
        void existsById() {
            var noticeId = NoticeId.of("notice123");

            when(noticeRepository.existsById(noticeId.value())).thenReturn(true);

            var result = noticeService.existsById(noticeId);

            assertThat(result).isTrue();
            verify(noticeRepository).existsById(noticeId.value());
        }

        @Test
        @DisplayName("안읽은 공지사항 존재 여부 확인")
        void areUnreadNoticesExist() {
            var countryId = "KR";
            var userId = "user123";

            when(noticeRepository.areUnreadNoticesExist(countryId, userId)).thenReturn(true);

            var result = noticeService.areUnreadNoticesExist(countryId, userId);

            assertThat(result).isTrue();
            verify(noticeRepository).areUnreadNoticesExist(countryId, userId);
        }
    }

    @Nested
    @DisplayName("공지사항 삭제 작업")
    class DeleteOperations {

        @Test
        @DisplayName("공지사항 엔티티 삭제")
        void deleteNoticeEntity() {
            var notice = createTestNotice();

            noticeService.delete(notice);

            verify(noticeRepository).delete(notice);
        }

        @Test
        @DisplayName("ID로 공지사항 삭제")
        void deleteNoticeById() {
            var noticeId = "notice123";

            noticeService.deleteById(noticeId);

            verify(noticeRepository).deleteById(noticeId);
        }
    }

    private Notice createTestNotice() {
        return Notice.create(
                NoticeAuthor.of("user123"),
                NoticeTitle.of("테스트 공지사항"),
                NoticeContent.of("테스트 내용"),
                NoticeType.NOTICE,
                NoticeTargeting.of(List.of("KR"))
        );
    }
    
    private Notice createTestNoticeWithId() {
        var notice = createTestNotice();
        // 리플렉션을 사용하여 ID 설정
        try {
            var idField = Notice.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(notice, NoticeId.of("notice-id-123"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return notice;
    }
}