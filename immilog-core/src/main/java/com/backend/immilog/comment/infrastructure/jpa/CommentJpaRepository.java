package com.backend.immilog.comment.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, String> {
}