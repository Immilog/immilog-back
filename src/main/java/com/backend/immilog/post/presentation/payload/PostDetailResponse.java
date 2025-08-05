package com.backend.immilog.post.presentation.payload;

import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.comment.presentation.payload.CommentResponse;
import com.backend.immilog.post.application.dto.PostResult;
import org.springframework.http.HttpStatus;

import java.util.List;

public record PostDetailResponse(
        int status,
        String message,
        PostInformation post,
        List<CommentResponse.CommentInformation> comments
) {
    public static PostDetailResponse success(
            PostResult post,
            List<CommentResult> comments
    ) {
        var commentInfoList = comments.stream()
                .map(CommentResult::toInfraDTO)
                .toList();
        return new PostDetailResponse(
                HttpStatus.OK.value(),
                "success",
                post.toInfraDTO(),
                commentInfoList
        );
    }
}