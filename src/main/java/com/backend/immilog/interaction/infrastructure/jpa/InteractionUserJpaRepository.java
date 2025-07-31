package com.backend.immilog.interaction.infrastructure.jpa;

import com.backend.immilog.post.domain.model.post.PostType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteractionUserJpaRepository extends JpaRepository<InteractionUserEntity, String> {
    List<InteractionUserEntity> findByPostIdInAndPostType(
            List<String> postIdList,
            PostType postType
    );

    List<InteractionUserEntity> findByUserIdAndPostType(
            String userId,
            PostType postType
    );
}