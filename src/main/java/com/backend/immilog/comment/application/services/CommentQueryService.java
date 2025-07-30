package com.backend.immilog.comment.application.services;

import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.comment.domain.repositories.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentQueryService {
    private final CommentRepository commentRepository;

    public CommentQueryService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<CommentResult> getComments(String postId) {
        return commentRepository.findCommentsByPostId(postId);
    }
}
