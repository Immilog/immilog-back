package com.backend.immilog.shared.application.event;

import com.backend.immilog.shared.domain.event.DomainEvent;
import com.backend.immilog.shared.domain.event.DomainEvents;
import com.backend.immilog.shared.infrastructure.event.RedisEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DomainEventPublisher {

    private final RedisEventPublisher redisEventPublisher;

    public DomainEventPublisher(RedisEventPublisher redisEventPublisher) {
        this.redisEventPublisher = redisEventPublisher;
    }

    public void publishEvents() {
        List<DomainEvent> events = DomainEvents.getEvents();
        
        if (events.isEmpty()) {
            log.debug("No events to publish in ThreadLocal");
            return;
        }
        
        log.debug("Publishing {} fallback domain events via Redis pub/sub", events.size());
        
        for (DomainEvent event : events) {
            try {
                redisEventPublisher.publishDomainEvent(event);
            } catch (Exception e) {
                log.error("Failed to publish fallback domain event: {}", event.getClass().getSimpleName(), e);
            }
        }
        
        DomainEvents.clearEvents();
        log.debug("Cleared fallback domain events from ThreadLocal");
    }

    public void publishCompensationEvent(DomainEvent event) {
        try {
            log.debug("Publishing compensation event: {}", event.getClass().getSimpleName());
            redisEventPublisher.publishCompensationEvent(event);
        } catch (Exception e) {
            log.error("Failed to publish compensation event: {}", event.getClass().getSimpleName(), e);
            throw new RuntimeException("Failed to publish compensation event", e);
        }
    }
}