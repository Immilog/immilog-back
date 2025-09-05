package com.backend.immilog.comment.application.services;

import com.backend.immilog.shared.application.event.DomainEventPublisher;
import com.backend.immilog.shared.domain.event.UserValidationRequestedEvent;
import com.backend.immilog.shared.domain.event.UserValidationResponseEvent;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class CommentUserValidationService {

    private final DomainEventPublisher eventPublisher;
    private final EventResultStorageService eventResultStorage;
    private final Duration validationTimeout = Duration.ofSeconds(5);

    public CommentUserValidationService(
            DomainEventPublisher eventPublisher,
            EventResultStorageService eventResultStorage
    ) {
        this.eventPublisher = eventPublisher;
        this.eventResultStorage = eventResultStorage;
    }

    public CompletableFuture<Boolean> validateUserAsync(String userId) {
        var requestId = UUID.randomUUID().toString();

        log.debug("Requesting user validation for userId: {} with requestId: {}", userId, requestId);

        var requestEvent = new UserValidationRequestedEvent(requestId, userId, "comment");

        return CompletableFuture.supplyAsync(() -> {
            try {
                eventPublisher.publishDomainEvent(requestEvent);

                for (int i = 0; i < validationTimeout.toSeconds(); i++) {
                    try {
                        String responseKey = "user_validation_" + requestId;
                        Object response = eventResultStorage.getResult(responseKey, UserValidationResponseEvent.class);

                        if (response instanceof UserValidationResponseEvent validationResponse) {
                            log.debug("Received user validation response for requestId: {}, isValid: {}", requestId, validationResponse.isValid());
                            return validationResponse.isValid();
                        }

                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Comment user validation interrupted", e);
                    }
                }

                log.warn("Comment user validation timeout for userId: {}, requestId: {}", userId, requestId);
                throw new RuntimeException("Comment user validation timeout");

            } catch (Exception e) {
                log.error("Error during comment user validation for userId: {}", userId, e);
                return false;
            }
        });
    }

    public boolean validateUser(String userId) {
        try {
            return validateUserAsync(userId).get(validationTimeout.toSeconds(), TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.warn("Comment user validation timeout for userId: {}", userId);
            return false;
        } catch (Exception e) {
            log.error("Error during synchronous comment user validation for userId: {}", userId, e);
            return false;
        }
    }
}