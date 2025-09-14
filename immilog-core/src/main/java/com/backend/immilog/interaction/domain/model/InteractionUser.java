package com.backend.immilog.interaction.domain.model;

import com.backend.immilog.shared.enums.ContentType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record InteractionUser(
        String id,
        String userId,
        String postId,
        ContentType contentType,
        InteractionType interactionType,
        InteractionStatus interactionStatus,
        LocalDateTime createdAt
) {
    public static InteractionUser of(
            String userId,
            String postId,
            ContentType contentType,
            InteractionType interactionType
    ) {
        return InteractionUser.builder()
                .userId(userId)
                .postId(postId)
                .contentType(contentType)
                .interactionType(interactionType)
                .interactionStatus(InteractionStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    public static InteractionUser createBookmark(
            String userId,
            String postId,
            ContentType contentType
    ) {
        return of(userId, postId, contentType, InteractionType.BOOKMARK);
    }
    
    public static InteractionUser createLike(
            String userId,
            String postId,
            ContentType contentType
    ) {
        return of(userId, postId, contentType, InteractionType.LIKE);
    }

    public InteractionUser toggleStatus() {
        return InteractionUser.builder()
                .id(this.id)
                .userId(this.userId)
                .postId(this.postId)
                .contentType(this.contentType)
                .interactionType(this.interactionType)
                .interactionStatus(this.interactionStatus.isActive() ? InteractionStatus.INACTIVE : InteractionStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }
}