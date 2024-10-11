package com.backend.immilog.post.infrastructure.repositories;

import com.backend.immilog.post.application.result.CommentResult;
import com.backend.immilog.post.domain.model.Comment;
import com.backend.immilog.post.domain.repositories.CommentRepository;
import com.backend.immilog.post.infrastructure.jdbc.CommentJdbcRepository;
import com.backend.immilog.post.infrastructure.jpa.CommentEntity;
import com.backend.immilog.post.infrastructure.jpa.CommentJpaRepository;
import com.backend.immilog.post.infrastructure.result.CommentEntityResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {
    private final CommentJdbcRepository commentJdbcRepository;
    private final CommentJpaRepository commentJpaRepository;

    @Override
    public List<CommentResult> getComments(
            Long postSeq
    ) {
        return commentJdbcRepository
                .getComments(postSeq)
                .stream()
                .map(CommentEntityResult::toCommentResult)
                .toList();
    }

    @Override
    public void saveEntity(
            Comment comment
    ) {
        commentJpaRepository.save(CommentEntity.from(comment));
    }
}


