package com.backend.immilog.notice.domain.model;

import com.backend.immilog.notice.exception.NoticeErrorCode;
import com.backend.immilog.notice.exception.NoticeException;

public record NoticeAuthor(Long userSeq) {

    public static NoticeAuthor of(Long userSeq) {
        if (userSeq == null || userSeq <= 0) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_AUTHOR);
        }
        return new NoticeAuthor(userSeq);
    }
}