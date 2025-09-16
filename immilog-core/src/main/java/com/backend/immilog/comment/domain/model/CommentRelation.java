package com.backend.immilog.comment.domain.model;

import lombok.Builder;

@Builder(access = lombok.AccessLevel.PRIVATE)
public record CommentRelation(
        String postId,
        String parentId,
        ReferenceType referenceType
) {
    public static CommentRelation of(
            String postId,
            String parentId,
            ReferenceType referenceType
    ) {
        return CommentRelation.builder()
                .postId(postId)
                .parentId(parentId)
                .referenceType(referenceType)
                .build();
    }
}