package com.backend.immilog.interaction.application.services;

import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import com.backend.immilog.shared.domain.model.InteractionData;
import com.backend.immilog.shared.domain.service.InteractionDataProvider;
import com.backend.immilog.shared.enums.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InteractionDataProviderImpl implements InteractionDataProvider {

    private final InteractionUserRepository interactionUserRepository;

    @Override
    public InteractionData getInteractionData(String contentId, ContentType contentType) {
        try {
            var interactions = interactionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    List.of(contentId),
                    contentType,
                    InteractionStatus.ACTIVE
            );

            var likeInteraction = interactions.stream()
                    .filter(i -> InteractionType.LIKE.equals(i.interactionType()))
                    .findFirst()
                    .orElse(null);

            return likeInteraction != null
                    ? InteractionData.builder()
                    .id(likeInteraction.id())
                    .postId(likeInteraction.postId())
                    .userId(likeInteraction.userId())
                    .interactionStatus(likeInteraction.interactionStatus().name())
                    .interactionType(likeInteraction.interactionType().name())
                    .contentType(contentType.name())
                    .build()
                    : InteractionData.builder()
                    .id(null)
                    .postId(contentId)
                    .userId(null)
                    .interactionStatus("INACTIVE")
                    .interactionType("NONE")
                    .contentType(contentType.name())
                    .build();
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
            var interactions = interactionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    List.of(contentId),
                    contentType,
                    InteractionStatus.ACTIVE
            );

            var userInteraction = interactions.stream()
                    .filter(i -> userId.equals(i.userId()))
                    .findFirst()
                    .orElse(null);

            return userInteraction != null
                    ? InteractionData.builder()
                    .id(userInteraction.id())
                    .postId(userInteraction.postId())
                    .userId(userInteraction.userId())
                    .interactionStatus(userInteraction.interactionStatus().name())
                    .interactionType(userInteraction.interactionType().name())
                    .contentType(contentType.name())
                    .build()
                    : InteractionData.builder()
                    .id(null)
                    .postId(contentId)
                    .userId(userId)
                    .interactionStatus("INACTIVE")
                    .interactionType("NONE")
                    .contentType(contentType.name())
                    .build();
        } catch (Exception e) {
            log.warn("Failed to get user interaction data for userId: {}, contentId: {}", userId, contentId, e);
            return new InteractionData(null, contentId, userId, "INACTIVE", "NONE", contentType.name());
        }
    }
}