package com.backend.immilog.comment.presentation.payload;

import com.backend.immilog.comment.application.dto.CommentCreateCommand;
import com.backend.immilog.comment.domain.model.ReferenceType;

public record CommentCreateRequest(
        String postId,
        String content,
        ReferenceType referenceType
) {
    public CommentCreateCommand toCommand(String userId) {
        return new CommentCreateCommand(userId, postId, content, referenceType);
    }
}