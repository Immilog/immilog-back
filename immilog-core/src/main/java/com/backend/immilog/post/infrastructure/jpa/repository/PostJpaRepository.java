package com.backend.immilog.post.infrastructure.jpa.repository;

import com.backend.immilog.post.domain.model.post.Badge;
import com.backend.immilog.post.infrastructure.jpa.entity.post.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostJpaRepository extends JpaRepository<PostEntity, String> {
    List<PostEntity> findAllByIdIn(List<String> postIdList);
    
    List<PostEntity> findByBadge(Badge badge);
}
