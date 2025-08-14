package com.backend.immilog.post.application.services;

import com.backend.immilog.post.domain.model.resource.ContentResource;
import com.backend.immilog.post.domain.repositories.ContentResourceRepository;
import com.backend.immilog.shared.enums.ContentType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostResourceQueryService {
    private final ContentResourceRepository contentResourceRepository;

    public PostResourceQueryService(ContentResourceRepository contentResourceRepository) {
        this.contentResourceRepository = contentResourceRepository;
    }

    @Transactional(readOnly = true)
    public List<ContentResource> getResourcesByPostIdList(
            List<String> postIdList,
            ContentType contentType
    ) {
        return contentResourceRepository.findAllByContentIdList(postIdList, contentType);
    }
}
