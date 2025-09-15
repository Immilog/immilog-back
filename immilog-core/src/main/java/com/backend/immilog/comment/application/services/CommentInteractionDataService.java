package com.backend.immilog.comment.application.services;

import com.backend.immilog.shared.application.event.DomainEventPublisher;
import com.backend.immilog.shared.domain.event.InteractionDataRequestedEvent;
import com.backend.immilog.shared.domain.model.InteractionData;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentInteractionDataService {

    private final DomainEventPublisher eventPublisher;
    private final EventResultStorageService eventResultStorage;
    private final Duration requestTimeout = Duration.ofSeconds(3);

    public CompletableFuture<List<InteractionData>> getInteractionDataAsync(List<String> commentIds) {
        var requestId = UUID.randomUUID().toString();

        log.debug("Requesting comment interaction data for commentIds: {} with requestId: {}", commentIds, requestId);

        var requestEvent = new InteractionDataRequestedEvent(requestId, commentIds, "COMMENT", "comment");

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

                if (response != null) {
                    log.info("Received comment interaction data response for requestId: {}, count: {}", 
                            requestId, response.size());
                    return response;
                } else {
                    log.warn("No valid comment interaction data found for requestId: {}", requestId);
                    return List.of();
                }

            } catch (Exception e) {
                log.error("Error during comment interaction data request for commentIds: {}", commentIds, e);
                return List.of();
            }
        });
    }

    public List<InteractionData> getInteractionData(List<String> commentIds) {
        try {
            return getInteractionDataAsync(commentIds).get(requestTimeout.toSeconds(), TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.warn("Comment interaction data request timeout for commentIds: {}", commentIds);
            return List.of();
        } catch (Exception e) {
            log.error("Error during synchronous comment interaction data request for commentIds: {}", commentIds, e);
            return List.of();
        }
    }

    public Map<String, Integer> getLikeCountsByCommentIds(List<String> commentIds) {
        var interactionData = getInteractionData(commentIds);
        return interactionData.stream()
                .filter(data -> "LIKE".equals(data.interactionType()))
                .collect(Collectors.groupingBy(
                        InteractionData::id,
                        Collectors.summingInt(data -> 1)
                ));
    }
}