package com.backend.immilog.jobboard.exception;

public enum JobBoardErrorCode {
    JOBBOARD_NOT_FOUND("JobBoard not found"),
    JOBBOARD_CREATE_FAILED("JobBoard create failed"),
    JOBBOARD_UPDATE_FAILED("JobBoard update failed"),
    JOBBOARD_DELETE_FAILED("JobBoard delete failed");

    private final String message;

    JobBoardErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}