package com.backend.immilog.interaction.infrastructure.jpa;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.shared.enums.ContentType;
import jakarta.persistence.*;
import lombok.Builder;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@DynamicUpdate
@Entity
@Table(name = "interaction_user")
public class InteractionUserEntity {
    @Id
    @Column(name = "interaction_user_id")
    private String interactionUserId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "post_id", nullable = false)
    private String postId;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private ContentType contentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_type", nullable = false)
    private InteractionType interactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_status", nullable = false)
    private InteractionStatus interactionStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void generateId() {
        if (this.interactionUserId == null) {
            this.interactionUserId = NanoIdUtils.randomNanoId();
        }
    }

    protected InteractionUserEntity() {}

    @Builder
    public InteractionUserEntity(
            String interactionUserId,
            String userId,
            String postId,
            ContentType contentType,
            InteractionType interactionType,
            InteractionStatus interactionStatus,
            LocalDateTime createdAt
    ) {
        this.interactionUserId = interactionUserId;
        this.userId = userId;
        this.postId = postId;
        this.contentType = contentType;
        this.interactionType = interactionType;
        this.interactionStatus = interactionStatus;
        this.createdAt = createdAt;
    }

    public static InteractionUserEntity from(InteractionUser interactionUser) {
        return InteractionUserEntity.builder()
                .interactionUserId(interactionUser.id())
                .userId(interactionUser.userId())
                .postId(interactionUser.postId())
                .contentType(interactionUser.contentType())
                .interactionType(interactionUser.interactionType())
                .interactionStatus(interactionUser.interactionStatus())
                .createdAt(interactionUser.createdAt())
                .build();
    }

    public InteractionUser toDomain() {
        return InteractionUser.builder()
                .id(this.interactionUserId)
                .userId(this.userId)
                .postId(this.postId)
                .contentType(this.contentType)
                .interactionType(this.interactionType)
                .interactionStatus(this.interactionStatus)
                .createdAt(this.createdAt)
                .build();
    }
}