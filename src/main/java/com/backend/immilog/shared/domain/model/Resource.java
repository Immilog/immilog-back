package com.backend.immilog.shared.domain.model;

import com.backend.immilog.post.domain.model.post.PostType;

public class Resource {
    private final String id;
    private final String entityId;
    private final PostType entityType;
    private final ResourceType resourceType;
    private final String content;

    public Resource(
            String id,
            String entityId,
            PostType entityType,
            ResourceType resourceType,
            String content
    ) {
        this.id = id;
        this.entityId = entityId;
        this.entityType = entityType;
        this.resourceType = resourceType;
        this.content = content;
    }

    public static Resource of(
            PostType entityType,
            ResourceType resourceType,
            String content,
            String entityId
    ) {
        return new Resource(null, entityId, entityType, resourceType, content);
    }

    public String id() {return id;}

    public String entityId() {return entityId;}

    public PostType entityType() {return entityType;}

    public ResourceType resourceType() {return resourceType;}

    public String content() {return content;}
}