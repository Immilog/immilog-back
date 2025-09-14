package com.backend.immilog.interaction.application.services;

import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

public interface InteractionDeleteService {

    void delete(String id);

    @Service
    @RequiredArgsConstructor
    class Impl implements InteractionDeleteService {
        private final InteractionUserRepository interactionUserRepository;

        @Override
        public void delete(String id) {
            interactionUserRepository.deleteById(id);
        }

    }
}
