package com.backend.immilog.shared.infrastructure.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record RedisEventMessage(
        @Schema(description = "이벤트 메시지 ID", example = "uuid-1234") 
        String messageId,
        
        @Schema(description = "이벤트 타입 (클래스명)", example = "com.backend.immilog.post.domain.events.PostEvent$InteractionDataRequested") 
        String eventType,
        
        @Schema(description = "직렬화된 이벤트 페이로드") 
        String payload,
        
        @Schema(description = "이벤트 발생 시간") 
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime publishedAt
) {
}