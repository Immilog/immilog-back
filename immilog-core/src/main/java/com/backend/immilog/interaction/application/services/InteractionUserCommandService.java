package com.backend.immilog.interaction.application.services;

import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InteractionUserCommandService {
    private final InteractionUserRepository interactionUserRepository;

    public InteractionUserCommandService(InteractionUserRepository interactionUserRepository) {
        this.interactionUserRepository = interactionUserRepository;
    }

    @Transactional
    public InteractionUser toggleInteraction(InteractionUser interactionUser) {
        return interactionUserRepository
                .findByUserIdAndInteractionTypeAndContentTypeAndPostId(
                        interactionUser.userId(),
                        interactionUser.interactionType(),
                        interactionUser.contentType(),
                        interactionUser.postId()
                )
                .map(existing -> interactionUserRepository.save(existing.toggleStatus()))
                .orElseGet(() -> interactionUserRepository.save(interactionUser));
    }

    @Transactional
    public void deleteInteraction(String interactionId) {
        interactionUserRepository.deleteById(interactionId);
    }
}