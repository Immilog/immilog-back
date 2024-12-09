package com.backend.immilog.post.application.services;

import com.backend.immilog.global.infrastructure.lock.RedisDistributedLock;
import com.backend.immilog.post.application.command.PostUpdateCommand;
import com.backend.immilog.post.domain.model.Post;
import com.backend.immilog.post.domain.model.enums.PostType;
import com.backend.immilog.post.domain.model.enums.ResourceType;
import com.backend.immilog.post.domain.repositories.BulkInsertRepository;
import com.backend.immilog.post.domain.repositories.PostRepository;
import com.backend.immilog.post.domain.repositories.PostResourceRepository;
import com.backend.immilog.post.exception.PostException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.backend.immilog.post.domain.model.enums.PostType.POST;
import static com.backend.immilog.post.domain.model.enums.ResourceType.ATTACHMENT;
import static com.backend.immilog.post.domain.model.enums.ResourceType.TAG;
import static com.backend.immilog.post.exception.PostErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostUpdateService {
    private final PostRepository postRepository;
    private final PostResourceRepository postResourceRepository;
    private final BulkInsertRepository bulkInsertRepository;

    private final RedisDistributedLock redisDistributedLock;
    final String VIEW_LOCK_KEY = "viewPost : ";

    @Transactional
    public void updatePost(
            Long userId,
            Long postSeq,
            PostUpdateCommand postUpdateCommand
    ) {
        Post post = getPost(postSeq);
        validateAuthor(userId, post);
        post = updatePostMetaData(post, postUpdateCommand);
        updateResources(postSeq, postUpdateCommand);
        postRepository.save(post);
    }

    @Async
    @Transactional
    public void increaseViewCount(Long postSeq) {
        executeWithLock(
                VIEW_LOCK_KEY,
                postSeq.toString(),
                () -> {
                    Post post = getPost(postSeq);
                    Long currentViewCount = post.postMetaData().getViewCount();
                    post.postMetaData().setViewCount(currentViewCount + 1);
                }
        );
    }

    private void executeWithLock(
            String lockKey,
            String subKey,
            Runnable action
    ) {
        boolean lockAcquired = false;
        try {
            lockAcquired = redisDistributedLock.tryAcquireLock(lockKey, subKey);
            if (lockAcquired) {
                action.run();
            } else {
                log.error("Failed to acquire lock for {}, key: {}", lockKey, subKey);
            }
        } finally {
            if (lockAcquired) {
                redisDistributedLock.releaseLock(lockKey, subKey);
            }
        }
    }

    private void updateResources(
            Long postSeq,
            PostUpdateCommand request
    ) {
        updateResource(postSeq, request.deleteTags(), request.addTags(), TAG);
        updateResource(postSeq, request.deleteAttachments(), request.addAttachments(), ATTACHMENT);
    }

    private void updateResource(
            Long postSeq,
            List<String> deleteResources,
            List<String> addResources,
            ResourceType resourceType
    ) {
        deleteResourceIfExists(postSeq, POST, deleteResources, resourceType);
        addResourceIfExists(postSeq, POST, addResources, resourceType);
    }

    private void deleteResourceIfExists(
            Long postSeq,
            PostType postType,
            List<String> deleteResources,
            ResourceType resourceType
    ) {
        if (deleteResources != null && !deleteResources.isEmpty()) {
            postResourceRepository.deleteAllEntities(
                    postSeq,
                    postType,
                    resourceType,
                    deleteResources
            );
        }
    }

    private void addResourceIfExists(
            Long postSeq,
            PostType postType,
            List<String> addResources,
            ResourceType resourceType
    ) {
        if (addResources != null && !addResources.isEmpty()) {
            bulkInsertRepository.saveAll(
                    addResources,
                    """
                    INSERT INTO post_resource (
                        post_seq,
                        post_type,
                        resource_type,
                        content
                    ) VALUES (?, ?, ?, ?)
                    """,
                    (ps, resource) -> {
                        try {
                            ps.setLong(1, postSeq);
                            ps.setString(2, postType.name());
                            ps.setString(3, resourceType.name());
                            ps.setString(4, resource);
                        } catch (Exception e) {
                            log.error("Failed to save post resource", e);
                            throw new PostException(FAILED_TO_SAVE_POST);
                        }
                    }
            );
        }
    }

    private Post updatePostMetaData(
            Post post,
            PostUpdateCommand request
    ) {
        if (request.title() != null) {
            post = post.copyWithNewTitle(request.title());
        }
        if (request.content() != null) {
            post = post.copyWithNewContent(request.content());
        }
        if (request.isPublic() != null) {
            post = post.copyWithNewIsPublic(request.isPublic() ? "Y" : "N");
        }
        return post;
    }

    private void validateAuthor(
            Long userId,
            Post post
    ) {
        if (!Objects.equals(post.postUserData().getUserSeq(), userId)) {
            throw new PostException(NO_AUTHORITY);
        }
    }

    private Post getPost(
            Long postSeq
    ) {
        return postRepository.getById(postSeq)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    }
}
