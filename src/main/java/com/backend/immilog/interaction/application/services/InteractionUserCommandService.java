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
    public InteractionUser createInteraction(InteractionUser interactionUser) {
        return interactionUserRepository.save(interactionUser);
    }

    @Transactional
    public void deleteInteraction(String interactionId) {
        interactionUserRepository.deleteById(interactionId);
    }
}