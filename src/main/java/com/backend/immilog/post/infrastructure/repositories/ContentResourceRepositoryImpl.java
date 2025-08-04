package com.backend.immilog.post.infrastructure.repositories;

import com.backend.immilog.post.domain.model.resource.ContentResource;
import com.backend.immilog.post.domain.model.resource.ResourceType;
import com.backend.immilog.post.domain.repositories.ContentResourceRepository;
import com.backend.immilog.post.infrastructure.jdbc.ContentResourceJdbcRepository;
import com.backend.immilog.post.infrastructure.jpa.entity.resource.ContentResourceEntity;
import com.backend.immilog.post.infrastructure.jpa.repository.ContentResourceJpaRepository;
import com.backend.immilog.shared.enums.ContentType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ContentResourceRepositoryImpl implements ContentResourceRepository {
    private final ContentResourceJdbcRepository contentResourceJdbcRepository;
    private final ContentResourceJpaRepository contentResourceJpaRepository;

    public ContentResourceRepositoryImpl(
            ContentResourceJdbcRepository contentResourceJdbcRepository,
            ContentResourceJpaRepository contentResourceJpaRepository
    ) {
        this.contentResourceJdbcRepository = contentResourceJdbcRepository;
        this.contentResourceJpaRepository = contentResourceJpaRepository;
    }

    @Override
    public void deleteAllEntities(
            String postId,
            ContentType contentType,
            ResourceType resourceType,
            List<String> deleteAttachments
    ) {
        contentResourceJdbcRepository.deleteAllEntities(
                postId,
                contentType,
                resourceType,
                deleteAttachments
        );
    }

    @Override
    public void deleteAllByContentId(String id) {
        contentResourceJdbcRepository.deleteAllByPostId(id);
    }

    @Override
    public List<ContentResource> findAllByContentId(String id) {
        return contentResourceJpaRepository.findAllByContentId(id)
                .stream()
                .map(ContentResourceEntity::toDomain)
                .toList();
    }

    @Override
    public List<ContentResource> findAllByContentIdList(
            List<String> postIdList,
            ContentType contentType
    ) {
        return contentResourceJdbcRepository.findAllByPostIdList(postIdList, contentType);
    }
}
