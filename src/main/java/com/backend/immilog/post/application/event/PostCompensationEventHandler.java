package com.backend.immilog.post.application.event;

import com.backend.immilog.post.application.services.PostCommandService;
import com.backend.immilog.post.application.services.PostQueryService;
import com.backend.immilog.post.domain.events.PostCompensationEvent;
import com.backend.immilog.shared.domain.event.DomainEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostCompensationEventHandler implements DomainEventHandler<PostCompensationEvent.CommentCountIncreaseCompensation> {

    private final PostQueryService postQueryService;
    private final PostCommandService postCommandService;

    public PostCompensationEventHandler(
            PostQueryService postQueryService,
            PostCommandService postCommandService
    ) {
        this.postQueryService = postQueryService;
        this.postCommandService = postCommandService;
    }

    @Override
    public void handle(PostCompensationEvent.CommentCountIncreaseCompensation event) {
        log.warn("Processing compensation event for transaction: {} - Rolling back comment count increase for post: {}",
                event.getTransactionId(), event.getPostId());

        try {
            // 댓글 수 증가를 롤백 (댓글 수 감소)
            var post = postQueryService.getPostById(event.getPostId());
            var compensatedPost = post.decreaseCommentCount();
            postCommandService.save(compensatedPost);

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