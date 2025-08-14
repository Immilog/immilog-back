package com.backend.immilog.interaction.application.usecase;

import com.backend.immilog.interaction.application.command.InteractionCreateCommand;
import com.backend.immilog.interaction.application.result.InteractionResult;
import com.backend.immilog.interaction.application.services.InteractionUserCommandService;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import org.springframework.stereotype.Service;

public interface InteractionCreateUseCase {
    InteractionResult toggleInteraction(InteractionCreateCommand command);

    @Service
    class InteractionCreator implements InteractionCreateUseCase {
        private final InteractionUserCommandService interactionUserCommandService;

        public InteractionCreator(InteractionUserCommandService interactionUserCommandService) {
            this.interactionUserCommandService = interactionUserCommandService;
        }

        @Override
        public InteractionResult toggleInteraction(InteractionCreateCommand command) {
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