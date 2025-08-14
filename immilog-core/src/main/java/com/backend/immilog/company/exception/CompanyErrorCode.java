package com.backend.immilog.company.exception;

import com.backend.immilog.shared.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public enum CompanyErrorCode implements ErrorCode {
    MANAGER_INFO_REQUIRED(BAD_REQUEST, "Manager information is required."),
    COMPANY_META_DATA_REQUIRED(BAD_REQUEST, "Company metadata is required."),
    INVALID_COMPANY_NAME(BAD_REQUEST, "Company name cannot be empty."),
    COMPANY_NOT_FOUND(BAD_REQUEST, "Company not found."),
    USER_ALREADY_MANAGER(BAD_REQUEST, "User is already a company manager."),
    COMPANY_NAME_ALREADY_EXISTS(BAD_REQUEST, "Company name already exists."),
    COMPANY_NAME_TOO_LONG(BAD_REQUEST, "Company name is too long."),
    INVALID_COMPANY_EMAIL(BAD_REQUEST, "Company email is invalid."),
    INVALID_COMPANY_EMAIL_FORMAT(BAD_REQUEST, "Company email format is invalid."),
    INVALID_COMPANY_PHONE(BAD_REQUEST, "Company phone is invalid."),
    INVALID_COMPANY_PHONE_FORMAT(BAD_REQUEST, "Company phone format is invalid."),
    INVALID_COMPANY_ADDRESS(BAD_REQUEST, "Company address is invalid."),
    COMPANY_ADDRESS_TOO_LONG(BAD_REQUEST, "Company address is too long."),
    ;
    private final HttpStatus status;
    private final String message;

    CompanyErrorCode(
            HttpStatus status,
            String message
    ) {
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
