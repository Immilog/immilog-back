package com.backend.immilog.post.application.usecase;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.backend.immilog.post.application.dto.in.PostUpdateCommand;
import com.backend.immilog.post.application.services.command.BulkCommandService;
import com.backend.immilog.post.application.services.command.PostCommandService;
import com.backend.immilog.post.application.services.command.PostResourceCommandService;
import com.backend.immilog.post.application.services.query.PostQueryService;
import com.backend.immilog.post.domain.model.resource.ResourceType;
import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.shared.aop.annotation.DistributedLock;
import com.backend.immilog.shared.enums.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UpdatePostUseCase {
    void updatePost(
            String userId,
            String postId,
            PostUpdateCommand command
    );

    void increaseViewCount(String postId);

    @Slf4j
    @Service
    @RequiredArgsConstructor
    class UpdaterPost implements UpdatePostUseCase {
        private final PostQueryService postQueryService;
        private final PostCommandService postCommandService;
        private final PostResourceCommandService postResourceCommandService;
        private final BulkCommandService bulkCommandService;

        @Transactional
        public void updatePost(
                String userId,
                String postId,
                PostUpdateCommand command
        ) {
            postCommandService.updatePost(
                    postId,
                    userId,
                    command.title(),
                    command.content()
            );
            if (command.isPublic() != null) {
                postCommandService.updatePostVisibility(
                        postId,
                        userId,
                        command.isPublic()
                );
            }
            updateResource(
                    postId,
                    command.deleteTags(),
                    command.addTags(),
                    ResourceType.TAG
            );
            updateResource(
                    postId,
                    command.deleteAttachments(),
                    command.addAttachments(),
                    ResourceType.ATTACHMENT
            );
        }

        @Async
        @DistributedLock(key = "'viewPost:'", identifier = "#p0.toString()", expireTime = 5)
        public void increaseViewCount(String postId) {
            var post = postQueryService.getPostById(postId);
            var updatedPost = post.increaseViewCount();
            postCommandService.save(updatedPost);
        }

        private void updateResource(
                String postId,
                List<String> deleteResources,
                List<String> addResources,
                ResourceType resourceType
        ) {
            this.deleteResourceIfExists(postId, deleteResources, resourceType);
            this.addResourceIfExists(postId, addResources, resourceType);
        }

        private void deleteResourceIfExists(
                String postId,
                List<String> deleteResources,
                ResourceType resourceType
        ) {
            if (deleteResources != null && !deleteResources.isEmpty()) {
                postResourceCommandService.deleteAllEntities(
                        postId,
                        ContentType.POST,
                        resourceType,
                        deleteResources
                );
            }
        }

        private void addResourceIfExists(
                String postId,
                List<String> addResources,
                ResourceType resourceType
        ) {
            if (addResources != null && !addResources.isEmpty()) {
                bulkCommandService.saveAll(
                        addResources,
                        """
                                INSERT INTO content_resource (
                                    content_resource_id,
                                    content_id,
                                    content_type,
                                    resource_type,
                                    content
                                ) VALUES (?, ?, ?, ?, ?)
                                """,
                        (ps, resource) -> {
                            try {
                                ps.setString(1, NanoIdUtils.randomNanoId());
                                ps.setString(2, postId);
                                ps.setString(3, ContentType.POST.name());
                                ps.setString(4, resourceType.name());
                                ps.setString(5, resource);
                            } catch (Exception e) {
                                log.error("Failed to save post resource", e);
                                throw new PostException(PostErrorCode.FAILED_TO_SAVE_POST);
                            }
                        }
                );
            }
        }
    }
}
