package com.backend.immilog.comment.exception;

public class CommentException extends RuntimeException {
    private final CommentErrorCode errorCode;

    public CommentException(CommentErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CommentErrorCode getErrorCode() {
        return errorCode;
    }
}