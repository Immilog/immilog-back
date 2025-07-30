package com.backend.immilog.comment.presentation.response;

import com.backend.immilog.comment.application.dto.CommentResult;

import java.util.List;

public record CommentResponse(
        int status,
        String message,
        Object data
) {
    public static CommentResponse success(CommentResult data) {
        return new CommentResponse(200, "success", data);
    }

    public static CommentResponse success(List<CommentResult> data) {
        return new CommentResponse(200, "success", data);
    }

    public static CommentResponse success(String message) {
        return new CommentResponse(200, message, null);
    }
}