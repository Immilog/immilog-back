package com.backend.immilog.comment.domain.service;

import com.backend.immilog.comment.domain.model.Comment;
import com.backend.immilog.comment.domain.model.ReferenceType;
import org.springframework.stereotype.Service;

@Service
public class CommentDomainService {

    public Comment createComment(
            String userId,
            String postId,
            String content,
            String parentId,
            ReferenceType referenceType
    ) {
        return Comment.of(userId, postId, content, parentId, referenceType);
    }

    public void publishCommentCreatedEvent(Comment comment) {
        comment.publishCreatedEvent();
    }
}