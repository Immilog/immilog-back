package com.backend.immilog.post.presentation.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.util.List;

public record PostListResponse(
        @Schema(description = "상태 코드", example = "200") Integer status,
        @Schema(description = "메시지", example = "success") String message,
        @Schema(description = "게시글 리스트") List<PostInformation> data
) {
    public static PostListResponse of(List<PostInformation> data) {
        return new PostListResponse(
                HttpStatus.OK.value(),
                "success",
                data
        );
    }
}
