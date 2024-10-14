package com.backend.immilog.notice.presentation.controller;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.notice.application.services.NoticeCreateService;
import com.backend.immilog.notice.application.services.NoticeInquiryService;
import com.backend.immilog.notice.application.services.NoticeModifyService;
import com.backend.immilog.notice.domain.model.enums.NoticeType;
import com.backend.immilog.notice.presentation.request.NoticeRegisterRequest;
import com.backend.immilog.notice.presentation.response.NoticeApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("공지사항 컨트롤러 테스트")
class NoticeControllerTest {
    private final NoticeCreateService noticeRegisterService = mock(NoticeCreateService.class);
    private final NoticeInquiryService noticeInquiryService = mock(NoticeInquiryService.class);
    private final NoticeModifyService noticeModifyService = mock(NoticeModifyService.class);

    private final NoticeController noticeController = new NoticeController(
            noticeRegisterService,
            noticeInquiryService,
            noticeModifyService
    );

    @Test
    @DisplayName("공지사항 등록 테스트")
    void registerNotice_success() {
        // given
        Long userSeq = 1L;
        String title = "제목";
        String content = "내용";
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserRole userRole = UserRole.ROLE_ADMIN;
        when(request.getAttribute("userRole")).thenReturn(userRole);
        when(request.getAttribute("userSeq")).thenReturn(userSeq);
        NoticeRegisterRequest param = NoticeRegisterRequest.builder()
                .title(title)
                .content(content)
                .type(NoticeType.NOTICE)
                .build();


        // when
        ResponseEntity<NoticeApiResponse> response = noticeController.registerNotice(param, request);

        // then
        verify(noticeRegisterService).registerNotice(userSeq, userRole.name(), param.toCommand());
        assertThat(response.getStatusCodeValue()).isEqualTo(201);

    }

    @Test
    @DisplayName("공지사항 목록 조회 테스트")
    void getNotices_success() {
        // given
        Long userSeq = 1L;
        Integer page = 0;
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userSeq")).thenReturn(userSeq);
        when(noticeInquiryService.getNotices(userSeq, page)).thenReturn(null);

        // when
        ResponseEntity<NoticeApiResponse> response = noticeController.getNotices(page, request);

        // then
        verify(noticeInquiryService).getNotices(userSeq, page);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    @DisplayName("공지사항 상세 조회 테스트")
    void getNoticeDetail_success() {
        // given
        Long noticeSeq = 1L;
        when(noticeInquiryService.getNoticeDetail(noticeSeq)).thenReturn(null);

        // when
        ResponseEntity<NoticeApiResponse> response = noticeController.getNoticeDetail(noticeSeq);

        // then
        verify(noticeInquiryService).getNoticeDetail(noticeSeq);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    @DisplayName("안읽은 공지사항 여부 체크 테스트")
    void isNoticeExist_success() {
        // given
        Long userSeq = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userSeq")).thenReturn(userSeq);
        when(noticeInquiryService.isUnreadNoticeExist(userSeq)).thenReturn(true);

        // when
        ResponseEntity<NoticeApiResponse> response = noticeController.isNoticeExist(request);

        // then
        verify(noticeInquiryService).isUnreadNoticeExist(userSeq);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
}