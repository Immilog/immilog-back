package com.backend.immilog.post.infrastructure.jdbc;

import com.backend.immilog.post.domain.model.resource.ContentResource;
import com.backend.immilog.post.domain.model.resource.ResourceType;
import com.backend.immilog.shared.enums.ContentType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ContentResourceJdbcRepository {
    private final JdbcClient jdbcClient;

    public ContentResourceJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void deleteAllEntities(
            String postId,
            ContentType contentType,
            ResourceType resourceType,
            List<String> deleteAttachments
    ) {
        if (deleteAttachments.isEmpty()) {
            return;
        }
        String inClause = deleteAttachments.stream()
                .map(item -> "?")
                .collect(Collectors.joining(", "));

        String sql = """
                DELETE FROM content_resource
                WHERE content_id = ?
                AND content_type = ?
                AND resource_type = ?
                AND content IN (%s)
                """.formatted(inClause);

        jdbcClient.sql(sql)
                .param(postId)
                .param(contentType.toString())
                .param(resourceType.toString())
                .params(deleteAttachments.toArray())
                .update();
    }

    public void deleteAllByPostId(String id) {
        jdbcClient.sql("""
                        DELETE FROM content_resource
                        WHERE content_id = ?
                        """)
                .param(id)
                .update();
    }

    public List<ContentResource> findAllByPostIdList(
            List<String> postIdList,
            ContentType contentType
    ) {
        if (postIdList.isEmpty()) {
            return List.of();
        }

        String inClause = postIdList.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        String sql = """
                SELECT *
                FROM content_resource
                WHERE content_id IN (%s)
                AND content_type = ?
                """.formatted(inClause);

        return jdbcClient.sql(sql)
                .params(postIdList.toArray())
                .param(contentType.name())
                .query(ContentResource.class)
                .list();
    }
}
