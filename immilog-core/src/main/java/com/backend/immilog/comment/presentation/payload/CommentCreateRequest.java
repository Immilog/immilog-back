package com.backend.immilog.comment.presentation.payload;

import com.backend.immilog.comment.application.dto.CommentCreateCommand;
import com.backend.immilog.comment.domain.model.ReferenceType;
import io.swagger.v3.oas.annotations.media.Schema;

public record CommentCreateRequest(
        String postId,
        String content,
        String parentId,
        ReferenceType referenceType
) {
    public CommentCreateCommand toCommand(String userId) {
        return new CommentCreateCommand(userId, postId, content, parentId, referenceType);
    }
}