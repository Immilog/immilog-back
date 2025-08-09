package com.backend.immilog.interaction.infrastructure.jpa;

import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.shared.enums.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InteractionUserJpaRepository extends JpaRepository<InteractionUserEntity, String> {
    List<InteractionUserEntity> findByPostIdInAndContentType(
            List<String> postIdList,
            ContentType contentType
    );

    List<InteractionUserEntity> findByUserIdAndContentType(
            String userId,
            ContentType contentType
    );

    Optional<InteractionUserEntity> findByUserIdAndInteractionTypeAndContentTypeAndPostId(
            String userId,
            InteractionType interactionType,
            ContentType contentType,
            String postId
    );

    Long countByPostIdAndInteractionTypeAndInteractionStatus(
            String postId,
            InteractionType interactionType,
            InteractionStatus interactionStatus
    );
}