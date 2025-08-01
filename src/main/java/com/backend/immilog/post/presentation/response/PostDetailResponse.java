package com.backend.immilog.post.presentation.response;

import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.post.application.dto.PostResult;
import org.springframework.http.HttpStatus;

import java.util.List;

public record PostDetailResponse(
        int status,
        String message,
        PostResult post,
        List<CommentResult> comments
) {
    public static PostDetailResponse success(PostResult post, List<CommentResult> comments) {
        return new PostDetailResponse(
                HttpStatus.OK.value(),
                "success",
                post,
                comments
        );
    }
}