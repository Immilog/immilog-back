package com.backend.immilog.interaction.presentation.payload;

import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.shared.enums.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record InteractionResponse(
        @Schema(description = "상태 코드", example = "200") int status,
        @Schema(description = "응답 메시지", example = "success") String message,
        @Schema(description = "인터렉션") InteractionInformation data
) {
    public static InteractionResponse success(InteractionInformation data) {
        return new InteractionResponse(200, "success", data);
    }

    public static InteractionResponse success(String message) {
        return new InteractionResponse(200, message, null);
    }

    public record InteractionInformation(
            @Schema(description = "인터렉션 ID", example = "interaction123") String interactionId,
            @Schema(description = "사용자 ID", example = "user123") String userId,
            @Schema(description = "게시물 ID", example = "post123") String postId,
            @Schema(description = "댓글 ID", example = "comment123") ContentType contentType,
            @Schema(description = "참조 ID", example = "reference123") InteractionType interactionType,
            @Schema(description = "좋아요 여부", example = "true") LocalDateTime createdAt
    ) {
    }
}