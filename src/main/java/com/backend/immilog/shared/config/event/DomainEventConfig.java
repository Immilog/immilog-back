package com.backend.immilog.shared.config.event;

import com.backend.immilog.post.application.event.CommentCreatedEventHandler;
import com.backend.immilog.shared.application.event.DomainEventPublisher;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainEventConfig {

    private final DomainEventPublisher domainEventPublisher;
    private final CommentCreatedEventHandler commentCreatedEventHandler;

    public DomainEventConfig(
            DomainEventPublisher domainEventPublisher,
            CommentCreatedEventHandler commentCreatedEventHandler
    ) {
        this.domainEventPublisher = domainEventPublisher;
        this.commentCreatedEventHandler = commentCreatedEventHandler;
    }

    @PostConstruct
    public void registerEventHandlers() {
        domainEventPublisher.registerHandler(commentCreatedEventHandler);
    }
}