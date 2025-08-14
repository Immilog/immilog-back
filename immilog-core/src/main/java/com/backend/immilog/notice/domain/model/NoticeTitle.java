package com.backend.immilog.notice.domain.model;

import com.backend.immilog.notice.exception.NoticeErrorCode;
import com.backend.immilog.notice.exception.NoticeException;

public record NoticeTitle(String value) {

    public static NoticeTitle of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_TITLE);
        }
        if (value.length() > 200) {
            throw new NoticeException(NoticeErrorCode.NOTICE_TITLE_TOO_LONG);
        }
        return new NoticeTitle(value.trim());
    }

    public boolean isEmpty() {
        return value == null || value.trim().isEmpty();
    }

    public int length() {
        return value != null ? value.length() : 0;
    }
}