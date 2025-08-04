package com.backend.immilog.post.presentation.payload;

import com.backend.immilog.post.application.dto.PostResult;
import com.backend.immilog.post.domain.model.post.PostInfo;
import io.swagger.v3.oas.annotations.media.Schema;

public record PostSingleResponse(
        @Schema(description = "상태 코드", example = "200") Integer status,
        @Schema(description = "메시지", example = "success") String message,
        @Schema(description = "게시글") PostInformation data
) {
}
