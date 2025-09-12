package com.backend.immilog.post.application.event;

import com.backend.immilog.post.domain.service.PostDomainService;
import com.backend.immilog.post.domain.model.post.PostId;
import com.backend.immilog.post.domain.events.PostCompensationEvent;
import com.backend.immilog.shared.domain.event.DomainEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCompensationEventHandler implements DomainEventHandler<PostCompensationEvent.CommentCountIncreaseCompensation> {

    private final PostDomainService postDomainService;

    @Override
    public void handle(PostCompensationEvent.CommentCountIncreaseCompensation event) {
        log.warn(
                "Processing compensation event for transaction: {} - Rolling back comment count increase for post: {}",
                event.getTransactionId(),
                event.getPostId()
        );

        try {
            postDomainService.decrementCommentCount(PostId.of(event.getPostId()));

            log.info(
                    "Successfully processed compensation event for transaction: {} - Comment count rolled back for post: {}",
                    event.getTransactionId(),
                    event.getPostId()
            );

        } catch (Exception e) {
            log.error(
                    "Failed to process compensation event for transaction: {} - Could not rollback comment count for post: {}",
                    event.getTransactionId(),
                    event.getPostId(),
                    e
            );
        }
    }

    @Override
    public Class<PostCompensationEvent.CommentCountIncreaseCompensation> getEventType() {
        return PostCompensationEvent.CommentCountIncreaseCompensation.class;
    }
}