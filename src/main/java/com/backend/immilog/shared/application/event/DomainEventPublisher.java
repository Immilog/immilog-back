package com.backend.immilog.shared.application.event;

import com.backend.immilog.shared.domain.event.DomainEvent;
import com.backend.immilog.shared.domain.event.DomainEventHandler;
import com.backend.immilog.shared.domain.event.DomainEvents;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DomainEventPublisher {

    private final Map<Class<? extends DomainEvent>, List<DomainEventHandler<? extends DomainEvent>>> handlers = 
            new ConcurrentHashMap<>();

    public <T extends DomainEvent> void registerHandler(DomainEventHandler<T> handler) {
        handlers.computeIfAbsent(handler.getEventType(), k -> new java.util.ArrayList<>()).add(handler);
    }

    @SuppressWarnings("unchecked")
    public void publishEvents() {
        List<DomainEvent> events = DomainEvents.getEvents();
        
        for (DomainEvent event : events) {
            List<DomainEventHandler<? extends DomainEvent>> eventHandlers = handlers.get(event.getClass());
            if (eventHandlers != null) {
                for (DomainEventHandler<? extends DomainEvent> handler : eventHandlers) {
                    ((DomainEventHandler<DomainEvent>) handler).handle(event);
                }
            }
        }
        
        DomainEvents.clearEvents();
    }
}