package com.backend.immilog.country.exception;

public class CountryException extends RuntimeException {
    private final CountryErrorCode errorCode;

    public CountryException(CountryErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CountryException(
            CountryErrorCode errorCode,
            Throwable cause
    ) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public CountryErrorCode getErrorCode() {
        return errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }
}