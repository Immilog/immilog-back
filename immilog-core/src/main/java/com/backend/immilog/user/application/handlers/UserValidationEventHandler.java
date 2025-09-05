package com.backend.immilog.user.application.handlers;

import com.backend.immilog.shared.application.event.DomainEventPublisher;
import com.backend.immilog.shared.domain.event.DomainEventHandler;
import com.backend.immilog.shared.domain.event.UserValidationRequestedEvent;
import com.backend.immilog.shared.domain.event.UserValidationResponseEvent;
import com.backend.immilog.shared.domain.service.UserDataProvider;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 사용자 검증 요청 이벤트 핸들러
 * 다른 도메인에서 요청한 사용자 검증을 처리하고 결과를 이벤트로 응답
 */
@Slf4j
@Component
public class UserValidationEventHandler implements DomainEventHandler<UserValidationRequestedEvent> {
    
    private final UserDataProvider userDataProvider;
    private final DomainEventPublisher eventPublisher;
    private final EventResultStorageService eventResultStorage;
    
    public UserValidationEventHandler(UserDataProvider userDataProvider, 
                                    DomainEventPublisher eventPublisher,
                                    EventResultStorageService eventResultStorage) {
        this.userDataProvider = userDataProvider;
        this.eventPublisher = eventPublisher;
        this.eventResultStorage = eventResultStorage;
    }
    
    @Override
    public void handle(UserValidationRequestedEvent event) {
        log.debug("Processing UserValidationRequestedEvent for userId: {} from domain: {}", 
                event.getTargetUserId(), event.getRequestingDomain());
        
        try {
            String userId = event.getTargetUserId();
            boolean isValid = userDataProvider.isValidUser(userId);
            
            UserValidationResponseEvent response;
            if (isValid) {
                var userData = userDataProvider.getUserData(userId);
                response = new UserValidationResponseEvent(
                        event.getRequestId(), 
                        userId, 
                        true, 
                        userData
                );
                log.debug("User validation successful for userId: {}", userId);
            } else {
                response = new UserValidationResponseEvent(
                        event.getRequestId(), 
                        userId, 
                        false, 
                        null, 
                        "User not found or invalid"
                );
                log.warn("User validation failed for userId: {}", userId);
            }
            
            // 이벤트 발행 및 결과 저장
            eventPublisher.publishDomainEvent(response);
            
            // EventResultStorageService에 결과 저장 (동기 조회를 위해)
            String resultKey = "user_validation_" + event.getRequestId();
            eventResultStorage.storeResult(resultKey, response);
            
        } catch (Exception e) {
            log.error("Error processing UserValidationRequestedEvent for userId: {}", 
                    event.getTargetUserId(), e);
            
            UserValidationResponseEvent errorResponse = new UserValidationResponseEvent(
                    event.getRequestId(), 
                    event.getTargetUserId(), 
                    false, 
                    null, 
                    "Internal error: " + e.getMessage()
            );
            eventPublisher.publishDomainEvent(errorResponse);
            
            // 에러 응답도 결과 저장소에 저장
            String resultKey = "user_validation_" + event.getRequestId();
            eventResultStorage.storeResult(resultKey, errorResponse);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Class<UserValidationRequestedEvent> getEventType() {
        return UserValidationRequestedEvent.class;
    }
}