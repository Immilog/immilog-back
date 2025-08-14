package com.backend.immilog.shared.domain.event;

public interface DomainEventHandler<T extends DomainEvent> {
    void handle(T event);
    Class<T> getEventType();
}