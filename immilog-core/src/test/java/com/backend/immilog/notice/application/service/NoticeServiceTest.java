package com.backend.immilog.notice.application.service;

import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.model.NoticeId;
import com.backend.immilog.notice.domain.service.NoticeAuthorizationService;
import com.backend.immilog.notice.domain.service.NoticeFactory;
import com.backend.immilog.notice.domain.service.NoticeValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NoticeServiceTest {

    private final NoticeQueryService mockNoticeQueryService = mock(NoticeQueryService.class);
    private final NoticeCommandService mockNoticeCommandService = mock(NoticeCommandService.class);
    private final NoticeAuthorizationService mockAuthorizationService = mock(NoticeAuthorizationService.class);
    private final NoticeValidationService mockValidationService = mock(NoticeValidationService.class);
    private final NoticeFactory mockNoticeFactory = mock(NoticeFactory.class);

    private NoticeService noticeService;

    @BeforeEach
    void setUp() {
        noticeService = new NoticeService(
                mockNoticeQueryService,
                mockNoticeCommandService,
                mockAuthorizationService,
                mockValidationService,
                mockNoticeFactory
        );
    }

    @Test
    @DisplayName("공지사항 생성 - 정상 케이스")
    void createNoticeSuccessfully() {
        //given
        String token = "adminToken";
        String title = "공지사항 제목";
        String content = "공지사항 내용";
        NoticeType type = NoticeType.NOTICE;
        List<String> targetCountries = List.of("KR");
        
        com.backend.immilog.notice.domain.model.NoticeAuthor author = com.backend.immilog.notice.domain.model.NoticeAuthor.of("authorId");
        Notice createdNotice = createTestNotice();
        Notice savedNotice = createTestNoticeWithId();
        
        when(mockAuthorizationService.validateAndGetAuthor(token)).thenReturn(author);
        when(mockNoticeFactory.createNotice(author.userId(), title, content, type, targetCountries))
                .thenReturn(createdNotice);
        when(mockNoticeCommandService.save(createdNotice)).thenReturn(savedNotice);

        //when
        NoticeId result = noticeService.createNotice(token, title, content, type, targetCountries);

        //then
        assertThat(result).isEqualTo(savedNotice.getId());
        verify(mockAuthorizationService).validateAndGetAuthor(token);
        verify(mockValidationService).validateNoticeCreation(title, content, type, targetCountries);
        verify(mockNoticeFactory).createNotice(author.userId(), title, content, type, targetCountries);
        verify(mockNoticeCommandService).save(createdNotice);
    }

    @Test
    @DisplayName("공지사항 업데이트 - 정상 케이스")
    void updateNoticeSuccessfully() {
        //given
        String token = "adminToken";
        NoticeId noticeId = NoticeId.of("noticeId");
        String title = "새로운 제목";
        String content = "새로운 내용";
        NoticeType type = NoticeType.EVENT;
        
        Notice existingNotice = createTestNoticeWithId();
        Notice updatedNotice = createTestNoticeWithId();
        
        when(mockNoticeQueryService.getById(noticeId)).thenReturn(existingNotice);
        when(mockNoticeFactory.updateNoticeContent(existingNotice, title, content, type))
                .thenReturn(updatedNotice);
        when(mockNoticeCommandService.save(updatedNotice)).thenReturn(updatedNotice);

        //when
        noticeService.updateNotice(token, noticeId, title, content, type);

        //then
        verify(mockNoticeQueryService).getById(noticeId);
        verify(mockAuthorizationService).validateNoticeModificationAccess(existingNotice, token);
        verify(mockValidationService).validateNoticeUpdate(existingNotice, title, content);
        verify(mockNoticeFactory).updateNoticeContent(existingNotice, title, content, type);
        verify(mockNoticeCommandService).save(updatedNotice);
    }

    @Test
    @DisplayName("공지사항 삭제 - 정상 케이스")
    void deleteNoticeSuccessfully() {
        //given
        String token = "adminToken";
        NoticeId noticeId = NoticeId.of("noticeId");
        
        Notice existingNotice = createTestNoticeWithId();
        
        when(mockNoticeQueryService.getById(noticeId)).thenReturn(existingNotice);
        when(mockNoticeCommandService.save(any(Notice.class))).thenReturn(existingNotice);

        //when
        noticeService.deleteNotice(token, noticeId);

        //then
        verify(mockNoticeQueryService).getById(noticeId);
        verify(mockAuthorizationService).validateNoticeModificationAccess(existingNotice, token);
        verify(mockNoticeCommandService).save(any(Notice.class));
    }

    @Test
    @DisplayName("공지사항 읽음 처리 - 정상 케이스")
    void markAsReadSuccessfully() {
        //given
        NoticeId noticeId = NoticeId.of("noticeId");
        String userId = "userId";
        String userCountry = "KR";
        
        Notice existingNotice = createTestNoticeWithId();
        
        when(mockNoticeQueryService.getById(noticeId)).thenReturn(existingNotice);
        when(mockNoticeCommandService.save(any(Notice.class))).thenReturn(existingNotice);

        //when
        noticeService.markAsRead(noticeId, userId, userCountry);

        //then
        verify(mockNoticeQueryService).getById(noticeId);
        verify(mockAuthorizationService).validateNoticeReadAccess(existingNotice, userId, userCountry);
        verify(mockNoticeCommandService).save(any(Notice.class));
    }

    @Test
    @DisplayName("ID로 공지사항 조회 - 정상 케이스")
    void getNoticeByIdSuccessfully() {
        //given
        NoticeId noticeId = NoticeId.of("noticeId");
        Notice expectedNotice = createTestNoticeWithId();
        
        when(mockNoticeQueryService.getById(noticeId)).thenReturn(expectedNotice);

        //when
        Notice result = noticeService.getNoticeById(noticeId);

        //then
        assertThat(result).isEqualTo(expectedNotice);
        verify(mockNoticeQueryService).getById(noticeId);
    }

    @Test
    @DisplayName("국가별 공지사항 조회 - 정상 케이스")
    void getNoticesForCountrySuccessfully() {
        //given
        String country = "KR";
        List<Notice> expectedNotices = List.of(createTestNotice(), createTestNotice());

        when(mockNoticeQueryService.getActiveNoticesForCountryId(country)).thenReturn(expectedNotices);

        //when
        List<Notice> result = noticeService.getNoticesForCountry(country);

        //then
        assertThat(result).isEqualTo(expectedNotices);
        verify(mockNoticeQueryService).getActiveNoticesForCountryId(country);
    }

    @Test
    @DisplayName("모든 활성 공지사항 조회 - 정상 케이스")
    void getAllActiveNoticesSuccessfully() {
        //given
        List<Notice> expectedNotices = List.of(createTestNotice(), createTestNotice());
        
        when(mockNoticeQueryService.getAllActiveNotices()).thenReturn(expectedNotices);

        //when
        List<Notice> result = noticeService.getAllActiveNotices();

        //then
        assertThat(result).isEqualTo(expectedNotices);
        verify(mockNoticeQueryService).getAllActiveNotices();
    }

    @Test
    @DisplayName("타입별 공지사항 조회 - 정상 케이스")
    void getNoticesByTypeSuccessfully() {
        //given
        NoticeType type = NoticeType.EVENT;
        List<Notice> expectedNotices = List.of(createTestNotice(), createTestNotice());
        
        when(mockNoticeQueryService.getNoticesByType(type)).thenReturn(expectedNotices);

        //when
        List<Notice> result = noticeService.getNoticesByType(type);

        //then
        assertThat(result).isEqualTo(expectedNotices);
        verify(mockNoticeQueryService).getNoticesByType(type);
    }

    @Test
    @DisplayName("공지사항 읽음 상태 확인 - 읽은 경우")
    void isNoticeReadByWhenRead() {
        //given
        NoticeId noticeId = NoticeId.of("noticeId");
        String userId = "userId";
        Notice mockNotice = mock(Notice.class);
        
        when(mockNoticeQueryService.getById(noticeId)).thenReturn(mockNotice);
        when(mockNotice.isReadBy(userId)).thenReturn(true);

        //when
        boolean result = noticeService.isNoticeReadBy(noticeId, userId);

        //then
        assertThat(result).isTrue();
        verify(mockNoticeQueryService).getById(noticeId);
    }

    @Test
    @DisplayName("공지사항 읽음 상태 확인 - 읽지 않은 경우")
    void isNoticeReadByWhenNotRead() {
        //given
        NoticeId noticeId = NoticeId.of("noticeId");
        String userId = "userId";
        Notice mockNotice = mock(Notice.class);
        
        when(mockNoticeQueryService.getById(noticeId)).thenReturn(mockNotice);
        when(mockNotice.isReadBy(userId)).thenReturn(false);

        //when
        boolean result = noticeService.isNoticeReadBy(noticeId, userId);

        //then
        assertThat(result).isFalse();
        verify(mockNoticeQueryService).getById(noticeId);
    }

    @Test
    @DisplayName("글로벌 공지사항 생성")
    void createGlobalNotice() {
        //given
        String token = "adminToken";
        String title = "글로벌 공지사항";
        String content = "전체 국가 대상 공지사항";
        NoticeType type = NoticeType.PROMOTION;
        List<String> allCountries = List.of("KR", "JP", "CN", "US", "UK", "DE", "FR", "CA", "AU", "SG", "MY", "TH", "VN", "PH", "IN", "BR", "MX", "AR", "CL", "CO");
        
        com.backend.immilog.notice.domain.model.NoticeAuthor author = com.backend.immilog.notice.domain.model.NoticeAuthor.of("authorId");
        Notice createdNotice = createTestNotice();
        Notice savedNotice = createTestNoticeWithId();
        
        when(mockAuthorizationService.validateAndGetAuthor(token)).thenReturn(author);
        when(mockNoticeFactory.createNotice(author.userId(), title, content, type, allCountries))
                .thenReturn(createdNotice);
        when(mockNoticeCommandService.save(createdNotice)).thenReturn(savedNotice);

        //when
        NoticeId result = noticeService.createNotice(token, title, content, type, allCountries);

        //then
        assertThat(result).isEqualTo(savedNotice.getId());
        verify(mockValidationService).validateNoticeCreation(title, content, type, allCountries);
    }

    @Test
    @DisplayName("부분 업데이트 - 제목만")
    void updateNoticePartiallyTitleOnly() {
        //given
        String token = "adminToken";
        NoticeId noticeId = NoticeId.of("noticeId");
        String newTitle = "새로운 제목";
        
        Notice existingNotice = createTestNoticeWithId();
        Notice updatedNotice = createTestNoticeWithId();
        
        when(mockNoticeQueryService.getById(noticeId)).thenReturn(existingNotice);
        when(mockNoticeFactory.updateNoticeContent(existingNotice, newTitle, null, null))
                .thenReturn(updatedNotice);
        when(mockNoticeCommandService.save(updatedNotice)).thenReturn(updatedNotice);

        //when
        noticeService.updateNotice(token, noticeId, newTitle, null, null);

        //then
        verify(mockValidationService).validateNoticeUpdate(existingNotice, newTitle, null);
        verify(mockNoticeFactory).updateNoticeContent(existingNotice, newTitle, null, null);
    }

    @Test
    @DisplayName("부분 업데이트 - 내용만")
    void updateNoticePartiallyContentOnly() {
        //given
        String token = "adminToken";
        NoticeId noticeId = NoticeId.of("noticeId");
        String newContent = "새로운 내용";
        
        Notice existingNotice = createTestNoticeWithId();
        Notice updatedNotice = createTestNoticeWithId();
        
        when(mockNoticeQueryService.getById(noticeId)).thenReturn(existingNotice);
        when(mockNoticeFactory.updateNoticeContent(existingNotice, null, newContent, null))
                .thenReturn(updatedNotice);
        when(mockNoticeCommandService.save(updatedNotice)).thenReturn(updatedNotice);

        //when
        noticeService.updateNotice(token, noticeId, null, newContent, null);

        //then
        verify(mockValidationService).validateNoticeUpdate(existingNotice, null, newContent);
        verify(mockNoticeFactory).updateNoticeContent(existingNotice, null, newContent, null);
    }

    @Test
    @DisplayName("부분 업데이트 - 타입만")
    void updateNoticePartiallyTypeOnly() {
        //given
        String token = "adminToken";
        NoticeId noticeId = NoticeId.of("noticeId");
        NoticeType newType = NoticeType.EVENT;
        
        Notice existingNotice = createTestNoticeWithId();
        Notice updatedNotice = createTestNoticeWithId();
        
        when(mockNoticeQueryService.getById(noticeId)).thenReturn(existingNotice);
        when(mockNoticeFactory.updateNoticeContent(existingNotice, null, null, newType))
                .thenReturn(updatedNotice);
        when(mockNoticeCommandService.save(updatedNotice)).thenReturn(updatedNotice);

        //when
        noticeService.updateNotice(token, noticeId, null, null, newType);

        //then
        verify(mockValidationService).validateNoticeUpdate(existingNotice, null, null);
        verify(mockNoticeFactory).updateNoticeContent(existingNotice, null, null, newType);
    }

    @Test
    @DisplayName("여러 국가 대상 공지사항 생성")
    void createNoticeForMultipleCountries() {
        //given
        String token = "adminToken";
        String title = "다국가 공지사항";
        String content = "여러 국가 대상 공지사항";
        NoticeType type = NoticeType.EVENT;
        List<String> targetCountries = List.of("KR", "JP", "CN");
        
        com.backend.immilog.notice.domain.model.NoticeAuthor author = com.backend.immilog.notice.domain.model.NoticeAuthor.of("authorId");
        Notice createdNotice = createTestNotice();
        Notice savedNotice = createTestNoticeWithId();
        
        when(mockAuthorizationService.validateAndGetAuthor(token)).thenReturn(author);
        when(mockNoticeFactory.createNotice(author.userId(), title, content, type, targetCountries))
                .thenReturn(createdNotice);
        when(mockNoticeCommandService.save(createdNotice)).thenReturn(savedNotice);

        //when
        NoticeId result = noticeService.createNotice(token, title, content, type, targetCountries);

        //then
        assertThat(result).isEqualTo(savedNotice.getId());
        verify(mockValidationService).validateNoticeCreation(title, content, type, targetCountries);
    }

    @Test
    @DisplayName("빈 결과 조회")
    void getEmptyResults() {
        //given
        String country = "KR";
        List<Notice> emptyList = List.of();

        when(mockNoticeQueryService.getActiveNoticesForCountryId(country)).thenReturn(emptyList);

        //when
        List<Notice> result = noticeService.getNoticesForCountry(country);

        //then
        assertThat(result).isEmpty();
        verify(mockNoticeQueryService).getActiveNoticesForCountryId(country);
    }

    private Notice createTestNotice() {
        return com.backend.immilog.notice.domain.model.Notice.create(
                com.backend.immilog.notice.domain.model.NoticeAuthor.of("authorId"),
                com.backend.immilog.notice.domain.model.NoticeTitle.of("테스트 제목"),
                com.backend.immilog.notice.domain.model.NoticeContent.of("테스트 내용"),
                NoticeType.NOTICE,
                com.backend.immilog.notice.domain.model.NoticeTargeting.of(List.of("KR"))
        );
    }

    private Notice createTestNoticeWithId() {
        return com.backend.immilog.notice.domain.model.Notice.restore(
                NoticeId.of("noticeId"),
                com.backend.immilog.notice.domain.model.NoticeAuthor.of("authorId"),
                com.backend.immilog.notice.domain.model.NoticeTitle.of("테스트 제목"),
                com.backend.immilog.notice.domain.model.NoticeContent.of("테스트 내용"),
                NoticeType.NOTICE,
                com.backend.immilog.notice.domain.enums.NoticeStatus.NORMAL,
                com.backend.immilog.notice.domain.model.NoticeTargeting.of(List.of("KR")),
                com.backend.immilog.notice.domain.model.NoticeReadStatus.empty(),
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );
    }
}