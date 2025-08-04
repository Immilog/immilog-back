package com.backend.immilog.interaction.infrastructure.repositories;

import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import com.backend.immilog.interaction.infrastructure.jpa.InteractionUserEntity;
import com.backend.immilog.interaction.infrastructure.jpa.InteractionUserJpaRepository;
import com.backend.immilog.shared.enums.ContentType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InteractionUserRepositoryImpl implements InteractionUserRepository {
    private final InteractionUserJpaRepository interactionUserJpaRepository;

    public InteractionUserRepositoryImpl(InteractionUserJpaRepository interactionUserJpaRepository) {
        this.interactionUserJpaRepository = interactionUserJpaRepository;
    }

    @Override
    public List<InteractionUser> findByPostIdListAndContentType(
            List<String> postIdList,
            ContentType contentType
    ) {
        return interactionUserJpaRepository.findByPostIdInAndContentType(postIdList, contentType)
                .stream()
                .map(InteractionUserEntity::toDomain)
                .toList();
    }

    @Override
    public List<InteractionUser> findBookmarksByUserIdAndContentType(
            String userId,
            ContentType contentType
    ) {
        return interactionUserJpaRepository.findByUserIdAndContentType(userId, contentType)
                .stream()
                .map(InteractionUserEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<InteractionUser> findById(String id) {
        return interactionUserJpaRepository.findById(id)
                .map(InteractionUserEntity::toDomain);
    }

    @Override
    public InteractionUser save(InteractionUser interactionUser) {
        var entity = interactionUserJpaRepository.save(InteractionUserEntity.from(interactionUser));
        return entity.toDomain();
    }

    @Override
    public void deleteById(String id) {
        interactionUserJpaRepository.deleteById(id);
    }
}