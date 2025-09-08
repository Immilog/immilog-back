package com.backend.immilog.shared.domain.event;

import com.backend.immilog.shared.domain.model.UserData;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.backend.immilog.shared.domain.event.DomainEventTypes.USER_DATA_RESPONSE;

/**
 * 사용자 검증 응답 이벤트
 * 사용자 검증 결과를 요청한 도메인으로 반환하는 표준 이벤트
 */
public class UserValidationResponseEvent extends StandardDomainEvent {
    
    private final String requestId;
    private final boolean isValid;
    private final UserData userData;
    private final String errorMessage;
    
    public UserValidationResponseEvent() {
        super();
        this.requestId = null;
        this.isValid = false;
        this.userData = null;
        this.errorMessage = null;
    }
    
    public UserValidationResponseEvent(String requestId, String userId, boolean isValid, UserData userData) {
        this(requestId, userId, userId, isValid, userData, null);
    }
    
    @JsonCreator
    public UserValidationResponseEvent(
            @JsonProperty("requestId") String requestId, 
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("userId") String userId, 
            @JsonProperty("valid") boolean isValid, 
            @JsonProperty("userData") UserData userData, 
            @JsonProperty("errorMessage") String errorMessage) {
        super(USER_DATA_RESPONSE, aggregateId);
        this.requestId = requestId;
        this.isValid = isValid;
        this.userData = userData;
        this.errorMessage = errorMessage;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public boolean isValid() {
        return isValid;
    }
    
    public UserData getUserData() {
        return userData;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public boolean hasError() {
        return errorMessage != null;
    }
    
    @Override
    public String toString() {
        return String.format("UserValidationResponseEvent{requestId='%s', isValid=%s, userData=%s, errorMessage='%s'}",
                requestId, isValid, userData, errorMessage);
    }
}