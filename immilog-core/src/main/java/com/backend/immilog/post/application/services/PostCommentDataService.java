package com.backend.immilog.post.application.services;

import com.backend.immilog.shared.application.event.DomainEventPublisher;
import com.backend.immilog.shared.domain.event.CommentDataRequestedEvent;
import com.backend.immilog.shared.domain.model.CommentData;
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
public class PostCommentDataService {

    private final DomainEventPublisher eventPublisher;
    private final EventResultStorageService eventResultStorage;
    private final Duration requestTimeout = Duration.ofSeconds(3);

    public PostCommentDataService(
            DomainEventPublisher eventPublisher,
            EventResultStorageService eventResultStorage
    ) {
        this.eventPublisher = eventPublisher;
        this.eventResultStorage = eventResultStorage;
    }

    public CompletableFuture<List<CommentData>> getCommentDataAsync(List<String> postIds) {
        var requestId = UUID.randomUUID().toString();

        log.debug("Requesting comment data for postIds: {} with requestId: {}", postIds, requestId);

        var requestEvent = new CommentDataRequestedEvent(requestId, postIds, "post");

        return CompletableFuture.supplyAsync(() -> {
            try {
                eventPublisher.publishDomainEvent(requestEvent);

                for (int i = 0; i < requestTimeout.toSeconds(); i++) {
                    try {
                        String responseKey = "comment_data_" + requestId;
                        @SuppressWarnings("unchecked")
                        List<CommentData> response = (List<CommentData>) eventResultStorage.getResult(
                                responseKey, List.class);

                        if (response != null && !response.isEmpty()) {
                            log.debug("Received comment data response for requestId: {}, count: {}", 
                                    requestId, response.size());
                            return response;
                        }

                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Comment data request interrupted", e);
                    }
                }

                log.warn("Comment data request timeout for postIds: {}, requestId: {}", postIds, requestId);
                return List.of();

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