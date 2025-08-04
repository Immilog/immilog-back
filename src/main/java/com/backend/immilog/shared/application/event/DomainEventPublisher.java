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

    /**
     * ThreadLocal에 저장된 이벤트들을 일괄 발행 (fallback용)
     * 이제 DomainEvents.raise()에서 즉시 발행하므로 이 메소드는 fallback 상황에서만 사용
     */
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
                // 필요에 따라 실패한 이벤트를 별도로 처리하거나 재시도 로직 추가
            }
        }
        
        DomainEvents.clearEvents();
        log.debug("Cleared fallback domain events from ThreadLocal");
    }

    /**
     * 보상 이벤트 발행
     */
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