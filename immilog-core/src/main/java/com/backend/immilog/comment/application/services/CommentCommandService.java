package com.backend.immilog.comment.application.services;

import com.backend.immilog.comment.domain.model.Comment;
import com.backend.immilog.comment.domain.repositories.CommentRepository;
import com.backend.immilog.comment.exception.CommentErrorCode;
import com.backend.immilog.comment.exception.CommentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentCommandService {
    private final CommentRepository commentRepository;

    public CommentCommandService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(
            String commentId,
            String content
    ) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));
        return commentRepository.save(comment.updateContent(content));
    }

    @Transactional
    public void deleteComment(String commentId) {
        commentRepository.deleteById(commentId);
    }
}