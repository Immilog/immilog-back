package com.backend.immilog.shared.application.event;

import com.backend.immilog.shared.domain.event.DomainEvent;
import com.backend.immilog.shared.domain.event.DomainEvents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DomainEventPublisher {

    private final EventPublisher eventPublisher;

    public DomainEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishEvents() {
        List<DomainEvent> events = DomainEvents.getEvents();
        
        if (events.isEmpty()) {
            log.debug("No events to publish in ThreadLocal");
            return;
        }
        
        log.debug("Publishing {} domain events via event publisher", events.size());
        
        for (DomainEvent event : events) {
            try {
                eventPublisher.publishDomainEvent(event);
            } catch (Exception e) {
                log.error("Failed to publish domain event: {}", event.getClass().getSimpleName(), e);
            }
        }
        
        DomainEvents.clearEvents();
        log.debug("Cleared domain events from ThreadLocal");
    }

    public void publishCompensationEvent(DomainEvent event) {
        try {
            log.debug("Publishing compensation event: {}", event.getClass().getSimpleName());
            eventPublisher.publishCompensationEvent(event);
        } catch (Exception e) {
            log.error("Failed to publish compensation event: {}", event.getClass().getSimpleName(), e);
            throw new RuntimeException("Failed to publish compensation event", e);
        }
    }
    
    public void publishDomainEvent(DomainEvent event) {
        try {
            log.debug("Publishing domain event: {}", event.getClass().getSimpleName());
            eventPublisher.publishDomainEvent(event);
        } catch (Exception e) {
            log.error("Failed to publish domain event: {}", event.getClass().getSimpleName(), e);
            throw new RuntimeException("Failed to publish domain event", e);
        }
    }
}