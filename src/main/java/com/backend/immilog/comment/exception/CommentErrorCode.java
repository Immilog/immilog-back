package com.backend.immilog.comment.exception;

public enum CommentErrorCode {
    COMMENT_NOT_FOUND("Comment not found"),
    COMMENT_UPDATE_FAILED("Comment update failed"),
    COMMENT_DELETE_FAILED("Comment delete failed"),
    COMMENT_CREATE_FAILED("Comment create failed"),
    INVALID_REFERENCE_TYPE("Invalid reference type"),
    ;
    private final String message;

    CommentErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}