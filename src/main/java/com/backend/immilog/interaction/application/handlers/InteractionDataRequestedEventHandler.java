package com.backend.immilog.interaction.application.handlers;

import com.backend.immilog.interaction.application.services.InteractionUserQueryService;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.post.domain.events.PostEvent;
import com.backend.immilog.shared.domain.event.DomainEventHandler;
import com.backend.immilog.shared.domain.model.InteractionData;
import com.backend.immilog.shared.enums.ContentType;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class InteractionDataRequestedEventHandler implements DomainEventHandler<PostEvent.InteractionDataRequested> {

    private final InteractionUserQueryService interactionUserQueryService;
    private final EventResultStorageService eventResultStorageService;

    public InteractionDataRequestedEventHandler(
            InteractionUserQueryService interactionUserQueryService,
            EventResultStorageService eventResultStorageService) {
        this.interactionUserQueryService = interactionUserQueryService;
        this.eventResultStorageService = eventResultStorageService;
    }

    @Override
    public void handle(PostEvent.InteractionDataRequested event) {
        log.debug("Processing InteractionDataRequested event for postIds: {}", event.getPostIds());
        
        try {
            // Interaction 도메인 서비스를 통해 데이터 조회
            List<InteractionUser> interactionUsers = interactionUserQueryService
                    .getInteractionUsersByPostIdList(event.getPostIds(), ContentType.valueOf(event.getContentType()));
            
            // InteractionUser를 InteractionData로 변환
            List<InteractionData> interactionDataList = interactionUsers.stream()
                    .map(this::convertToInteractionData)
                    .toList();
            
            // Redis에 결과 저장
            eventResultStorageService.storeInteractionData(event.getRequestId(), interactionDataList);
            log.debug("Successfully processed and stored {} interaction records with requestId: {}", 
                    interactionDataList.size(), event.getRequestId());
            
        } catch (Exception e) {
            log.error("Failed to process InteractionDataRequested event", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<PostEvent.InteractionDataRequested> getEventType() {
        return PostEvent.InteractionDataRequested.class;
    }
    
    private InteractionData convertToInteractionData(InteractionUser interactionUser) {
        return new InteractionData(
                interactionUser.id(),
                interactionUser.postId(),
                interactionUser.userId(),
                interactionUser.interactionType().name(),
                interactionUser.contentType().name()
        );
    }
}