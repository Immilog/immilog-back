package com.backend.immilog.comment.application.handlers;

import com.backend.immilog.comment.application.services.CommentQueryService;
import com.backend.immilog.shared.domain.event.CommentDataRequestedEvent;
import com.backend.immilog.shared.domain.event.DomainEventHandler;
import com.backend.immilog.shared.domain.model.CommentData;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentDataRequestedEventHandler implements DomainEventHandler<CommentDataRequestedEvent> {

    private final CommentQueryService commentQueryService;
    private final EventResultStorageService eventResultStorageService;

    @Override
    public void handle(CommentDataRequestedEvent event) {
        log.info("Processing CommentDataRequestedEvent for postIds: {} from domain: {}",
                event.getPostIds(), event.getRequestingDomain());
        
        try {
            List<CommentData> commentDataList = event.getPostIds().stream()
                    .flatMap(postId -> {
                        try {
                            var comments = commentQueryService.getCommentsByPostId(postId);
                            return comments.stream().map(comment -> new CommentData(
                                    comment.id(),
                                    comment.postId(),
                                    comment.userId(),
                                    comment.content(),
                                    comment.replyCount(),
                                    comment.status().name()
                            ));
                        } catch (Exception e) {
                            log.warn("Failed to get comment data for postId: {}", postId, e);
                            return java.util.stream.Stream.empty();
                        }
                    })
                    .toList();
            
            String responseKey = "comment_data_" + event.getRequestId();
            eventResultStorageService.storeResult(responseKey, commentDataList);

            log.info("Successfully processed and stored {} comment data records with requestId: {} from domain: {}",
                    commentDataList.size(), event.getRequestId(), event.getRequestingDomain());
            
        } catch (Exception e) {
            log.error("Failed to process CommentDataRequestedEvent from domain: {}", 
                    event.getRequestingDomain(), e);
        }
    }

    @Override
    public Class<CommentDataRequestedEvent> getEventType() {
        return CommentDataRequestedEvent.class;
    }
}