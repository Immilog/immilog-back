package com.backend.immilog.post.infrastructure.jdbc;

import com.backend.immilog.post.domain.model.post.PostType;
import com.backend.immilog.post.domain.model.resource.PostResource;
import com.backend.immilog.post.domain.model.resource.ResourceType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PostResourceJdbcRepository {
    private final JdbcClient jdbcClient;

    public PostResourceJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void deleteAllEntities(
            String postId,
            PostType postType,
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
                DELETE FROM post_resource
                WHERE post_id = ?
                AND post_type = ?
                AND resource_type = ?
                AND content IN (%s)
                """.formatted(inClause);

        jdbcClient.sql(sql)
                .param(postId)
                .param(postType.toString())
                .param(resourceType.toString())
                .params(deleteAttachments.toArray())
                .update();
    }

    public void deleteAllByPostId(String id) {
        jdbcClient.sql("""
                        DELETE FROM post_resource
                        WHERE post_id = ?
                        """)
                .param(id)
                .update();
    }

    public List<PostResource> findAllByPostIdList(
            List<String> postIdList,
            PostType postType
    ) {
        if (postIdList.isEmpty()) {
            return List.of();
        }

        String inClause = postIdList.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        String sql = """
                SELECT *
                FROM post_resource
                WHERE post_id IN (%s)
                AND post_type = ?
                """.formatted(inClause);

        return jdbcClient.sql(sql)
                .params(postIdList.toArray())
                .param(postType.name())
                .query(PostResource.class)
                .list();
    }
}
