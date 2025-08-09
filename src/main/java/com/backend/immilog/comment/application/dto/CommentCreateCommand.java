package com.backend.immilog.comment.application.dto;

import com.backend.immilog.comment.domain.model.ReferenceType;

public record CommentCreateCommand(
        String userId,
        String postId,
        String content,
        String parentId,
        ReferenceType referenceType
) {
}