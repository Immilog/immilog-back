package com.backend.immilog.post.infrastructure.jpa.entity.resource;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.backend.immilog.post.domain.model.post.PostType;
import com.backend.immilog.post.domain.model.resource.PostResource;
import com.backend.immilog.post.domain.model.resource.ResourceType;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@Entity
@Table(name = "post_resource")
public class PostResourceEntity {
    @Id
    @Column(name = "post_resource_id")
    private String id;

    @Column(name = "post_id")
    private String postId;

    @Column(name = "post_type")
    @Enumerated(EnumType.STRING)
    private PostType postType;

    @Getter
    @Column(name = "resource_type")
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    @Getter
    @Column(name = "content")
    private String content;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = NanoIdUtils.randomNanoId();
        }
    }

    protected PostResourceEntity() {}

    protected PostResourceEntity(
            String id,
            String postId,
            PostType postType,
            ResourceType resourceType,
            String content
    ) {
        this.id = id;
        this.postId = postId;
        this.postType = postType;
        this.resourceType = resourceType;
        this.content = content;
    }

    public static PostResourceEntity from(PostResource postResource) {
        return new PostResourceEntity(
                postResource.id(),
                postResource.postId(),
                postResource.postType(),
                postResource.resourceType(),
                postResource.content()
        );
    }

    public PostResource toDomain() {
        return new PostResource(
                this.id,
                this.postId,
                this.postType,
                this.resourceType,
                this.content
        );
    }
}
