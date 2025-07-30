package com.backend.immilog.comment.infrastructure.jpa;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.backend.immilog.comment.domain.model.Comment;
import com.backend.immilog.comment.domain.model.CommentRelation;
import com.backend.immilog.comment.domain.model.ReferenceType;
import com.backend.immilog.post.domain.model.post.PostStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;

@DynamicUpdate
@Entity
@Table(name = "comment")
public class CommentEntity {
    @Id
    @Column(name = "comment_id")
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "post_id", nullable = false)
    private String postId;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "content", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type")
    private ReferenceType referenceType;

    @Column(name = "reply_count")
    private int replyCount = 0;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PostStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = NanoIdUtils.randomNanoId();
        }
    }

    protected CommentEntity() {}

    public CommentEntity(
            String id,
            String userId,
            String postId,
            String parentId,
            String content,
            ReferenceType referenceType,
            int replyCount,
            Integer likeCount,
            PostStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.parentId = parentId;
        this.content = content;
        this.referenceType = referenceType;
        this.replyCount = replyCount;
        this.likeCount = likeCount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CommentEntity from(Comment comment) {
        return new CommentEntity(
                comment.id(),
                comment.userId(),
                comment.postId() != null ? comment.postId() : null,
                comment.parentId() != null ? comment.parentId() : null,
                comment.content(),
                comment.referenceType(),
                comment.replyCount(),
                comment.likeCount(),
                comment.status(),
                comment.createdAt(),
                comment.updatedAt()
        );
    }

    public Comment toDomain() {
        return new Comment(
                id,
                userId,
                content,
                CommentRelation.of(
                        postId != null ? postId : null,
                        parentId != null ? parentId : null,
                        referenceType
                ),
                replyCount,
                likeCount,
                status,
                new ArrayList<>(),
                createdAt,
                updatedAt
        );
    }
}