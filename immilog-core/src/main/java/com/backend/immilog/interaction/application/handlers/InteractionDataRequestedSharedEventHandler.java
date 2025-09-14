package com.backend.immilog.interaction.application.handlers;

import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import com.backend.immilog.shared.domain.event.DomainEventHandler;
import com.backend.immilog.shared.domain.event.InteractionDataRequestedEvent;
import com.backend.immilog.shared.domain.model.InteractionData;
import com.backend.immilog.shared.enums.ContentType;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InteractionDataRequestedSharedEventHandler implements DomainEventHandler<InteractionDataRequestedEvent> {

    private final InteractionUserRepository interactionUserRepository;
    private final EventResultStorageService eventResultStorageService;

    @Override
    public void handle(InteractionDataRequestedEvent event) {
        log.debug("Processing shared InteractionDataRequestedEvent for contentIds: {} from domain: {}", 
                event.getContentIds(), event.getRequestingDomain());
        
        try {
            ContentType contentType = ContentType.valueOf(event.getContentType().toUpperCase());

            var interactions = interactionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    event.getContentIds(),
                    contentType,
                    InteractionStatus.ACTIVE
            );

            var interactionDataList = interactions.stream()
                    .map(interaction -> new InteractionData(
                            interaction.id(),
                            interaction.postId(),
                            interaction.userId(),
                            interaction.interactionStatus().name(),
                            interaction.interactionType().name(),
                            contentType.name()
                    ))
                    .toList();
            
            String responseKey = "interaction_data_" + event.getRequestId();
            eventResultStorageService.storeResult(responseKey, interactionDataList);
            
            log.debug("Successfully processed and stored {} interaction data records with requestId: {} from domain: {}", 
                    interactionDataList.size(), event.getRequestId(), event.getRequestingDomain());
            
        } catch (Exception e) {
            log.error("Failed to process shared InteractionDataRequestedEvent from domain: {}", 
                    event.getRequestingDomain(), e);
        }
    }

    @Override
    public Class<InteractionDataRequestedEvent> getEventType() {
        return InteractionDataRequestedEvent.class;
    }
}