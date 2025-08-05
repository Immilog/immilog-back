package com.backend.immilog.shared.infrastructure.event;

import com.backend.immilog.shared.domain.event.DomainEvent;
import com.backend.immilog.shared.domain.event.DomainEventHandler;
import com.backend.immilog.shared.infrastructure.event.dto.RedisEventMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RedisEventSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final ApplicationContext applicationContext;
    private final Map<Class<? extends DomainEvent>, DomainEventHandler<? extends DomainEvent>> handlerCache = new ConcurrentHashMap<>();

    public RedisEventSubscriber(
            ObjectMapper objectMapper,
            ApplicationContext applicationContext
    ) {
        this.objectMapper = objectMapper;
        this.applicationContext = applicationContext;
        initializeHandlers();
    }

    @Override
    public void onMessage(
            Message message,
            byte[] pattern
    ) {
        try {
            var messageBody = new String(message.getBody());
            var channelName = new String(message.getChannel());

            log.debug("Received message from channel: {}", channelName);

            // 채널에 따라 적절한 핸들러 메소드 호출
            if (channelName.equals("domain-events")) {
                this.handleDomainEvent(messageBody);
            } else if (channelName.equals("compensation-events")) {
                this.handleCompensationEvent(messageBody);
            }

        } catch (Exception e) {
            log.error("Failed to process Redis message", e);
        }
    }

    public void handleDomainEvent(String messageBody) {
        try {
            // RedisTemplate이 이미 역직렬화를 수행했으므로, 문자열을 다시 파싱할 필요가 있습니다
            var eventMessage = objectMapper.readValue(messageBody, RedisEventMessage.class);
            this.processEventMessage(eventMessage, false);
        } catch (Exception e) {
            log.error("Failed to handle domain event: {}", messageBody, e);
        }
    }

    public void handleCompensationEvent(String messageBody) {
        try {
            var eventMessage = objectMapper.readValue(messageBody, RedisEventMessage.class);
           this.processEventMessage(eventMessage, true);
        } catch (Exception e) {
            log.error("Failed to handle compensation event: {}", messageBody, e);
        }
    }

    @SuppressWarnings("unchecked")
    private void processEventMessage(
            RedisEventMessage eventMessage,
            boolean isCompensation
    ) {
        try {
            // 이벤트 타입으로 클래스 로드
            var eventClass = Class.forName(eventMessage.eventType());

            if (!DomainEvent.class.isAssignableFrom(eventClass)) {
                log.warn("Event class {} is not a DomainEvent", eventClass.getName());
                return;
            }

            // JSON에서 이벤트 객체로 역직렬화 
            var event = (DomainEvent) objectMapper.readValue(eventMessage.payload(), eventClass);

            // 캐시된 핸들러 조회 또는 생성
            var handler = (DomainEventHandler<DomainEvent>) handlerCache.get(event.getClass());

            if (handler != null) {
                log.debug(
                        "Processing {} event: {}",
                        isCompensation ? "compensation" : "domain",
                        event.getClass().getSimpleName()
                );

                handler.handle(event);

                log.debug(
                        "Successfully processed {} event: {}",
                        isCompensation ? "compensation" : "domain",
                        event.getClass().getSimpleName()
                );
            } else {
                log.debug("No handler found for event type: {}", event.getClass().getName());
            }

        } catch (Exception e) {
            log.error("Failed to process event message: {}", eventMessage.eventType(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeHandlers() {
        var handlers = applicationContext.getBeansOfType(DomainEventHandler.class);

        for (var handler : handlers.values()) {
            var eventType = handler.getEventType();
            handlerCache.put(eventType, handler);
            log.debug(
                    "Registered event handler: {} for event type: {}",
                    handler.getClass().getSimpleName(),
                    eventType.getName()
            );
        }

        log.info("Initialized {} domain event handlers", handlerCache.size());
    }
}