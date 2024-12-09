package com.backend.immilog.notice.application;

import com.backend.immilog.notice.application.result.NoticeResult;
import com.backend.immilog.notice.application.services.NoticeInquiryService;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.model.enums.NoticeType;
import com.backend.immilog.notice.domain.repositories.NoticeRepository;
import com.backend.immilog.user.application.services.UserInformationService;
import com.backend.immilog.user.domain.model.user.User;
import com.backend.immilog.user.domain.enums.UserCountry;
import com.backend.immilog.user.domain.model.user.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static com.backend.immilog.notice.domain.model.enums.NoticeCountry.SOUTH_KOREA;
import static com.backend.immilog.notice.domain.model.enums.NoticeStatus.NORMAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("공지사항 조회 테스트")
class NoticeInquiryServiceTest {
    private final NoticeRepository noticeRepository = mock(NoticeRepository.class);
    private final UserInformationService userInformationService = mock(UserInformationService.class);

    private final NoticeInquiryService noticeInquiryService = new NoticeInquiryService(
            noticeRepository,
            userInformationService
    );

    @Test
    @DisplayName("공지사항 조회 - 성공")
    void getNotices_Success() {
        // given
        Long userSeq = 1L;
        int page = 0;
        Notice notice = Notice.builder()
                .title("title")
                .userSeq(userSeq)
                .content("content")
                .type(NoticeType.NOTICE)
                .targetCountries(List.of(SOUTH_KOREA))
                .status(NORMAL)
                .build();

        when(noticeRepository.getNotices(userSeq, PageRequest.of(page, 10)))
                .thenReturn(new PageImpl<>(List.of(NoticeResult.from(notice))));
        // when
        Page<NoticeResult> notices = noticeInquiryService.getNotices(userSeq, page);

        // then
        assertThat(notices.get().findFirst().get().content()).isEqualTo("content");
    }

    @Test
    @DisplayName("공지사항 조회 - 실패")
    void getNotices_Fail() {
        // given
        Long userSeq = null;
        int page = 0;

        // when
        Page<NoticeResult> notices = noticeInquiryService.getNotices(userSeq, page);

        // then
        assertThat(notices).isEmpty();
    }

    @Test
    @DisplayName("공지사항 상세 조회 - 성공")
    void getNoticeDetail_Success() {
        // given
        Long noticeSeq = 1L;
        Notice notice = Notice.builder()
                .seq(noticeSeq)
                .title("title")
                .userSeq(1L)
                .content("content")
                .type(NoticeType.NOTICE)
                .targetCountries(List.of(SOUTH_KOREA))
                .status(NORMAL)
                .build();

        when(noticeRepository.findBySeq(noticeSeq))
                .thenReturn(java.util.Optional.of(notice));
        // when
        NoticeResult noticeDTO = noticeInquiryService.getNoticeDetail(noticeSeq);

        // then
        assertThat(noticeDTO.content()).isEqualTo("content");
    }

    @Test
    @DisplayName("안읽은 공지사항 여부 체크")
    void areUnreadNoticesExist() {
        // given
        Long userSeq = 1L;
        User user = User.builder()
                .seq(1L)
                .location(Location.of(UserCountry.SOUTH_KOREA, "서울"))
                .build();

        when(noticeRepository.areUnreadNoticesExist(SOUTH_KOREA, userSeq))
                .thenReturn(true);
        when(userInformationService.getUser(userSeq)).thenReturn(user);

        // when
        boolean result = noticeInquiryService.isUnreadNoticeExist(userSeq);

        // then
        assertThat(result).isTrue();
    }
}