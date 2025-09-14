package com.backend.immilog.post.presentation.payload;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "북마크 토글 응답")
public record BookmarkToggleResponse(
        @Schema(description = "북마크 상태 (true: 북마크됨, false: 북마크 해제됨)")
        boolean bookmarked
) {
}