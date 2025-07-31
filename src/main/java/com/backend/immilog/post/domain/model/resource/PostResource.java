package com.backend.immilog.post.domain.model.resource;

import com.backend.immilog.post.domain.model.post.PostType;

public class PostResource {
    private final String resourceId;
    private final String postId;
    private final PostType postType;
    private final ResourceType resourceType;
    private final String content;

    public PostResource(
            String resourceId,
            String postId,
            PostType postType,
            ResourceType resourceType,
            String content
    ) {
        this.resourceId = resourceId;
        this.postId = postId;
        this.postType = postType;
        this.resourceType = resourceType;
        this.content = content;
    }

    public static PostResource of(
            PostType postType,
            ResourceType resourceType,
            String content,
            String postId
    ) {
        return new PostResource(null, postId, postType, resourceType, content);
    }

    public String id() {return resourceId;}

    public String postId() {return postId;}

    public PostType postType() {return postType;}

    public ResourceType resourceType() {return resourceType;}

    public String content() {return content;}
}
