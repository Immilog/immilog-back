package com.backend.immilog.interaction.application.services;

import com.backend.immilog.interaction.domain.model.InteractionStatus;
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
    
    @Transactional
    public InteractionUser createInteraction(InteractionUser interactionUser) {
        return interactionUserRepository.save(interactionUser);
    }
    
    @Transactional
    public InteractionUser activateInteraction(String interactionId) {
        var interaction = interactionUserRepository.findById(interactionId)
                .orElseThrow(() -> new IllegalArgumentException("Interaction not found: " + interactionId));
        
        if (interaction.interactionStatus() == InteractionStatus.ACTIVE) {
            return interaction;
        }
        
        var activatedInteraction = new InteractionUser(
                interaction.id(),
                interaction.userId(),
                interaction.postId(),
                interaction.contentType(),
                interaction.interactionType(),
                InteractionStatus.ACTIVE,
                java.time.LocalDateTime.now()
        );
        
        return interactionUserRepository.save(activatedInteraction);
    }
    
    @Transactional
    public InteractionUser deactivateInteraction(String interactionId) {
        var interaction = interactionUserRepository.findById(interactionId)
                .orElseThrow(() -> new IllegalArgumentException("Interaction not found: " + interactionId));
        
        if (interaction.interactionStatus() == InteractionStatus.INACTIVE) {
            return interaction;
        }
        
        var deactivatedInteraction = new InteractionUser(
                interaction.id(),
                interaction.userId(),
                interaction.postId(),
                interaction.contentType(),
                interaction.interactionType(),
                InteractionStatus.INACTIVE,
                java.time.LocalDateTime.now()
        );
        
        return interactionUserRepository.save(deactivatedInteraction);
    }
}