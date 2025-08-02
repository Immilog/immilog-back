package com.backend.immilog.post.domain.model.resource;

import com.backend.immilog.shared.enums.ContentType;

public class PostResource {
    private final String resourceId;
    private final String postId;
    private final ContentType contentType;
    private final ResourceType resourceType;
    private final String content;

    public PostResource(
            String resourceId,
            String postId,
            ContentType contentType,
            ResourceType resourceType,
            String content
    ) {
        this.resourceId = resourceId;
        this.postId = postId;
        this.contentType = contentType;
        this.resourceType = resourceType;
        this.content = content;
    }

    public static PostResource of(
            ContentType contentType,
            ResourceType resourceType,
            String content,
            String postId
    ) {
        return new PostResource(null, postId, contentType, resourceType, content);
    }

    public String id() {return resourceId;}

    public String postId() {return postId;}

    public ContentType contentType() {return contentType;}

    public ResourceType resourceType() {return resourceType;}

    public String content() {return content;}
}
