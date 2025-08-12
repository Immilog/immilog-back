package com.backend.immilog.notice.domain.service;

import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.model.NoticeAuthor;
import com.backend.immilog.notice.exception.NoticeErrorCode;
import com.backend.immilog.notice.exception.NoticeException;
import org.springframework.stereotype.Service;

@Service
public class NoticeAuthorizationService {

    private final NoticeAuthPolicy noticeAuthPolicy;

    public NoticeAuthorizationService(NoticeAuthPolicy noticeAuthPolicy) {
        this.noticeAuthPolicy = noticeAuthPolicy;
    }

    public NoticeAuthor validateAndGetAuthor(String token) {
        noticeAuthPolicy.validateAdmin(token);
        String userId = noticeAuthPolicy.getUserIdFromToken(token);
        return NoticeAuthor.of(userId);
    }

    public void validateAdminAccess(String token) {
        noticeAuthPolicy.validateAdmin(token);
    }

    public void validateNoticeReadAccess(
            Notice notice,
            String userId,
            String userCountryId
    ) {
        if (notice == null) {
            throw new NoticeException(NoticeErrorCode.NOTICE_NOT_FOUND);
        }

        if (notice.isDeleted()) {
            throw new NoticeException(NoticeErrorCode.NOTICE_ALREADY_DELETED);
        }

        if (userId == null || userId.isBlank()) {
            throw new NoticeException(NoticeErrorCode.INVALID_USER_SEQ);
        }

        if (userCountryId != null && !notice.isTargetedTo(userCountryId)) {
            throw new NoticeException(NoticeErrorCode.NOTICE_NOT_FOUND);
        }
    }

    public void validateNoticeModificationAccess(
            Notice notice,
            String token
    ) {
        NoticeAuthor requester = validateAndGetAuthor(token);

        if (!notice.isAuthor(requester.userId())) {
            throw new NoticeException(NoticeErrorCode.NOT_AN_ADMIN_USER);
        }

        if (notice.isDeleted()) {
            throw new NoticeException(NoticeErrorCode.NOTICE_ALREADY_DELETED);
        }
    }

    public String getUserIdFromToken(String token) {
        return noticeAuthPolicy.getUserIdFromToken(token);
    }
}