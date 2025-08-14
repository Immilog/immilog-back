package com.backend.immilog.notice.domain.model;

import com.backend.immilog.notice.exception.NoticeErrorCode;
import com.backend.immilog.notice.exception.NoticeException;

public record NoticeAuthor(String userId) {

    public static NoticeAuthor of(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_AUTHOR);
        }
        return new NoticeAuthor(userId);
    }
}