package com.backend.immilog.post.application.services.command;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.backend.immilog.post.domain.model.resource.ContentResource;
import com.backend.immilog.post.domain.repositories.BulkInsertRepository;
import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.shared.aop.annotation.PerformanceMonitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.function.BiConsumer;

@Slf4j
@Service
public class BulkCommandService {
    private final BulkInsertRepository bulkInsertRepository;

    public BulkCommandService(BulkInsertRepository bulkInsertRepository) {
        this.bulkInsertRepository = bulkInsertRepository;
    }

    @PerformanceMonitor
    @Transactional
    public <T> void saveAll(
            List<T> entities,
            String command,
            BiConsumer<PreparedStatement, T> statementSetter
    ) {
        if (entities == null || entities.isEmpty()) {
            log.warn("Attempted to save empty or null entity list");
            return;
        }
        
        try {
            bulkInsertRepository.saveAll(entities, command, statementSetter);
            log.info("Successfully bulk saved {} entities", entities.size());
        } catch (Exception e) {
            log.error("Failed to bulk save entities: {}", e.getMessage());
            throw new PostException(PostErrorCode.FAILED_TO_SAVE_POST);
        }
    }
    
    @PerformanceMonitor
    @Transactional
    public void saveContentResources(List<ContentResource> resources) {
        if (resources == null || resources.isEmpty()) {
            return;
        }
        
        saveAll(
                resources,
                """
                INSERT INTO content_resource (
                    content_resource_id,
                    content_id,
                    content_type,
                    resource_type,
                    content
                ) VALUES (?, ?, ?, ?, ?)
                """,
                this::setContentResourceStatement
        );
    }
    
    private void setContentResourceStatement(
            PreparedStatement ps,
            ContentResource resource
    ) {
        try {
            ps.setString(1, resource.id() != null ? resource.id() : NanoIdUtils.randomNanoId());
            ps.setString(2, resource.postId());
            ps.setString(3, resource.contentType().name());
            ps.setString(4, resource.resourceType().name());
            ps.setString(5, resource.content());
        } catch (SQLException e) {
            log.error("Failed to set parameters for content resource: {}", e.getMessage());
            throw new PostException(PostErrorCode.FAILED_TO_SAVE_POST);
        }
    }
}
