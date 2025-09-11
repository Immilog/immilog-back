package com.backend.immilog.post.application.services;

import com.backend.immilog.shared.application.event.DomainEventPublisher;
import com.backend.immilog.shared.domain.event.CommentDataRequestedEvent;
import com.backend.immilog.shared.domain.model.CommentData;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class PostCommentDataService {

    private final DomainEventPublisher eventPublisher;
    private final EventResultStorageService eventResultStorage;
    private final Duration requestTimeout = Duration.ofSeconds(5);

    public CompletableFuture<List<CommentData>> getCommentDataAsync(List<String> postIds) {
        var requestId = UUID.randomUUID().toString();

        log.info("Requesting comment data for postIds: {} with requestId: {}", postIds, requestId);

        var requestEvent = new CommentDataRequestedEvent(requestId, postIds, "post");

        return CompletableFuture.supplyAsync(() -> {
            try {
                var processingFuture = eventResultStorage.registerEventProcessing(requestId);
                
                eventPublisher.publishDomainEvent(requestEvent);

                try {
                    processingFuture.get(requestTimeout.toMillis(), TimeUnit.MILLISECONDS);
                    log.info("Event processing completed within timeout for requestId: {}", requestId);
                } catch (java.util.concurrent.TimeoutException e) {
                    log.info("Event processing timeout reached for requestId: {}, checking results anyway", requestId);
                } catch (Exception e) {
                    log.warn("Event processing failed for requestId: {}", requestId, e);
                }
                
                String responseKey = "comment_data_" + requestId;
                var result = eventResultStorage.getResult(responseKey, Object.class);

                log.info("Checking event result for responseKey: {}, result type: {}", 
                        responseKey, result != null ? result.getClass().getSimpleName() : "null");

                if (result instanceof List<?> list) {
                    @SuppressWarnings("unchecked")
                    var response = (List<CommentData>) list;
                    log.info("Received comment data response for requestId: {}, count: {}", requestId, response.size());
                    return response;
                } else {
                    log.warn("No valid event result found for responseKey: {}", responseKey);
                    return List.of();
                }

            } catch (Exception e) {
                log.error("Error during comment data request for postIds: {}", postIds, e);
                return List.of();
            }
        });
    }

    public List<CommentData> getCommentData(List<String> postIds) {
        try {
            return getCommentDataAsync(postIds).get(requestTimeout.toSeconds(), TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.warn("Comment data request timeout for postIds: {}", postIds);
            return List.of();
        } catch (Exception e) {
            log.error("Error during synchronous comment data request for postIds: {}", postIds, e);
            return List.of();
        }
    }

    public int getCommentCountForPost(String postId) {
        var commentData = getCommentData(List.of(postId));
        return commentData.size();
    }
}