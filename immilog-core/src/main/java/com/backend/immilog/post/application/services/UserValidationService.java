package com.backend.immilog.post.application.services;

import com.backend.immilog.shared.application.event.DomainEventPublisher;
import com.backend.immilog.shared.domain.event.UserValidationRequestedEvent;
import com.backend.immilog.shared.domain.event.UserValidationResponseEvent;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

/**
 * Post 도메인에서 사용자 검증을 위한 Event-driven 서비스
 * 직접적인 User 도메인 의존성을 제거하고 이벤트 기반으로 검증 수행
 */
@Slf4j
@Service
public class UserValidationService {
    
    private final DomainEventPublisher eventPublisher;
    private final EventResultStorageService eventResultStorage;
    private final Duration validationTimeout = Duration.ofSeconds(5);
    
    public UserValidationService(DomainEventPublisher eventPublisher, 
                               EventResultStorageService eventResultStorage) {
        this.eventPublisher = eventPublisher;
        this.eventResultStorage = eventResultStorage;
    }
    
    /**
     * 사용자 유효성을 Event-driven 방식으로 검증
     * @param userId 검증할 사용자 ID
     * @return 검증 결과 (비동기)
     */
    public CompletableFuture<Boolean> validateUserAsync(String userId) {
        String requestId = UUID.randomUUID().toString();
        
        log.debug("Requesting user validation for userId: {} with requestId: {}", userId, requestId);
        
        // 사용자 검증 요청 이벤트 발행
        UserValidationRequestedEvent requestEvent = new UserValidationRequestedEvent(
                requestId, userId, "post"
        );
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                eventPublisher.publishDomainEvent(requestEvent);
                
                // 응답 대기 (폴링 방식)
                for (int i = 0; i < validationTimeout.toSeconds(); i++) {
                    try {
                        String responseKey = "user_validation_" + requestId;
                        Object response = eventResultStorage.getResult(responseKey, UserValidationResponseEvent.class);
                        
                        if (response instanceof UserValidationResponseEvent validationResponse) {
                            log.debug("Received user validation response for requestId: {}, isValid: {}", 
                                    requestId, validationResponse.isValid());
                            return validationResponse.isValid();
                        }
                        
                        Thread.sleep(1000); // 1초 대기
                        
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("User validation interrupted", e);
                    }
                }
                
                log.warn("User validation timeout for userId: {}, requestId: {}", userId, requestId);
                throw new RuntimeException("User validation timeout");
                
            } catch (Exception e) {
                log.error("Error during user validation for userId: {}", userId, e);
                return false; // 검증 실패 시 false 반환
            }
        });
    }
    
    /**
     * 동기식 사용자 검증 (내부적으로 비동기 호출 후 대기)
     * @param userId 검증할 사용자 ID
     * @return 검증 결과
     */
    public boolean validateUser(String userId) {
        try {
            return validateUserAsync(userId).get(validationTimeout.toSeconds(), 
                    java.util.concurrent.TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.warn("User validation timeout for userId: {}", userId);
            return false;
        } catch (Exception e) {
            log.error("Error during synchronous user validation for userId: {}", userId, e);
            return false;
        }
    }
}