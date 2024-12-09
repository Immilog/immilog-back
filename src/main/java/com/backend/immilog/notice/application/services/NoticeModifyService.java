package com.backend.immilog.notice.application.services;

import com.backend.immilog.notice.application.command.NoticeModifyCommand;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.repositories.NoticeRepository;
import com.backend.immilog.notice.exception.NoticeErrorCode;
import com.backend.immilog.notice.exception.NoticeException;
import com.backend.immilog.user.application.services.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.backend.immilog.notice.domain.model.enums.NoticeStatus.DELETED;
import static com.backend.immilog.notice.exception.NoticeErrorCode.NOTICE_NOT_FOUND;
import static com.backend.immilog.notice.exception.NoticeErrorCode.NOT_AN_ADMIN_USER;

@Service
@RequiredArgsConstructor
public class NoticeModifyService {
    private final UserQueryService userQueryService;
    private final NoticeRepository noticeRepository;

    @Transactional
    public void modifyNotice(
            Long userSeq,
            Long noticeSeq,
            NoticeModifyCommand request
    ) {
        throwExceptionIfNotAdmin(userSeq);
        Notice notice = getNoticeBySeq(noticeSeq);
        if (notice.status().equals(DELETED)) {
            throw new NoticeException(NoticeErrorCode.NOTICE_NOT_FOUND);
        }
        setIfItsNotNull(request, notice);
        noticeRepository.saveEntity(notice);
    }

    private void throwExceptionIfNotAdmin(
            Long userSeq
    ) {
        Optional.ofNullable(userQueryService.getUserById(userSeq))
                .filter(Optional::isPresent)
                .filter(user -> user.get().getUserRole().name().equals("ROLE_ADMIN"))
                .orElseThrow(() -> new NoticeException(NOT_AN_ADMIN_USER));
    }

    private void setIfItsNotNull(
            NoticeModifyCommand request,
            Notice notice
    ) {
        new Notice(
                notice.seq(),
                notice.userSeq(),
                request.title() != null ? request.title() : notice.title(),
                request.content() != null ? request.content() : notice.content(),
                request.type() != null ? request.type() : notice.type(),
                request.status() != null ? request.status() : notice.status(),
                notice.targetCountries(),
                notice.readUsers(),
                notice.createdAt(),
                notice.updatedAt()
        );
    }

    private Notice getNoticeBySeq(
            Long noticeSeq
    ) {
        return noticeRepository
                .getNotice(noticeSeq)
                .orElseThrow(() -> new NoticeException(NOTICE_NOT_FOUND));
    }
}

