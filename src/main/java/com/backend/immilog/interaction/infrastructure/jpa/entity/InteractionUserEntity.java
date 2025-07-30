package com.backend.immilog.interaction.infrastructure.jpa.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.post.domain.model.post.PostType;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@DynamicUpdate
@Entity
@Table(name = "interaction_user")
public class InteractionUserEntity {
    @Id
    @Column(name = "interaction_user_id")
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "post_id", nullable = false)
    private String postId;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private PostType postType;

    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_type", nullable = false)
    private InteractionType interactionType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = NanoIdUtils.randomNanoId();
        }
    }

    protected InteractionUserEntity() {}

    public InteractionUserEntity(
            String id,
            String userId,
            String postId,
            PostType postType,
            InteractionType interactionType,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.postType = postType;
        this.interactionType = interactionType;
        this.createdAt = createdAt;
    }

    public static InteractionUserEntity from(InteractionUser interactionUser) {
        return new InteractionUserEntity(
                interactionUser.id(),
                interactionUser.userId(),
                interactionUser.postId(),
                interactionUser.postType(),
                interactionUser.interactionType(),
                interactionUser.createdAt()
        );
    }

    public InteractionUser toDomain() {
        return new InteractionUser(
                id,
                userId,
                postId,
                postType,
                interactionType,
                createdAt
        );
    }
}