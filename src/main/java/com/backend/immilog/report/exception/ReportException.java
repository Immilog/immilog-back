package com.backend.immilog.report.exception;

public class ReportException extends RuntimeException {
    private final ReportErrorCode errorCode;

    public ReportException(ReportErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ReportException(ReportErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ReportException(ReportErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public ReportErrorCode getErrorCode() {
        return errorCode;
    }
}