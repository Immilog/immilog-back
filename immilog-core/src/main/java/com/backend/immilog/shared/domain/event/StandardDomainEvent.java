package com.backend.immilog.shared.domain.event;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public abstract class StandardDomainEvent implements DomainEvent {
    
    private final String eventId;
    private final String eventType;
    private final String aggregateId;
    private final LocalDateTime occurredAt;
    private final String userId;
    
    protected StandardDomainEvent(String eventType, String aggregateId) {
        this(eventType, aggregateId, null);
    }
    
    protected StandardDomainEvent(String eventType, String aggregateId, String userId) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = Objects.requireNonNull(eventType, "Event type cannot be null");
        this.aggregateId = Objects.requireNonNull(aggregateId, "Aggregate ID cannot be null");
        this.userId = userId;
        this.occurredAt = LocalDateTime.now();
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public String getAggregateId() {
        return aggregateId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    @Override
    public LocalDateTime occurredAt() {
        return occurredAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardDomainEvent that = (StandardDomainEvent) o;
        return Objects.equals(eventId, that.eventId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
    
    @Override
    public String toString() {
        return String.format("%s{eventId='%s', eventType='%s', aggregateId='%s', userId='%s', occurredAt=%s}",
                getClass().getSimpleName(), eventId, eventType, aggregateId, userId, occurredAt);
    }
}