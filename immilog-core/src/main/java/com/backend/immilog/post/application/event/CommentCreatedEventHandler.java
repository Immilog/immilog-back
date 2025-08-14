package com.backend.immilog.post.application.event;

import com.backend.immilog.comment.domain.event.CommentCreatedEvent;
import com.backend.immilog.post.application.services.PostCommandService;
import com.backend.immilog.post.application.services.PostQueryService;
import com.backend.immilog.post.domain.events.PostCompensationEvent;
import com.backend.immilog.shared.config.properties.EventProperties;
import com.backend.immilog.shared.domain.event.DomainEventHandler;
import com.backend.immilog.shared.domain.event.DomainEvents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class CommentCreatedEventHandler implements DomainEventHandler<CommentCreatedEvent> {

    private final PostQueryService postQueryService;
    private final PostCommandService postCommandService;
    private final EventProperties eventProperties;

    public CommentCreatedEventHandler(
            PostQueryService postQueryService,
            PostCommandService postCommandService,
            EventProperties eventProperties
    ) {
        this.postQueryService = postQueryService;
        this.postCommandService = postCommandService;
        this.eventProperties = eventProperties;
    }

    @Override
    public void handle(CommentCreatedEvent event) {
        var transactionId = UUID.randomUUID().toString();
        
        try {
            log.debug(
                    "Processing CommentCreatedEvent for post: {} in transaction: {}",
                    event.getPostId(),
                    transactionId
            );

            if (eventProperties.simulateFailure() && Math.random() < eventProperties.failureRate()) {
                throw new RuntimeException("Simulated failure for testing compensation events");
            }
                
            var post = postQueryService.getPostById(event.getPostId());
            var updatedPost = post.increaseCommentCount();
            postCommandService.save(updatedPost);

            log.debug(
                    "Successfully increased comment count for post: {} in transaction: {}",
                    event.getPostId(),
                    transactionId
            );
                
        } catch (Exception e) {
            log.error(
                    "Failed to update comment count for post: {} in transaction: {} - Publishing compensation event",
                    event.getPostId(),
                    transactionId, e
            );

            // 보상 이벤트 발행이 활성화된 경우에만 실행
            if (eventProperties.enableCompensation()) {
                var compensationEvent = new PostCompensationEvent.CommentCountIncreaseCompensation(
                        transactionId,
                        event.getCommentId(),
                        event.getPostId()
                );
                DomainEvents.raiseCompensationEvent(compensationEvent);
                log.info("Published compensation event for transaction: {}", transactionId);
            } else {
                log.warn("Compensation disabled - compensation event not published for transaction: {}", transactionId);
            }
        }
    }

    @Override
    public Class<CommentCreatedEvent> getEventType() {
        return CommentCreatedEvent.class;
    }
}