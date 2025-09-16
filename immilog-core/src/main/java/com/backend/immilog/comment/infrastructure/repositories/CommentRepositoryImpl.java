package com.backend.immilog.comment.infrastructure.repositories;

import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.comment.domain.model.Comment;
import com.backend.immilog.comment.domain.repositories.CommentRepository;
import com.backend.immilog.comment.infrastructure.jdbc.CommentJdbcRepository;
import com.backend.immilog.comment.infrastructure.jpa.CommentEntity;
import com.backend.immilog.comment.infrastructure.jpa.CommentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {
    private final CommentJdbcRepository commentJdbcRepository;
    private final CommentJpaRepository commentJpaRepository;

    @Override
    public List<CommentResult> findCommentsByPostId(String postId) {
        return commentJdbcRepository.findCommentsByPostId(postId);
    }

    @Override
    public Optional<Comment> findById(String commentId) {
        return commentJpaRepository.findById(commentId).map(CommentEntity::toDomain);
    }

    @Override
    public Comment save(Comment comment) {
        var entity = commentJpaRepository.save(CommentEntity.from(comment));
        return entity.toDomain();
    }

    @Override
    public void deleteById(String commentId) {
        commentJpaRepository.deleteById(commentId);
    }

    @Override
    public CommentResult getCommentById(String commentId) {
        return commentJdbcRepository.findCommentById(commentId);
    }
}
