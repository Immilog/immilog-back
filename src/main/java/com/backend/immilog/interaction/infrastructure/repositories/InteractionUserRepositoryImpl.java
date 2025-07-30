package com.backend.immilog.interaction.infrastructure.repositories;

import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import com.backend.immilog.interaction.infrastructure.jpa.entity.InteractionUserEntity;
import com.backend.immilog.interaction.infrastructure.jpa.repository.InteractionUserJpaRepository;
import com.backend.immilog.post.domain.model.post.PostType;
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
    public List<InteractionUser> findByPostIdListAndPostType(
            List<String> postIdList,
            PostType postType
    ) {
        return interactionUserJpaRepository.findByPostIdInAndPostType(postIdList, postType)
                .stream()
                .map(InteractionUserEntity::toDomain)
                .toList();
    }

    @Override
    public List<InteractionUser> findBookmarksByUserIdAndPostType(
            String userId,
            PostType postType
    ) {
        return interactionUserJpaRepository.findByUserIdAndPostType(userId, postType)
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