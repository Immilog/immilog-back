package com.backend.immilog.notice.domain.service;

import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.model.NoticeAuthor;
import com.backend.immilog.notice.exception.NoticeErrorCode;
import com.backend.immilog.notice.exception.NoticeException;
import com.backend.immilog.user.domain.model.enums.Country;
import org.springframework.stereotype.Service;

@Service
public class NoticeAuthorizationService {

    private final NoticeAuthPolicy noticeAuthPolicy;

    public NoticeAuthorizationService(NoticeAuthPolicy noticeAuthPolicy) {
        this.noticeAuthPolicy = noticeAuthPolicy;
    }

    public NoticeAuthor validateAndGetAuthor(String token) {
        noticeAuthPolicy.validateAdmin(token);
        Long userSeq = noticeAuthPolicy.getUserSeqFromToken(token);
        return NoticeAuthor.of(userSeq);
    }

    public void validateAdminAccess(String token) {
        noticeAuthPolicy.validateAdmin(token);
    }

    public void validateNoticeReadAccess(
            Notice notice,
            Long userSeq,
            Country userCountry
    ) {
        if (notice == null) {
            throw new NoticeException(NoticeErrorCode.NOTICE_NOT_FOUND);
        }

        if (notice.isDeleted()) {
            throw new NoticeException(NoticeErrorCode.NOTICE_ALREADY_DELETED);
        }

        if (userSeq == null || userSeq <= 0) {
            throw new NoticeException(NoticeErrorCode.INVALID_USER_SEQ);
        }

        // 공지사항이 해당 국가를 대상으로 하는지 확인
        if (userCountry != null && !notice.isTargetedTo(userCountry)) {
            throw new NoticeException(NoticeErrorCode.NOTICE_NOT_FOUND);
        }
    }

    public void validateNoticeModificationAccess(
            Notice notice,
            String token
    ) {
        NoticeAuthor requester = validateAndGetAuthor(token);

        if (!notice.isAuthor(requester.userSeq())) {
            throw new NoticeException(NoticeErrorCode.NOT_AN_ADMIN_USER);
        }

        if (notice.isDeleted()) {
            throw new NoticeException(NoticeErrorCode.NOTICE_ALREADY_DELETED);
        }
    }

    public Long getUserSeqFromToken(String token) {
        return noticeAuthPolicy.getUserSeqFromToken(token);
    }
}