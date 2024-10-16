package com.backend.immilog.notice.infrastructure.repositories;

import com.backend.immilog.notice.application.result.NoticeResult;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.model.enums.NoticeCountry;
import com.backend.immilog.notice.infrastructure.jdbc.NoticeJdbcRepository;
import com.backend.immilog.notice.infrastructure.jpa.NoticeEntity;
import com.backend.immilog.notice.infrastructure.jpa.NoticeJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("NoticeRepositoryImpl 클래스 테스트")
class NoticeRepositoryImplTest {

    private final NoticeJdbcRepository noticeJdbcRepository = mock(NoticeJdbcRepository.class);
    private final NoticeJpaRepository noticeJpaRepository = mock(NoticeJpaRepository.class);
    private final JdbcClient jdbcClient = mock(JdbcClient.class);
    private final NoticeRepositoryImpl noticeRepository = new NoticeRepositoryImpl(
            noticeJdbcRepository,
            noticeJpaRepository,
            jdbcClient
    );

    @Test
    @DisplayName("공지사항 저장 - 성공")
    void saveEntity_savesNoticeSuccessfully() {
        Notice notice = Notice.builder().seq(1L).build();
        NoticeEntity noticeEntity = NoticeEntity.from(notice);
        when(noticeJpaRepository.save(any(NoticeEntity.class))).thenReturn(noticeEntity);
        noticeRepository.saveEntity(notice);
        verify(noticeJpaRepository, times(1)).save(any(NoticeEntity.class));
    }

    @Test
    @DisplayName("공지사항 조회 - 공지사항이 없는 경우")
    void getNotices_returnsEmptyPageWhenNoNotices() {
        Pageable pageable = PageRequest.of(0, 10);
        Long userSeq = 999L;
        when(noticeJdbcRepository.getNotices(userSeq, pageable.getPageSize(), pageable.getOffset())).thenReturn(List.of());
        when(noticeJdbcRepository.getTotal(userSeq)).thenReturn(0L);

        Page<NoticeResult> result = noticeRepository.getNotices(userSeq, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0L);
    }

    @Test
    @DisplayName("공지사항 존재유무 체크 - 성공")
    void findBySeq_returnsNoticeWhenExists() {
        Long noticeSeq = 1L;
        NoticeEntity noticeEntity = NoticeEntity.builder().seq(noticeSeq).build();
        when(noticeJpaRepository.findById(noticeSeq)).thenReturn(Optional.of(noticeEntity));

        Optional<Notice> result = noticeRepository.findBySeq(noticeSeq);

        assertThat(result).isPresent();
        assertThat(result.get().seq()).isEqualTo(noticeSeq);
    }

    @Test
    @DisplayName("공지사항 조회 - 공지사항이 없는 경우")
    void findBySeq_returnsEmptyWhenNotExists() {
        Long noticeSeq = 999L;
        when(noticeJpaRepository.findById(noticeSeq)).thenReturn(Optional.empty());

        Optional<Notice> result = noticeRepository.findBySeq(noticeSeq);

        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("읽지 않은 공지사항 존재유무 체크 - 성공")
    void areUnreadNoticesExist_returnsTrueWhenUnreadNoticesExist() {
        NoticeCountry country = NoticeCountry.MALAYSIA;
        Long seq = 1L;
        when(noticeJpaRepository.existsByTargetCountriesContainingAndReadUsersNotContaining(country, seq)).thenReturn(true);

        Boolean result = noticeRepository.areUnreadNoticesExist(country, seq);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("읽지 않은 공지사항 존재유무 체크 - 읽지 않은 공지사항이 없는 경우")
    void areUnreadNoticesExist_returnsFalseWhenNoUnreadNoticesExist() {
        NoticeCountry country = NoticeCountry.MALAYSIA;
        Long seq = 999L;
        when(noticeJpaRepository.existsByTargetCountriesContainingAndReadUsersNotContaining(country, seq)).thenReturn(false);

        Boolean result = noticeRepository.areUnreadNoticesExist(country, seq);

        assertThat(result).isFalse();
    }
}