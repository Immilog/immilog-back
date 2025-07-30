package com.backend.immilog.comment.domain.repositories;

import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.comment.domain.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    List<CommentResult> findCommentsByPostId(String postId);

    Optional<Comment> findById(String commentId);

    Comment save(Comment comment);

    void deleteById(String commentId);
}
