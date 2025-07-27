package com.backend.immilog.user.presentation.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

public record GeneralPayload(
        @Schema(description = "상태 코드", example = "200") Integer status,
        @Schema(description = "메시지", example = "success") String message
) {
    public static GeneralPayload success() {
        return new GeneralPayload(HttpStatus.OK.value(), "success");
    }
}
