package com.backend.immilog.jobboard.exception;

public class JobBoardException extends RuntimeException {
    private final JobBoardErrorCode errorCode;

    public JobBoardException(JobBoardErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public JobBoardErrorCode getErrorCode() {
        return errorCode;
    }
}