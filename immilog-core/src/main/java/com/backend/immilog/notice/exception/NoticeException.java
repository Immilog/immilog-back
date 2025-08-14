package com.backend.immilog.notice.exception;

import com.backend.immilog.shared.exception.CustomException;
import com.backend.immilog.shared.exception.ErrorCode;

public class NoticeException extends CustomException {
    public NoticeException(ErrorCode e) {
        super(e);
    }
}
