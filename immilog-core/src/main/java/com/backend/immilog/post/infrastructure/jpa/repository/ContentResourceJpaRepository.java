package com.backend.immilog.post.infrastructure.jpa.repository;

import com.backend.immilog.post.infrastructure.jpa.entity.resource.ContentResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentResourceJpaRepository extends JpaRepository<ContentResourceEntity, String> {
    List<ContentResourceEntity> findAllByContentId(String id);
}

