package com.backend.immilog.interaction.application.services;

import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import com.backend.immilog.shared.enums.ContentType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InteractionUserQueryService {
    private final InteractionUserRepository interactionUserRepository;

    public InteractionUserQueryService(InteractionUserRepository interactionUserRepository) {
        this.interactionUserRepository = interactionUserRepository;
    }

    public List<InteractionUser> getInteractionUsersByPostIdListAndActive(
            List<String> postIdList,
            ContentType contentType,
            InteractionStatus interactionStatus
    ) {
        return interactionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                postIdList,
                contentType,
                interactionStatus);
    }

    public List<InteractionUser> getBookmarkInteractions(
            String userId,
            ContentType contentType,
            InteractionStatus interactionStatus
    ) {
        return interactionUserRepository.findBookmarksByUserIdAndContentTypeAndInteractionStatus(
                userId,
                contentType,
                interactionStatus
        );
    }

}