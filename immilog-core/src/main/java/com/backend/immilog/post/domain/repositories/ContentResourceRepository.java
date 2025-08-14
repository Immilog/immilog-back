package com.backend.immilog.post.domain.repositories;

import com.backend.immilog.post.domain.model.resource.ContentResource;
import com.backend.immilog.post.domain.model.resource.ResourceType;
import com.backend.immilog.shared.enums.ContentType;

import java.util.List;

public interface ContentResourceRepository {
    void deleteAllEntities(
            String postId,
            ContentType contentType,
            ResourceType resourceType,
            List<String> deleteAttachments
    );

    void deleteAllByContentId(String id);

    List<ContentResource> findAllByContentId(String id);

    List<ContentResource> findAllByContentIdList(
            List<String> postIdList,
            ContentType contentType
    );
}
