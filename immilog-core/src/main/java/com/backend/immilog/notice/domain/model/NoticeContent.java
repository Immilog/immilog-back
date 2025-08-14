package com.backend.immilog.notice.domain.model;

import com.backend.immilog.notice.exception.NoticeErrorCode;
import com.backend.immilog.notice.exception.NoticeException;

public record NoticeContent(String value) {

    public static NoticeContent of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_CONTENT);
        }
        if (value.length() > 5000) {
            throw new NoticeException(NoticeErrorCode.NOTICE_CONTENT_TOO_LONG);
        }
        return new NoticeContent(value.trim());
    }

    public boolean isEmpty() {
        return value == null || value.trim().isEmpty();
    }

    public int length() {
        return value != null ? value.length() : 0;
    }
}