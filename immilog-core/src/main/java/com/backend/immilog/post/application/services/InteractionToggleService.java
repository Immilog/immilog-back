package com.backend.immilog.post.application.services;

import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InteractionToggleService {
    private final InteractionUserRepository interactionUserRepository;

    public void deactivateInteraction(String id) {
        interactionUserRepository.findById(id).ifPresent(interactionUser -> {
            if (interactionUser.interactionStatus().isActive()) {
                var updatedInteraction = new InteractionUser(
                        interactionUser.id(),
                        interactionUser.userId(),
                        interactionUser.postId(),
                        interactionUser.contentType(),
                        interactionUser.interactionType(),
                        com.backend.immilog.interaction.domain.model.InteractionStatus.INACTIVE,
                        java.time.LocalDateTime.now()
                );
                interactionUserRepository.save(updatedInteraction);
            }
        });
    }

    public void activateInteraction(String id) {
        interactionUserRepository.findById(id).ifPresent(interactionUser -> {
            if (!interactionUser.interactionStatus().isActive()) {
                var updatedInteraction = new InteractionUser(
                        interactionUser.id(),
                        interactionUser.userId(),
                        interactionUser.postId(),
                        interactionUser.contentType(),
                        interactionUser.interactionType(),
                        InteractionStatus.ACTIVE,
                        java.time.LocalDateTime.now()
                );
                interactionUserRepository.save(updatedInteraction);
            }
        });
    }
}
