package com.backend.immilog.shared.infrastructure.event;

import com.backend.immilog.shared.config.event.RedisEventConfig;
import com.backend.immilog.shared.domain.event.DomainEvent;
import com.backend.immilog.shared.domain.event.DomainEventHandler;
import com.backend.immilog.shared.infrastructure.event.dto.RedisEventMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RedisStreamsPushEventListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final ObjectMapper objectMapper;
    private final ApplicationContext applicationContext;
    private final RedisTemplate<String, Object> eventRedisTemplate;
    private final Map<Class<? extends DomainEvent>, DomainEventHandler<? extends DomainEvent>> handlerCache = new ConcurrentHashMap<>();

    public RedisStreamsPushEventListener(
            ObjectMapper objectMapper,
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
            String streamName = record.getStream();
            String consumerGroup = determineConsumerGroup(streamName);
            
            log.debug("Received message from stream: {}, recordId: {}", streamName, record.getId());

            // 이벤트 처리
            processEventMessage(record);
            
            // 메시지 처리 완료 후 ACK (자동 ACK가 비활성화된 경우)
            acknowledgeMessage(record, consumerGroup);
            
        } catch (Exception e) {
            log.error("Failed to process stream message: streamName={}, recordId={}", 
                    record.getStream(), record.getId(), e);
            // 예외 발생 시 ACK하지 않으므로 재처리됨
        }
    }

    private void processEventMessage(MapRecord<String, String, String> record) {
        try {
            Map<String, String> fields = record.getValue();
            String eventJson = fields.get("event");
            String messageId = fields.get("messageId");
            
            if (eventJson == null) {
                log.warn("No event field found in stream record: {}", record.getId());
                return;
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
            Class<?> eventClass = Class.forName(eventMessage.eventType());

            if (!DomainEvent.class.isAssignableFrom(eventClass)) {
                log.warn("Event class {} is not a DomainEvent", eventClass.getName());
                return;
            }

            // JSON에서 이벤트 객체로 역직렬화
            DomainEvent event = (DomainEvent) objectMapper.readValue(eventMessage.payload(), eventClass);

            // 캐시된 핸들러 조회
            DomainEventHandler<DomainEvent> handler = 
                (DomainEventHandler<DomainEvent>) handlerCache.get(event.getClass());

            if (handler != null) {
                log.debug("Processing {} event: {} with messageId: {}",
                        isCompensation ? "compensation" : "domain",
                        event.getClass().getSimpleName(),
                        eventMessage.messageId());

                handler.handle(event);

                log.debug("Successfully processed {} event: {} with messageId: {}",
                        isCompensation ? "compensation" : "domain",
                        event.getClass().getSimpleName(),
                        eventMessage.messageId());
            } else {
                log.debug("No handler found for event type: {}", event.getClass().getName());
            }

        } catch (Exception e) {
            log.error("Failed to process event message: {} with messageId: {}", 
                    eventMessage.eventType(), eventMessage.messageId(), e);
            throw new RuntimeException("Event processing failed", e);
        }
    }

    private void acknowledgeMessage(MapRecord<String, String, String> record, String consumerGroup) {
        try {
            String streamName = record.getStream();
            eventRedisTemplate.opsForStream().acknowledge(streamName, consumerGroup, record.getId());
            
            log.debug("Acknowledged message: stream={}, group={}, recordId={}", 
                    streamName, consumerGroup, record.getId());
        } catch (Exception e) {
            log.error("Failed to acknowledge message: stream={}, group={}, recordId={}", 
                    record.getStream(), consumerGroup, record.getId(), e);
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
        Map<String, DomainEventHandler> handlers = applicationContext.getBeansOfType(DomainEventHandler.class);

        for (DomainEventHandler handler : handlers.values()) {
            Class<? extends DomainEvent> eventType = handler.getEventType();
            handlerCache.put(eventType, handler);
            
            log.debug("Registered event handler: {} for event type: {}",
                    handler.getClass().getSimpleName(),
                    eventType.getName());
        }

        log.info("Initialized {} domain event handlers for Redis Streams Push", handlerCache.size());
    }
}