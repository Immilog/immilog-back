package com.backend.immilog.shared.infrastructure.event.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RedisEventMessage {
    
    @Schema(description = "이벤트 메시지 ID", example = "uuid-1234")
    @JsonProperty("messageId")
    private final String messageId;
    
    @Schema(description = "이벤트 타입 (클래스명)", example = "com.backend.immilog.post.domain.events.PostEvent$InteractionDataRequested")
    @JsonProperty("eventType")
    private final String eventType;
    
    @Schema(description = "직렬화된 이벤트 페이로드")
    @JsonProperty("payload")
    private final String payload;
    
    @Schema(description = "이벤트 발생 시간")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("publishedAt")
    private final LocalDateTime publishedAt;
    
    @JsonCreator
    public RedisEventMessage(
            @JsonProperty("messageId") String messageId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("payload") String payload,
            @JsonProperty("publishedAt") LocalDateTime publishedAt
    ) {
        this.messageId = messageId;
        this.eventType = eventType;
        this.payload = payload;
        this.publishedAt = publishedAt;
    }
    
    public String messageId() {
        return messageId;
    }
    
    public String eventType() {
        return eventType;
    }
    
    public String payload() {
        return payload;
    }
    
    public LocalDateTime publishedAt() {
        return publishedAt;
    }
}