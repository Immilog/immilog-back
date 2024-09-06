package com.backend.immilog.post.domain.model;

import com.backend.immilog.post.domain.enums.PostType;
import com.backend.immilog.post.domain.enums.ResourceType;
import lombok.Builder;

@Builder
public record PostResource(
        Long seq,
        Long postSeq,
        PostType postType,
        ResourceType resourceType,
        String content
) {
    public static PostResource of(
            PostType postType,
            ResourceType resourceType,
            String content,
            Long postSeq
    ) {
        return PostResource.builder()
                .postType(postType)
                .resourceType(resourceType)
                .content(content)
                .postSeq(postSeq)
                .build();
    }
}
