package com.backend.immilog.comment.domain.model;

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
        return new CommentRelation(
                postId,
                parentId,
                referenceType
        );
    }
}