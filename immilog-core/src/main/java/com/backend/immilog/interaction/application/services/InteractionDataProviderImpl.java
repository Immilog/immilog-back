package com.backend.immilog.interaction.application.services;

import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.shared.domain.model.InteractionData;
import com.backend.immilog.shared.domain.service.InteractionDataProvider;
import com.backend.immilog.shared.enums.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class InteractionDataProviderImpl implements InteractionDataProvider {

    private final InteractionUserQueryService interactionUserQueryService;

    public InteractionDataProviderImpl(InteractionUserQueryService interactionUserQueryService) {
        this.interactionUserQueryService = interactionUserQueryService;
    }

    @Override
    public InteractionData getInteractionData(String contentId, ContentType contentType) {
        try {
            var interactions = interactionUserQueryService.getInteractionUsersByPostIdListAndActive(
                    List.of(contentId),
                    contentType,
                    InteractionStatus.ACTIVE
            );

            var likeInteraction = interactions.stream()
                    .filter(i -> InteractionType.LIKE.equals(i.interactionType()))
                    .findFirst()
                    .orElse(null);

            return likeInteraction != null 
                    ? new InteractionData(
                            likeInteraction.id(), 
                            likeInteraction.postId(), 
                            likeInteraction.userId(),
                            likeInteraction.interactionStatus().name(),
                            likeInteraction.interactionType().name(),
                            contentType.name()
                      )
                    : new InteractionData(
                            null, 
                            contentId, 
                            null,
                            "INACTIVE",
                            "NONE",
                            contentType.name()
                      );
        } catch (Exception e) {
            log.warn("Failed to get interaction data for contentId: {}", contentId, e);
            return new InteractionData(null, contentId, null, "INACTIVE", "NONE", contentType.name());
        }
    }

    @Override
    public List<InteractionData> getInteractionDataBatch(List<String> contentIds, ContentType contentType) {
        return contentIds.stream()
                .map(contentId -> getInteractionData(contentId, contentType))
                .toList();
    }

    @Override
    public InteractionData getUserInteractionData(String userId, String contentId, ContentType contentType) {
        try {
            var interactions = interactionUserQueryService.getInteractionUsersByPostIdListAndActive(
                    List.of(contentId),
                    contentType,
                    InteractionStatus.ACTIVE
            );

            var userInteraction = interactions.stream()
                    .filter(i -> userId.equals(i.userId()))
                    .findFirst()
                    .orElse(null);

            return userInteraction != null
                    ? new InteractionData(
                            userInteraction.id(),
                            userInteraction.postId(),
                            userInteraction.userId(),
                            userInteraction.interactionStatus().name(),
                            userInteraction.interactionType().name(),
                            contentType.name()
                      )
                    : new InteractionData(
                            null,
                            contentId,
                            userId,
                            "INACTIVE",
                            "NONE",
                            contentType.name()
                      );
        } catch (Exception e) {
            log.warn("Failed to get user interaction data for userId: {}, contentId: {}", userId, contentId, e);
            return new InteractionData(null, contentId, userId, "INACTIVE", "NONE", contentType.name());
        }
    }
}