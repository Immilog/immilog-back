package com.backend.immilog.shared.infrastructure.event;

import com.backend.immilog.shared.config.event.RedisEventConfig;
import com.backend.immilog.shared.domain.event.DomainEvent;
import com.backend.immilog.shared.infrastructure.event.dto.RedisEventMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
public class RedisEventPublisher {

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
            String serializedMessage = objectMapper.writeValueAsString(eventMessage);

            eventRedisTemplate.convertAndSend(RedisEventConfig.DOMAIN_EVENT_CHANNEL, serializedMessage);

            log.debug("Published domain event: {} to channel: {}",
                    event.getClass().getSimpleName(), RedisEventConfig.DOMAIN_EVENT_CHANNEL);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize domain event: {}", event.getClass().getSimpleName(), e);
            throw new RuntimeException("Failed to publish domain event", e);
        }
    }

    public void publishCompensationEvent(DomainEvent event) {
        try {
            var eventMessage = this.createEventMessage(event);
            var serializedMessage = objectMapper.writeValueAsString(eventMessage);

            eventRedisTemplate.convertAndSend(RedisEventConfig.COMPENSATION_EVENT_CHANNEL, serializedMessage);

            log.debug(
                    "Published compensation event: {} to channel: {}",
                    event.getClass().getSimpleName(),
                    RedisEventConfig.COMPENSATION_EVENT_CHANNEL
            );

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize compensation event: {}", event.getClass().getSimpleName(), e);
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