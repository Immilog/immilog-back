package com.backend.immilog.interaction.application.handlers;

import com.backend.immilog.interaction.application.services.InteractionUserQueryService;
import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.post.domain.events.PostEvent;
import com.backend.immilog.shared.domain.event.DomainEventHandler;
import com.backend.immilog.shared.enums.ContentType;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookmarkPostsRequestedEventHandler implements DomainEventHandler<PostEvent.BookmarkPostsRequested> {

    private final InteractionUserQueryService interactionUserQueryService;
    private final EventResultStorageService eventResultStorageService;

    public BookmarkPostsRequestedEventHandler(
            InteractionUserQueryService interactionUserQueryService,
            EventResultStorageService eventResultStorageService
    ) {
        this.interactionUserQueryService = interactionUserQueryService;
        this.eventResultStorageService = eventResultStorageService;
    }

    @Override
    public void handle(PostEvent.BookmarkPostsRequested event) {
        log.info("Processing BookmarkPostsRequested event for requestId: {}, userId: {}", event.getRequestId(), event.getUserId());
        
        try {
            // Interaction 도메인 서비스를 통해 북마크 데이터 조회
            var bookmarkInteractions = interactionUserQueryService.getBookmarkInteractions(
                    event.getUserId(),
                    ContentType.valueOf(event.getContentType()),
                    InteractionStatus.ACTIVE
            );

            var postIds = bookmarkInteractions.stream()
                    .map(InteractionUser::postId)
                    .toList();
            
            // Redis에 결과 저장
            eventResultStorageService.storeBookmarkData(event.getRequestId(), postIds);
            log.info("Successfully processed and stored {} bookmark records with requestId: {}, postIds: {}", postIds.size(), event.getRequestId(), postIds);
            
        } catch (Exception e) {
            log.error("Failed to process BookmarkPostsRequested event", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<PostEvent.BookmarkPostsRequested> getEventType() {
        return PostEvent.BookmarkPostsRequested.class;
    }
}