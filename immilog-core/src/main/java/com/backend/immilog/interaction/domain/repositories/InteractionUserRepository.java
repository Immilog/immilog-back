package com.backend.immilog.interaction.domain.repositories;

import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.shared.enums.ContentType;

import java.util.List;
import java.util.Optional;

public interface InteractionUserRepository {
    List<InteractionUser> findByPostIdListAndContentTypeAndInteractionStatus(
            List<String> postIdList,
            ContentType contentType,
            InteractionStatus interactionStatus
    );

    List<InteractionUser> findBookmarksByUserIdAndContentTypeAndInteractionStatus(
            String userId,
            ContentType contentType,
            InteractionStatus interactionStatus
    );

    Optional<InteractionUser> findById(String id);

    InteractionUser save(InteractionUser interactionUser);

    void deleteById(String id);

    Optional<InteractionUser> findByUserIdAndInteractionTypeAndContentTypeAndPostId(
            String userId,
            InteractionType interactionType,
            ContentType contentType,
            String postId
    );
    
    Optional<InteractionUser> findByUserIdAndPostIdAndContentTypeAndInteractionType(
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

    Long countByCommentIdAndInteractionTypeAndInteractionStatus(
            String commentId,
            InteractionType interactionType,
            InteractionStatus interactionStatus
    );
}