package com.backend.immilog.interaction.application.services;

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

    public List<InteractionUser> getInteractionUsersByPostIdList(
            List<String> postIdList,
            ContentType contentType
    ) {
        return interactionUserRepository.findByPostIdListAndContentType(postIdList, contentType);
    }

    public List<InteractionUser> getBookmarkInteractions(
            String userId,
            ContentType contentType
    ) {
        return interactionUserRepository.findBookmarksByUserIdAndContentType(userId, contentType);
    }
}