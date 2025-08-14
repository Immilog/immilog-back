package com.backend.immilog.notice.application.service;

import com.backend.immilog.notice.application.dto.NoticeModelResult;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.model.NoticeId;
import com.backend.immilog.notice.domain.repository.NoticeRepository;
import com.backend.immilog.notice.exception.NoticeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class NoticeQueryServiceTest {

    private final NoticeRepository mockNoticeRepository = mock(NoticeRepository.class);

    private NoticeQueryService noticeQueryService;

    @BeforeEach
    void setUp() {
        noticeQueryService = new NoticeQueryService(mockNoticeRepository);
    }

    @Test
    @DisplayName("페이지네이션 공지사항 조회 - 정상 케이스")
    void getNoticesWithPaginationSuccessfully() {
        //given
        String userId = "userId";
        Pageable pageable = PageRequest.of(0, 10);
        Page<NoticeModelResult> expectedPage = new PageImpl<>(List.of());
        
        when(mockNoticeRepository.getNotices(userId, pageable)).thenReturn(expectedPage);

        //when
        Page<NoticeModelResult> result = noticeQueryService.getNotices(userId, pageable);

        //then
        assertThat(result).isEqualTo(expectedPage);
        verify(mockNoticeRepository).getNotices(userId, pageable);
    }

    @Test
    @DisplayName("ID로 공지사항 조회 - 존재하는 경우")
    void getByIdWhenExists() {
        //given
        NoticeId noticeId = NoticeId.of("noticeId");
        Notice expectedNotice = createTestNoticeWithId();
        
        when(mockNoticeRepository.findById(noticeId.value())).thenReturn(Optional.of(expectedNotice));

        //when
        Notice result = noticeQueryService.getById(noticeId);

        //then
        assertThat(result).isEqualTo(expectedNotice);
        verify(mockNoticeRepository).findById(noticeId.value());
    }

    @Test
    @DisplayName("ID로 공지사항 조회 - 존재하지 않는 경우")
    void getByIdWhenNotExists() {
        //given
        NoticeId noticeId = NoticeId.of("nonExistentNoticeId");
        
        when(mockNoticeRepository.findById(noticeId.value())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> noticeQueryService.getById(noticeId))
                .isInstanceOf(NoticeException.class);
        verify(mockNoticeRepository).findById(noticeId.value());
    }

    @Test
    @DisplayName("국가별 활성 공지사항 조회 - 정상 케이스")
    void getActiveNoticesForCountryIdSuccessfully() {
        //given
        String countryId = "KR";
        List<Notice> expectedNotices = List.of(createTestNotice(), createTestNotice());

        when(mockNoticeRepository.findActiveNoticesForCountryId(countryId)).thenReturn(expectedNotices);

        //when
        List<Notice> result = noticeQueryService.getActiveNoticesForCountryId(countryId);

        //then
        assertThat(result).isEqualTo(expectedNotices);
        verify(mockNoticeRepository).findActiveNoticesForCountryId(countryId);
    }

    @Test
    @DisplayName("모든 활성 공지사항 조회 - 정상 케이스")
    void getAllActiveNoticesSuccessfully() {
        //given
        List<Notice> expectedNotices = List.of(createTestNotice(), createTestNotice());
        
        when(mockNoticeRepository.findAllActiveNotices()).thenReturn(expectedNotices);

        //when
        List<Notice> result = noticeQueryService.getAllActiveNotices();

        //then
        assertThat(result).isEqualTo(expectedNotices);
        verify(mockNoticeRepository).findAllActiveNotices();
    }

    @Test
    @DisplayName("타입별 공지사항 조회 - 정상 케이스")
    void getNoticesByTypeSuccessfully() {
        //given
        NoticeType type = NoticeType.EVENT;
        List<Notice> expectedNotices = List.of(createTestNotice(), createTestNotice());
        
        when(mockNoticeRepository.findByType(type)).thenReturn(expectedNotices);

        //when
        List<Notice> result = noticeQueryService.getNoticesByType(type);

        //then
        assertThat(result).isEqualTo(expectedNotices);
        verify(mockNoticeRepository).findByType(type);
    }

    @Test
    @DisplayName("작성자별 공지사항 조회 - 정상 케이스")
    void getNoticesByAuthorSuccessfully() {
        //given
        String authorUserId = "authorUserId";
        List<Notice> expectedNotices = List.of(createTestNotice(), createTestNotice());
        
        when(mockNoticeRepository.findByAuthorUserId(authorUserId)).thenReturn(expectedNotices);

        //when
        List<Notice> result = noticeQueryService.getNoticesByAuthor(authorUserId);

        //then
        assertThat(result).isEqualTo(expectedNotices);
        verify(mockNoticeRepository).findByAuthorUserId(authorUserId);
    }

    @Test
    @DisplayName("공지사항 존재 여부 확인 - 존재하는 경우")
    void existsByIdWhenExists() {
        //given
        NoticeId noticeId = NoticeId.of("noticeId");
        
        when(mockNoticeRepository.existsById(noticeId.value())).thenReturn(true);

        //when
        boolean result = noticeQueryService.existsById(noticeId);

        //then
        assertThat(result).isTrue();
        verify(mockNoticeRepository).existsById(noticeId.value());
    }

    @Test
    @DisplayName("공지사항 존재 여부 확인 - 존재하지 않는 경우")
    void existsByIdWhenNotExists() {
        //given
        NoticeId noticeId = NoticeId.of("nonExistentNoticeId");
        
        when(mockNoticeRepository.existsById(noticeId.value())).thenReturn(false);

        //when
        boolean result = noticeQueryService.existsById(noticeId);

        //then
        assertThat(result).isFalse();
        verify(mockNoticeRepository).existsById(noticeId.value());
    }

    @Test
    @DisplayName("문자열 ID로 공지사항 조회 - 존재하는 경우")
    void getNoticeByIdStringWhenExists() {
        //given
        String noticeId = "noticeId";
        Notice expectedNotice = createTestNoticeWithId();
        
        when(mockNoticeRepository.findById(noticeId)).thenReturn(Optional.of(expectedNotice));

        //when
        Notice result = noticeQueryService.getNoticeById(noticeId);

        //then
        assertThat(result).isEqualTo(expectedNotice);
        verify(mockNoticeRepository).findById(noticeId);
    }

    @Test
    @DisplayName("문자열 ID로 공지사항 조회 - 존재하지 않는 경우")
    void getNoticeByIdStringWhenNotExists() {
        //given
        String noticeId = "nonExistentNoticeId";
        
        when(mockNoticeRepository.findById(noticeId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> noticeQueryService.getNoticeById(noticeId))
                .isInstanceOf(NoticeException.class);
        verify(mockNoticeRepository).findById(noticeId);
    }

    @Test
    @DisplayName("읽지 않은 공지사항 존재 여부 확인 - 존재하는 경우")
    void areUnreadNoticesExistWhenExists() {
        //given
        String country = "KR";
        String userId = "userId";
        
        when(mockNoticeRepository.areUnreadNoticesExist(country, userId)).thenReturn(true);

        //when
        Boolean result = noticeQueryService.areUnreadNoticesExist(country, userId);

        //then
        assertThat(result).isTrue();
        verify(mockNoticeRepository).areUnreadNoticesExist(country, userId);
    }

    @Test
    @DisplayName("읽지 않은 공지사항 존재 여부 확인 - 존재하지 않는 경우")
    void areUnreadNoticesExistWhenNotExists() {
        //given
        String country = "KR";
        String userId = "userId";
        
        when(mockNoticeRepository.areUnreadNoticesExist(country, userId)).thenReturn(false);

        //when
        Boolean result = noticeQueryService.areUnreadNoticesExist(country, userId);

        //then
        assertThat(result).isFalse();
        verify(mockNoticeRepository).areUnreadNoticesExist(country, userId);
    }

    @Test
    @DisplayName("빈 결과 조회")
    void getEmptyResults() {
        //given
        String country = "KR";
        List<Notice> emptyList = List.of();

        when(mockNoticeRepository.findActiveNoticesForCountryId(country)).thenReturn(emptyList);

        //when
        List<Notice> result = noticeQueryService.getActiveNoticesForCountryId(country);

        //then
        assertThat(result).isEmpty();
        verify(mockNoticeRepository).findActiveNoticesForCountryId(country);
    }

    @Test
    @DisplayName("모든 NoticeType으로 조회")
    void getNoticesWithAllNoticeTypes() {
        //given & when & then
        for (NoticeType type : NoticeType.values()) {
            List<Notice> expectedNotices = List.of(createTestNotice());
            when(mockNoticeRepository.findByType(type)).thenReturn(expectedNotices);
            
            List<Notice> result = noticeQueryService.getNoticesByType(type);
            
            assertThat(result).isEqualTo(expectedNotices);
            verify(mockNoticeRepository).findByType(type);
        }
    }

    @Test
    @DisplayName("다양한 국가로 활성 공지사항 조회")
    void getActiveNoticesForVariousCountries() {
        //given
        String[] countries = {"KR", "JP", "CN", "AU"};
        List<Notice> expectedNotices = List.of(createTestNotice());

        //when & then
        for (String country : countries) {
            when(mockNoticeRepository.findActiveNoticesForCountryId(country)).thenReturn(expectedNotices);

            List<Notice> result = noticeQueryService.getActiveNoticesForCountryId(country);
            
            assertThat(result).isEqualTo(expectedNotices);
            verify(mockNoticeRepository).findActiveNoticesForCountryId(country);
        }
    }

    @Test
    @DisplayName("페이지네이션 파라미터 전달 확인")
    void verifyPaginationParameters() {
        //given
        String userId = "userId";
        Pageable pageable = PageRequest.of(2, 5);
        Page<NoticeModelResult> expectedPage = new PageImpl<>(List.of());
        
        when(mockNoticeRepository.getNotices(userId, pageable)).thenReturn(expectedPage);

        //when
        Page<NoticeModelResult> result = noticeQueryService.getNotices(userId, pageable);

        //then
        assertThat(result).isEqualTo(expectedPage);
        verify(mockNoticeRepository).getNotices(userId, pageable);
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