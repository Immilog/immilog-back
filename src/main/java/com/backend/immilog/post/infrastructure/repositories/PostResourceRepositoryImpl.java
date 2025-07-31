package com.backend.immilog.post.infrastructure.repositories;

import com.backend.immilog.post.domain.model.post.PostType;
import com.backend.immilog.post.domain.model.resource.PostResource;
import com.backend.immilog.post.domain.model.resource.ResourceType;
import com.backend.immilog.post.domain.repositories.PostResourceRepository;
import com.backend.immilog.post.infrastructure.jdbc.PostResourceJdbcRepository;
import com.backend.immilog.post.infrastructure.jpa.entity.resource.PostResourceEntity;
import com.backend.immilog.post.infrastructure.jpa.repository.PostResourceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostResourceRepositoryImpl implements PostResourceRepository {
    private final PostResourceJdbcRepository postResourceJdbcRepository;
    private final PostResourceJpaRepository postResourceJpaRepository;

    public PostResourceRepositoryImpl(
            PostResourceJdbcRepository postResourceJdbcRepository,
            PostResourceJpaRepository postResourceJpaRepository
    ) {
        this.postResourceJdbcRepository = postResourceJdbcRepository;
        this.postResourceJpaRepository = postResourceJpaRepository;
    }

    @Override
    public void deleteAllEntities(
            String postId,
            PostType postType,
            ResourceType resourceType,
            List<String> deleteAttachments
    ) {
        postResourceJdbcRepository.deleteAllEntities(
                postId,
                postType,
                resourceType,
                deleteAttachments
        );
    }

    @Override
    public void deleteAllByPostId(String id) {
        postResourceJdbcRepository.deleteAllByPostId(id);
    }

    @Override
    public List<PostResource> findAllByPostId(String id) {
        return postResourceJpaRepository.findAllByPostId(id)
                .stream()
                .map(PostResourceEntity::toDomain)
                .toList();
    }

    @Override
    public List<PostResource> findAllByPostIdList(
            List<String> postIdList,
            PostType postType
    ) {
        return postResourceJdbcRepository.findAllByPostIdList(postIdList, postType);
    }
}
