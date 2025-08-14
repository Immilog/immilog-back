package com.backend.immilog.user.exception;

import com.backend.immilog.shared.exception.CustomException;
import com.backend.immilog.shared.exception.ErrorCode;

public class UserException extends CustomException {
    public UserException(ErrorCode e) {
        super(e);
    }
}
