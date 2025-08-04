package com.backend.immilog.shared.domain.event;

/**
 * 보상 트랜잭션을 위한 이벤트 마커 인터페이스
 */
public interface CompensationEvent extends DomainEvent {
    
    /**
     * 트랜잭션 ID - 관련된 작업들을 그룹핑하기 위함
     */
    String getTransactionId();
    
    /**
     * 보상 액션 타입
     */
    String getCompensationAction();
    
    /**
     * 원본 이벤트 ID (실패한 이벤트의 ID)
     */
    String getOriginalEventId();
}