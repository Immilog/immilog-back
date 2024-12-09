package com.backend.immilog.notice.application.services;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.notice.application.command.NoticeModifyCommand;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.model.enums.NoticeStatus;
import com.backend.immilog.notice.domain.repositories.NoticeRepository;
import com.backend.immilog.notice.exception.NoticeException;
import com.backend.immilog.user.application.services.UserInformationService;
import com.backend.immilog.user.domain.model.user.User;
import com.backend.immilog.user.domain.enums.UserCountry;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.user.domain.model.user.Location;
import com.backend.immilog.user.domain.model.user.ReportInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.backend.immilog.notice.domain.model.enums.NoticeStatus.DELETED;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("공지사항 수정 서비스 테스트")
class NoticeModifyServiceTest {
    private final UserInformationService userInformationService = mock(UserInformationService.class);
    private final NoticeRepository noticeRepository = mock(NoticeRepository.class);
    private final NoticeModifyService noticeModifyService = new NoticeModifyService(
            userInformationService,
            noticeRepository
    );

    @Test
    @DisplayName("관리자가 아닌 경우 예외 발생")
    void modifyNotice_throwsExceptionIfNotAdmin() {
        Long userSeq = 1L;
        Long noticeSeq = 1L;
        NoticeModifyCommand command = mock(NoticeModifyCommand.class);
        User user = mock(User.class);
        when(userInformationService.getUser(userSeq)).thenReturn(user);
        when(user.userRole()).thenReturn(UserRole.ROLE_USER);
        assertThrows(NoticeException.class, () -> noticeModifyService.modifyNotice(userSeq, noticeSeq, command));
    }

    @Test
    @DisplayName("공지사항을 찾을 수 없는 경우 예외 발생")
    void modifyNotice_throwsExceptionIfNoticeNotFound() {
        Long userSeq = 1L;
        Long noticeSeq = 1L;
        NoticeModifyCommand command = mock(NoticeModifyCommand.class);
        User user = mock(User.class);
        when(userInformationService.getUser(userSeq)).thenReturn(user);
        when(user.userRole()).thenReturn(UserRole.ROLE_ADMIN);
        when(noticeRepository.getNotice(noticeSeq)).thenReturn(Optional.empty());

        assertThrows(NoticeException.class, () -> noticeModifyService.modifyNotice(userSeq, noticeSeq, command));
    }

    @Test
    @DisplayName("삭제된 공지사항인 경우 예외 발생")
    void modifyNotice_throwsExceptionIfNoticeDeleted() {
        Long userSeq = 1L;
        Long noticeSeq = 1L;
        NoticeModifyCommand command = mock(NoticeModifyCommand.class);
        User user = mock(User.class);
        Notice notice = mock(Notice.class);
        when(userInformationService.getUser(userSeq)).thenReturn(user);
        when(user.userRole()).thenReturn(UserRole.ROLE_ADMIN);
        when(noticeRepository.getNotice(noticeSeq)).thenReturn(Optional.of(notice));
        when(notice.status()).thenReturn(DELETED);

        assertThrows(NoticeException.class, () -> noticeModifyService.modifyNotice(userSeq, noticeSeq, command));
    }

    @Test
    @DisplayName("공지사항 수정")
    void modifyNotice_savesNoticeIfValid() {
        Long userSeq = 1L;
        Long noticeSeq = 1L;
        NoticeModifyCommand command = mock(NoticeModifyCommand.class);
        User user = new User(
                userSeq,
                "email",
                "name",
                "password",
                "country",
                UserStatus.ACTIVE,
                UserRole.ROLE_ADMIN,
                UserCountry.SOUTH_KOREA,
                new Location(UserCountry.SOUTH_KOREA, "seoul"),
                ReportInfo.of(0L, Date.valueOf(LocalDate.now())),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Notice notice = mock(Notice.class);
        when(userInformationService.getUser(userSeq)).thenReturn(user);
        when(noticeRepository.getNotice(noticeSeq)).thenReturn(Optional.of(notice));
        when(notice.status()).thenReturn(mock(NoticeStatus.class));

        noticeModifyService.modifyNotice(userSeq, noticeSeq, command);

        verify(noticeRepository).saveEntity(notice);
    }
}