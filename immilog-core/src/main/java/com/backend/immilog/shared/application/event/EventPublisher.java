package com.backend.immilog.shared.application.event;

import com.backend.immilog.shared.domain.event.DomainEvent;

/**
 * 이벤트 발행을 위한 추상화 인터페이스
 * 추후 RabbitMQ, Kafka 등으로 쉽게 마이그레이션할 수 있도록 설계
 */
public interface EventPublisher {
    
    /**
     * 도메인 이벤트를 발행합니다.
     * @param event 발행할 도메인 이벤트
     */
    void publishDomainEvent(DomainEvent event);
    
    /**
     * 보상 이벤트를 발행합니다.
     * @param event 발행할 보상 이벤트
     */
    void publishCompensationEvent(DomainEvent event);
}