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
                var processingFuture = eventResultStorage.registerEventProcessing(requestId);
                
                eventPublisher.publishDomainEvent(requestEvent);

                try {
                    processingFuture.get(validationTimeout.toMillis(), TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    log.warn("Event processing timeout or failed for requestId: {}", requestId, e);
                }
                
                var responseKey = "user_validation_" + requestId;
                var response = eventResultStorage.getResult(responseKey, UserValidationResponseEvent.class);

                if (response instanceof UserValidationResponseEvent validationResponse) {
                    log.info("Received user validation response for requestId: {}, isValid: {}", requestId, validationResponse.isValid());
                    return validationResponse.isValid();
                } else {
                    log.warn("No valid user validation response found for requestId: {}", requestId);
                    return false;
                }

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