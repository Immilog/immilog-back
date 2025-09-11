package com.backend.immilog.post.infrastructure.jpa.entity.post;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.backend.immilog.post.domain.model.post.*;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@DynamicUpdate
@DynamicInsert
@Entity
@Table(name = "post")
public class PostEntity {

    @Id
    @Column(name = "post_id")
    private String id;

    @Embedded
    private PostUserInfoValue postUserInfo;

    @Embedded
    private PostInfoValue postInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Categories category;

    @Column(name = "is_public")
    private String isPublic;

    @Enumerated(EnumType.STRING)
    @Column(name = "badge")
    private Badge badge;

    @Column(name = "comment_count")
    private Long commentCount;

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

    protected PostEntity() {}

    public PostEntity(
            String id,
            PostUserInfo postUserInfo,
            PostInfo postInfo,
            Categories category,
            String isPublic,
            Badge badge,
            Long commentCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        PostUserInfoValue postUserInfoValue = PostUserInfoValue.of(
                postUserInfo.userId()
        );
        PostInfoValue postInfoValue = PostInfoValue.of(
                postInfo.title(),
                postInfo.content(),
                postInfo.viewCount(),
                postInfo.countryId(),
                postInfo.region(),
                postInfo.status()
        );
        this.id = id;
        this.postUserInfo = postUserInfoValue;
        this.postInfo = postInfoValue;
        this.category = category;
        this.isPublic = isPublic;
        this.badge = badge;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PostEntity from(Post post) {
        return new PostEntity(
                post.id().value(),
                post.postUserInfo(),
                post.postInfo(),
                post.category(),
                post.isPublicValue(),
                post.badge(),
                post.commentCountValue(),
                post.createdAt(),
                post.updatedAt()
        );
    }

    public Post toDomain() {
        return new Post(
                new PostId(this.id),
                this.postUserInfo == null ? null : this.postUserInfo.toDomain(),
                this.postInfo == null ? null : this.postInfo.toDomain(),
                this.category,
                PublicStatus.fromValue(this.isPublic),
                this.badge,
                new CommentCount(this.commentCount),
                this.createdAt,
                this.updatedAt
        );
    }
}
