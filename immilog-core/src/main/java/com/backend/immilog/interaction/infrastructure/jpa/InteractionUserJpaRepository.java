package com.backend.immilog.interaction.infrastructure.jpa;

import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.shared.enums.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    
    @Query("SELECT iu FROM InteractionUserEntity iu WHERE iu.userId = :userId AND iu.contentType = :contentType AND iu.interactionType = :interactionType AND iu.interactionStatus = :interactionStatus")
    List<InteractionUserEntity> findByUserIdAndContentTypeAndInteractionTypeAndInteractionStatus(
            @Param("userId") String userId,
            @Param("contentType") ContentType contentType,
            @Param("interactionType") InteractionType interactionType,
            @Param("interactionStatus") InteractionStatus interactionStatus
    );

    Optional<InteractionUserEntity> findByUserIdAndInteractionTypeAndContentTypeAndPostId(
            String userId,
            InteractionType interactionType,
            ContentType contentType,
            String postId
    );
    
    Optional<InteractionUserEntity> findByUserIdAndPostIdAndContentTypeAndInteractionType(
            String userId,
            String postId,
            ContentType contentType,
            InteractionType interactionType
    );

    Long countByPostIdAndInteractionTypeAndInteractionStatus(
            String postId,
            InteractionType interactionType,
            InteractionStatus interactionStatus
    );

    List<InteractionUserEntity> findByPostIdInAndContentTypeAndInteractionStatus(
            List<String> postIdList,
            ContentType contentType,
            InteractionStatus interactionStatus
    );
}