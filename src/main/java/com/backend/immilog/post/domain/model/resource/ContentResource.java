package com.backend.immilog.post.domain.model.resource;

import com.backend.immilog.shared.enums.ContentType;

import java.beans.ConstructorProperties;

public class ContentResource {
    private final String postResourceId;
    private final String postId;
    private final ContentType contentType;
    private final ResourceType resourceType;
    private final String content;

    @ConstructorProperties({"postResourceId", "postId", "contentType", "resourceType", "content"})
    public ContentResource(
            String postResourceId,
            String postId,
            ContentType contentType,
            ResourceType resourceType,
            String content
    ) {
        this.postResourceId = postResourceId;
        this.postId = postId;
        this.contentType = contentType;
        this.resourceType = resourceType;
        this.content = content;
    }

    public static ContentResource of(
            ContentType contentType,
            ResourceType resourceType,
            String content,
            String postId
    ) {
        return new ContentResource(null, postId, contentType, resourceType, content);
    }

    public String id() {return postResourceId;}

    public String postId() {return postId;}

    public ContentType contentType() {return contentType;}

    public ResourceType resourceType() {return resourceType;}

    public String content() {return content;}
}
