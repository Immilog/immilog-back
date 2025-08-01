package com.backend.immilog.post.application.event;

import com.backend.immilog.comment.domain.event.CommentCreatedEvent;
import com.backend.immilog.post.application.services.PostCommandService;
import com.backend.immilog.post.application.services.PostQueryService;
import com.backend.immilog.shared.domain.event.DomainEventHandler;
import org.springframework.stereotype.Component;

@Component
public class CommentCreatedEventHandler implements DomainEventHandler<CommentCreatedEvent> {

    private final PostQueryService postQueryService;
    private final PostCommandService postCommandService;

    public CommentCreatedEventHandler(
            PostQueryService postQueryService,
            PostCommandService postCommandService
    ) {
        this.postQueryService = postQueryService;
        this.postCommandService = postCommandService;
    }

    @Override
    public void handle(CommentCreatedEvent event) {
        try {
            var post = postQueryService.getPostById(event.getPostId());
            var updatedPost = post.increaseCommentCount();
            postCommandService.save(updatedPost);
        } catch (Exception e) {
            System.err.println("Failed to update comment count for post: " + event.getPostId());
        }
    }

    @Override
    public Class<CommentCreatedEvent> getEventType() {
        return CommentCreatedEvent.class;
    }
}