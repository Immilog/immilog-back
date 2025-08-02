package com.backend.immilog.post.infrastructure.jpa.entity.resource;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.backend.immilog.post.domain.model.resource.PostResource;
import com.backend.immilog.post.domain.model.resource.ResourceType;
import com.backend.immilog.shared.enums.ContentType;
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
    private ContentType contentType;

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
            ContentType contentType,
            ResourceType resourceType,
            String content
    ) {
        this.id = id;
        this.postId = postId;
        this.contentType = contentType;
        this.resourceType = resourceType;
        this.content = content;
    }

    public static PostResourceEntity from(PostResource postResource) {
        return new PostResourceEntity(
                postResource.id(),
                postResource.postId(),
                postResource.contentType(),
                postResource.resourceType(),
                postResource.content()
        );
    }

    public PostResource toDomain() {
        return new PostResource(
                this.id,
                this.postId,
                this.contentType,
                this.resourceType,
                this.content
        );
    }
}
