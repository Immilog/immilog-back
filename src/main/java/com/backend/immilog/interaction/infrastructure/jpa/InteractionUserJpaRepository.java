package com.backend.immilog.interaction.infrastructure.jpa;

import com.backend.immilog.shared.enums.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteractionUserJpaRepository extends JpaRepository<InteractionUserEntity, String> {
    List<InteractionUserEntity> findByPostIdInAndContentType(
            List<String> postIdList,
            ContentType contentType
    );

    List<InteractionUserEntity> findByUserIdAndContentType(
            String userId,
            ContentType contentType
    );
}