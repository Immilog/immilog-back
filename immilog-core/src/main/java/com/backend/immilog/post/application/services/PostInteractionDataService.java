package com.backend.immilog.post.application.services;

import com.backend.immilog.shared.application.event.DomainEventPublisher;
import com.backend.immilog.shared.domain.event.InteractionDataRequestedEvent;
import com.backend.immilog.shared.domain.model.InteractionData;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class PostInteractionDataService {

    private final DomainEventPublisher eventPublisher;
    private final EventResultStorageService eventResultStorage;
    private final Duration requestTimeout = Duration.ofSeconds(3);

    public PostInteractionDataService(
            DomainEventPublisher eventPublisher,
            EventResultStorageService eventResultStorage
    ) {
        this.eventPublisher = eventPublisher;
        this.eventResultStorage = eventResultStorage;
    }

    public CompletableFuture<List<InteractionData>> getInteractionDataAsync(List<String> postIds, String contentType) {
        var requestId = UUID.randomUUID().toString();

        log.debug("Requesting interaction data for postIds: {} with requestId: {}", postIds, requestId);

        var requestEvent = new InteractionDataRequestedEvent(requestId, postIds, contentType, "post");

        return CompletableFuture.supplyAsync(() -> {
            try {
                var processingFuture = eventResultStorage.registerEventProcessing(requestId);
                
                eventPublisher.publishDomainEvent(requestEvent);

                try {
                    processingFuture.get(requestTimeout.toMillis(), TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    log.warn("Event processing timeout or failed for requestId: {}", requestId, e);
                }

                var responseKey = "interaction_data_" + requestId;
                @SuppressWarnings("unchecked")
                var response = (List<InteractionData>) eventResultStorage.getResult(responseKey, List.class);

                if (response != null && !response.isEmpty()) {
                    log.info("Received interaction data response for requestId: {}, count: {}", 
                            requestId, response.size());
                    return response;
                } else {
                    log.warn("No valid interaction data found for requestId: {}", requestId);
                    return List.of();
                }

            } catch (Exception e) {
                log.error("Error during interaction data request for postIds: {}", postIds, e);
                return List.of();
            }
        });
    }

    public List<InteractionData> getInteractionData(List<String> postIds, String contentType) {
        try {
            return getInteractionDataAsync(postIds, contentType).get(requestTimeout.toSeconds(), TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.warn("Interaction data request timeout for postIds: {}", postIds);
            return List.of();
        } catch (Exception e) {
            log.error("Error during synchronous interaction data request for postIds: {}", postIds, e);
            return List.of();
        }
    }
}