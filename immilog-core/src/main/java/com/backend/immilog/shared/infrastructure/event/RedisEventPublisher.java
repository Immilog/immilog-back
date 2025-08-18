package com.backend.immilog.shared.infrastructure.event;

import com.backend.immilog.shared.application.event.EventPublisher;
import com.backend.immilog.shared.config.event.RedisEventConfig;
import com.backend.immilog.shared.domain.event.DomainEvent;
import com.backend.immilog.shared.infrastructure.event.dto.RedisEventMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class RedisEventPublisher implements EventPublisher {

    private final RedisTemplate<String, Object> eventRedisTemplate;
    private final ObjectMapper objectMapper;

    public RedisEventPublisher(
            RedisTemplate<String, Object> eventRedisTemplate,
            ObjectMapper objectMapper
    ) {
        this.eventRedisTemplate = eventRedisTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishDomainEvent(DomainEvent event) {
        try {
            RedisEventMessage eventMessage = createEventMessage(event);
            String eventJson = objectMapper.writeValueAsString(eventMessage);

            // Redis Streams에 메시지 추가 (Map 형태로)
            Map<String, String> fields = new HashMap<>();
            fields.put("event", eventJson);
            fields.put("messageId", eventMessage.messageId());
            fields.put("eventType", eventMessage.eventType());

            RecordId recordId = eventRedisTemplate.opsForStream()
                    .add(RedisEventConfig.DOMAIN_EVENT_STREAM, fields);

            log.debug("Published domain event: {} to stream: {} with recordId: {}",
                    event.getClass().getSimpleName(), 
                    RedisEventConfig.DOMAIN_EVENT_STREAM,
                    recordId);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize domain event: {}", event.getClass().getSimpleName(), e);
            throw new RuntimeException("Failed to publish domain event", e);
        } catch (Exception e) {
            log.error("Failed to publish domain event to stream: {}", event.getClass().getSimpleName(), e);
            throw new RuntimeException("Failed to publish domain event", e);
        }
    }

    public void publishCompensationEvent(DomainEvent event) {
        try {
            RedisEventMessage eventMessage = createEventMessage(event);
            String eventJson = objectMapper.writeValueAsString(eventMessage);

            // Redis Streams에 메시지 추가 (Map 형태로)
            Map<String, String> fields = new HashMap<>();
            fields.put("event", eventJson);
            fields.put("messageId", eventMessage.messageId());
            fields.put("eventType", eventMessage.eventType());

            RecordId recordId = eventRedisTemplate.opsForStream()
                    .add(RedisEventConfig.COMPENSATION_EVENT_STREAM, fields);

            log.debug("Published compensation event: {} to stream: {} with recordId: {}",
                    event.getClass().getSimpleName(),
                    RedisEventConfig.COMPENSATION_EVENT_STREAM,
                    recordId);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize compensation event: {}", event.getClass().getSimpleName(), e);
            throw new RuntimeException("Failed to publish compensation event", e);
        } catch (Exception e) {
            log.error("Failed to publish compensation event to stream: {}", event.getClass().getSimpleName(), e);
            throw new RuntimeException("Failed to publish compensation event", e);
        }
    }

    private RedisEventMessage createEventMessage(DomainEvent event) throws JsonProcessingException {
        return new RedisEventMessage(
                UUID.randomUUID().toString(),
                event.getClass().getName(),
                objectMapper.writeValueAsString(event),
                LocalDateTime.now()
        );
    }
}