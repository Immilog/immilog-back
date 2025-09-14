package com.backend.immilog.interaction.application.services;

import com.backend.immilog.interaction.application.dto.in.InteractionCreateCommand;
import com.backend.immilog.interaction.application.dto.out.InteractionResult;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.interaction.domain.service.InteractionDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

public interface CreateInteractionUseCase {
    InteractionResult toggleInteraction(InteractionCreateCommand command);

    @Service
    @RequiredArgsConstructor
    class CreatorInteraction implements CreateInteractionUseCase {
        private final InteractionUserCommandService interactionUserCommandService;
        private final InteractionDomainService interactionDomainService;

        @Override
        public InteractionResult toggleInteraction(InteractionCreateCommand command) {
            if (!interactionDomainService.canUserInteract(
                    command.userId(),
                    command.postId(),
                    command.interactionType(),
                    command.contentType()
            )) {
                throw new IllegalArgumentException("User cannot interact with this content");
            }
            
            var interaction = InteractionUser.of(
                    command.userId(),
                    command.postId(),
                    command.contentType(),
                    command.interactionType()
            );
            var savedInteraction = interactionUserCommandService.toggleInteraction(interaction);
            return InteractionResult.from(savedInteraction);
        }
    }
}