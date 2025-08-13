package com.backend.immilog.notice.application.service;

import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.repository.NoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NoticeCommandServiceTest {

    private final NoticeRepository mockNoticeRepository = mock(NoticeRepository.class);

    private NoticeCommandService noticeCommandService;

    @BeforeEach
    void setUp() {
        noticeCommandService = new NoticeCommandService(mockNoticeRepository);
    }

    @Test
    @DisplayName("공지사항 저장 - 정상 케이스")
    void saveNoticeSuccessfully() {
        //given
        Notice notice = createTestNotice();
        Notice savedNotice = createTestNoticeWithId();
        
        when(mockNoticeRepository.save(notice)).thenReturn(savedNotice);

        //when
        Notice result = noticeCommandService.save(notice);

        //then
        assertThat(result).isEqualTo(savedNotice);
        verify(mockNoticeRepository).save(notice);
    }

    @Test
    @DisplayName("공지사항 삭제 - 정상 케이스")
    void deleteNoticeSuccessfully() {
        //given
        Notice notice = createTestNoticeWithId();

        //when
        noticeCommandService.delete(notice);

        //then
        verify(mockNoticeRepository).delete(notice);
    }

    @Test
    @DisplayName("ID로 공지사항 삭제 - 정상 케이스")
    void deleteNoticeByIdSuccessfully() {
        //given
        String noticeId = "noticeId";

        //when
        noticeCommandService.deleteById(noticeId);

        //then
        verify(mockNoticeRepository).deleteById(noticeId);
    }

    @Test
    @DisplayName("null인 공지사항 저장")
    void saveNullNotice() {
        //given
        Notice notice = null;
        
        when(mockNoticeRepository.save(notice)).thenReturn(null);

        //when
        Notice result = noticeCommandService.save(notice);

        //then
        assertThat(result).isNull();
        verify(mockNoticeRepository).save(notice);
    }

    @Test
    @DisplayName("null인 공지사항 삭제")
    void deleteNullNotice() {
        //given
        Notice notice = null;

        //when
        noticeCommandService.delete(notice);

        //then
        verify(mockNoticeRepository).delete(notice);
    }

    @Test
    @DisplayName("빈 문자열 ID로 삭제")
    void deleteByEmptyId() {
        //given
        String noticeId = "";

        //when
        noticeCommandService.deleteById(noticeId);

        //then
        verify(mockNoticeRepository).deleteById(noticeId);
    }

    @Test
    @DisplayName("null ID로 삭제")
    void deleteByNullId() {
        //given
        String noticeId = null;

        //when
        noticeCommandService.deleteById(noticeId);

        //then
        verify(mockNoticeRepository).deleteById(noticeId);
    }

    @Test
    @DisplayName("여러 공지사항 연속 저장")
    void saveMultipleNoticesSequentially() {
        //given
        Notice notice1 = createTestNotice();
        Notice notice2 = createTestNotice();
        Notice savedNotice1 = createTestNoticeWithId();
        Notice savedNotice2 = createTestNoticeWithId();
        
        when(mockNoticeRepository.save(notice1)).thenReturn(savedNotice1);
        when(mockNoticeRepository.save(notice2)).thenReturn(savedNotice2);

        //when
        Notice result1 = noticeCommandService.save(notice1);
        Notice result2 = noticeCommandService.save(notice2);

        //then
        assertThat(result1).isEqualTo(savedNotice1);
        assertThat(result2).isEqualTo(savedNotice2);
        verify(mockNoticeRepository).save(notice1);
        verify(mockNoticeRepository).save(notice2);
    }

    @Test
    @DisplayName("여러 공지사항 연속 삭제")
    void deleteMultipleNoticesSequentially() {
        //given
        Notice notice1 = createTestNoticeWithId();
        Notice notice2 = createTestNoticeWithId();

        //when
        noticeCommandService.delete(notice1);
        noticeCommandService.delete(notice2);

        //then
        verify(mockNoticeRepository).delete(notice1);
        verify(mockNoticeRepository).delete(notice2);
    }

    @Test
    @DisplayName("저장과 삭제 혼합 작업")
    void mixedSaveAndDeleteOperations() {
        //given
        Notice noticeToSave = createTestNotice();
        Notice noticeToDelete = createTestNoticeWithId();
        Notice savedNotice = createTestNoticeWithId();
        String idToDelete = "idToDelete";
        
        when(mockNoticeRepository.save(noticeToSave)).thenReturn(savedNotice);

        //when
        Notice result = noticeCommandService.save(noticeToSave);
        noticeCommandService.delete(noticeToDelete);
        noticeCommandService.deleteById(idToDelete);

        //then
        assertThat(result).isEqualTo(savedNotice);
        verify(mockNoticeRepository).save(noticeToSave);
        verify(mockNoticeRepository).delete(noticeToDelete);
        verify(mockNoticeRepository).deleteById(idToDelete);
    }

    private Notice createTestNotice() {
        return com.backend.immilog.notice.domain.model.Notice.create(
                com.backend.immilog.notice.domain.model.NoticeAuthor.of("authorId"),
                com.backend.immilog.notice.domain.model.NoticeTitle.of("테스트 제목"),
                com.backend.immilog.notice.domain.model.NoticeContent.of("테스트 내용"),
                com.backend.immilog.notice.domain.enums.NoticeType.NOTICE,
                com.backend.immilog.notice.domain.model.NoticeTargeting.of(java.util.List.of("KR"))
        );
    }

    private Notice createTestNoticeWithId() {
        return com.backend.immilog.notice.domain.model.Notice.restore(
                com.backend.immilog.notice.domain.model.NoticeId.of("noticeId"),
                com.backend.immilog.notice.domain.model.NoticeAuthor.of("authorId"),
                com.backend.immilog.notice.domain.model.NoticeTitle.of("테스트 제목"),
                com.backend.immilog.notice.domain.model.NoticeContent.of("테스트 내용"),
                com.backend.immilog.notice.domain.enums.NoticeType.NOTICE,
                com.backend.immilog.notice.domain.enums.NoticeStatus.NORMAL,
                com.backend.immilog.notice.domain.model.NoticeTargeting.of(java.util.List.of("KR")),
                com.backend.immilog.notice.domain.model.NoticeReadStatus.empty(),
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );
    }
}