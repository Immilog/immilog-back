package com.backend.immilog.shared.infrastructure.event;

import com.backend.immilog.shared.config.event.RedisEventConfig;
import com.backend.immilog.shared.domain.event.DomainEvent;
import com.backend.immilog.shared.domain.event.DomainEventHandler;
import com.backend.immilog.shared.infrastructure.event.dto.RedisEventMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RedisStreamsPushEventListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final ObjectMapper objectMapper;
    private final ApplicationContext applicationContext;
    private final RedisTemplate<String, Object> eventRedisTemplate;
    private final Map<Class<? extends DomainEvent>, DomainEventHandler<? extends DomainEvent>> handlerCache = new ConcurrentHashMap<>();

    public RedisStreamsPushEventListener(
            @Qualifier("eventObjectMapper") ObjectMapper objectMapper,
            ApplicationContext applicationContext,
            RedisTemplate<String, Object> eventRedisTemplate
    ) {
        this.objectMapper = objectMapper;
        this.applicationContext = applicationContext;
        this.eventRedisTemplate = eventRedisTemplate;
        initializeHandlers();
    }

    @Override
    public void onMessage(MapRecord<String, String, String> record) {
        try {
            var streamName = record.getStream();
            var consumerGroup = determineConsumerGroup(streamName);

            log.info("Received message from stream: {}, recordId: {}", streamName, record.getId());

            // 이벤트 처리
            processEventMessage(record);
            
            // 메시지 처리 완료 후 ACK (자동 ACK가 비활성화된 경우)
            acknowledgeMessage(record, consumerGroup);
            
        } catch (Exception e) {
            log.error("Failed to process stream message: streamName={}, recordId={}", record.getStream(), record.getId(), e);
        }
    }

    private void processEventMessage(MapRecord<String, String, String> record) {
        try {
            var fields = record.getValue();
            var eventJson = fields.get("event");
            var messageId = fields.get("messageId");
            
            if (eventJson == null) {
                log.warn("No event field found in stream record: {}", record.getId());
                return;
            }

            log.info("Processing event JSON: {}", eventJson);
            log.info("Event JSON type: {}", eventJson.getClass().getName());
            log.info("Event JSON length: {}", eventJson.length());
            log.info("First 100 chars: {}", eventJson.length() > 100 ? eventJson.substring(0, 100) + "..." : eventJson);
            
            // JSON 문자열이 이스케이프된 상태인지 확인
            if (eventJson.startsWith("\"") && eventJson.endsWith("\"")) {
                log.warn("Event JSON appears to be double-encoded (starts and ends with quotes)");
                // 이스케이프된 JSON 문자열을 언이스케이프
                try {
                    eventJson = objectMapper.readValue(eventJson, String.class);
                    log.info("Unescaped JSON: {}", eventJson);
                } catch (Exception e) {
                    log.error("Failed to unescape JSON", e);
                }
            }
            
            RedisEventMessage eventMessage = objectMapper.readValue(eventJson, RedisEventMessage.class);
            
            // 이벤트 타입에 따른 처리
            boolean isCompensation = record.getStream().equals(RedisEventConfig.COMPENSATION_EVENT_STREAM);
            
            processEventMessage(eventMessage, isCompensation);
            
        } catch (Exception e) {
            log.error("Failed to process event message from record: {}", record.getId(), e);
            throw new RuntimeException("Event processing failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void processEventMessage(RedisEventMessage eventMessage, boolean isCompensation) {
        try {
            // 이벤트 타입으로 클래스 로드
            var eventClass = Class.forName(eventMessage.eventType());

            if (!DomainEvent.class.isAssignableFrom(eventClass)) {
                log.warn("Event class {} is not a DomainEvent", eventClass.getName());
                return;
            }

            // JSON에서 이벤트 객체로 역직렬화
            var event = (DomainEvent) objectMapper.readValue(eventMessage.payload(), eventClass);

            // 캐시된 핸들러 조회
            var handler = (DomainEventHandler<DomainEvent>) handlerCache.get(event.getClass());

            if (handler != null) {
                log.info("Processing {} event: {} with messageId: {}",
                        isCompensation ? "compensation" : "domain",
                        event.getClass().getSimpleName(),
                        eventMessage.messageId());

                handler.handle(event);

                log.info("Successfully processed {} event: {} with messageId: {}",
                        isCompensation ? "compensation" : "domain",
                        event.getClass().getSimpleName(),
                        eventMessage.messageId());
            } else {
                log.warn("No handler found for event type: {}", event.getClass().getName());
            }

        } catch (Exception e) {
            log.error("Failed to process event message: {} with messageId: {}", 
                    eventMessage.eventType(), eventMessage.messageId(), e);
            throw new RuntimeException("Event processing failed", e);
        }
    }

    private void acknowledgeMessage(MapRecord<String, String, String> record, String consumerGroup) {
        try {
            var streamName = record.getStream();
            eventRedisTemplate.opsForStream().acknowledge(Objects.requireNonNull(streamName), consumerGroup, record.getId());
            log.info("Acknowledged message: stream={}, group={}, recordId={}", streamName, consumerGroup, record.getId());
        } catch (Exception e) {
            log.error("Failed to acknowledge message: stream={}, group={}, recordId={}", record.getStream(), consumerGroup, record.getId(), e);
        }
    }

    private String determineConsumerGroup(String streamName) {
        if (RedisEventConfig.DOMAIN_EVENT_STREAM.equals(streamName)) {
            return RedisEventConfig.DOMAIN_EVENT_GROUP;
        } else if (RedisEventConfig.COMPENSATION_EVENT_STREAM.equals(streamName)) {
            return RedisEventConfig.COMPENSATION_EVENT_GROUP;
        } else {
            throw new IllegalArgumentException("Unknown stream: " + streamName);
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeHandlers() {
        var handlers = applicationContext.getBeansOfType(DomainEventHandler.class);

        for (var handler : handlers.values()) {
            var eventType = handler.getEventType();
            handlerCache.put(eventType, handler);

            log.info("Registered event handler: {} for event type: {},",
                    handler.getClass().getSimpleName(),
                    eventType.getName());
        }

        log.info("Initialized {} domain event handlers for Redis Streams Push", handlerCache.size());
    }
}