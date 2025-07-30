package com.backend.immilog.comment.presentation.request;

import com.backend.immilog.comment.domain.model.ReferenceType;

public record CommentCreateRequest(
        String postId,
        String content,
        ReferenceType referenceType
) {
}