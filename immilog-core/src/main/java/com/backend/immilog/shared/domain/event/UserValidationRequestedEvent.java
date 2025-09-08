package com.backend.immilog.shared.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.backend.immilog.shared.domain.event.DomainEventTypes.USER_DATA_REQUESTED;

/**
 * 사용자 검증 요청 이벤트
 * 도메인 간 사용자 존재 여부 및 유효성 검증을 위한 표준 이벤트
 */
@Getter
public class UserValidationRequestedEvent extends StandardDomainEvent {
    
    private final String requestId;
    private final String targetUserId;
    private final String requestingDomain;

    public UserValidationRequestedEvent() {
        super();
        this.requestId = null;
        this.targetUserId = null;
        this.requestingDomain = null;
    }
    
    public UserValidationRequestedEvent(String requestId, String targetUserId, String requestingDomain) {
        super(USER_DATA_REQUESTED, targetUserId);
        this.requestId = requestId;
        this.targetUserId = targetUserId;
        this.requestingDomain = requestingDomain;
    }

    @Override
    public String toString() {
        return String.format("UserValidationRequestedEvent{requestId='%s', targetUserId='%s', requestingDomain='%s'}",
                requestId, targetUserId, requestingDomain);
    }
}