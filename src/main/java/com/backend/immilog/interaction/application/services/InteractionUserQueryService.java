package com.backend.immilog.interaction.application.services;

import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import com.backend.immilog.post.domain.model.post.PostType;
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
            PostType postType
    ) {
        return interactionUserRepository.findByPostIdListAndPostType(postIdList, postType);
    }

    public List<InteractionUser> getBookmarkInteractions(
            String userId,
            PostType postType
    ) {
        return interactionUserRepository.findBookmarksByUserIdAndPostType(userId, postType);
    }
}