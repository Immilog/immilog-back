package com.backend.immilog.company.exception;

import com.backend.immilog.shared.exception.CustomException;
import com.backend.immilog.shared.exception.ErrorCode;

public class CompanyException extends CustomException {
    public CompanyException(ErrorCode e) {
        super(e);
    }
}
