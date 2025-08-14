package com.backend.immilog.post.infrastructure.jpa.entity.resource;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.backend.immilog.post.domain.model.resource.ContentResource;
import com.backend.immilog.post.domain.model.resource.ResourceType;
import com.backend.immilog.shared.enums.ContentType;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@Entity
@Table(name = "content_resource")
public class ContentResourceEntity {
    @Id
    @Column(name = "content_resource_id")
    private String contentResourceId;

    @Column(name = "content_id")
    private String contentId;

    @Column(name = "content_type")
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
        if (this.contentResourceId == null) {
            this.contentResourceId = NanoIdUtils.randomNanoId();
        }
    }

    protected ContentResourceEntity() {}

    protected ContentResourceEntity(
            String contentResourceId,
            String contentId,
            ContentType contentType,
            ResourceType resourceType,
            String content
    ) {
        this.contentResourceId = contentResourceId;
        this.contentId = contentId;
        this.contentType = contentType;
        this.resourceType = resourceType;
        this.content = content;
    }

    public static ContentResourceEntity from(ContentResource contentResource) {
        return new ContentResourceEntity(
                contentResource.id(),
                contentResource.postId(),
                contentResource.contentType(),
                contentResource.resourceType(),
                contentResource.content()
        );
    }

    public ContentResource toDomain() {
        return new ContentResource(
                this.contentResourceId,
                this.contentId,
                this.contentType,
                this.resourceType,
                this.content
        );
    }
}
