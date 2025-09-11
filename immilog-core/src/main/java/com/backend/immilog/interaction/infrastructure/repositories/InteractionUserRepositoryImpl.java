package com.backend.immilog.interaction.infrastructure.repositories;

import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionType;
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
    public List<InteractionUser> findByPostIdListAndContentTypeAndInteractionStatus(
            List<String> postIdList,
            ContentType contentType,
            InteractionStatus interactionStatus
    ) {
        return interactionUserJpaRepository.findByPostIdInAndContentTypeAndInteractionStatus(postIdList, contentType, interactionStatus)
                .stream()
                .map(InteractionUserEntity::toDomain)
                .toList();
    }

    @Override
    public List<InteractionUser> findBookmarksByUserIdAndContentTypeAndInteractionStatus(
            String userId,
            ContentType contentType,
            InteractionStatus interactionStatus
    ) {
        return interactionUserJpaRepository.findByUserIdAndContentTypeAndInteractionTypeAndInteractionStatus(
                        userId,
                        contentType,
                        InteractionType.BOOKMARK,
                        interactionStatus
                )
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

    @Override
    public Optional<InteractionUser> findByUserIdAndInteractionTypeAndContentTypeAndPostId(
            String userId,
            InteractionType interactionType,
            ContentType contentType,
            String postId
    ) {
        return interactionUserJpaRepository.findByUserIdAndInteractionTypeAndContentTypeAndPostId(
                        userId,
                        interactionType,
                        contentType,
                        postId
                )
                .map(InteractionUserEntity::toDomain);
    }

    @Override
    public Optional<InteractionUser> findByUserIdAndPostIdAndContentTypeAndInteractionType(
            String userId,
            String postId,
            ContentType contentType,
            InteractionType interactionType
    ) {
        return interactionUserJpaRepository.findByUserIdAndPostIdAndContentTypeAndInteractionType(
                        userId,
                        postId,
                        contentType,
                        interactionType
                )
                .map(InteractionUserEntity::toDomain);
    }

    @Override
    public Long countByPostIdAndInteractionTypeAndInteractionStatus(
            String postId,
            InteractionType interactionType,
            InteractionStatus interactionStatus
    ) {
        return interactionUserJpaRepository.countByPostIdAndInteractionTypeAndInteractionStatus(
                postId,
                interactionType,
                interactionStatus
        );
    }

    @Override
    public Long countByCommentIdAndInteractionTypeAndInteractionStatus(
            String commentId,
            InteractionType interactionType,
            InteractionStatus interactionStatus
    ) {
        return interactionUserJpaRepository.countByPostIdAndInteractionTypeAndInteractionStatus(
                commentId,
                interactionType,
                interactionStatus
        );
    }
}