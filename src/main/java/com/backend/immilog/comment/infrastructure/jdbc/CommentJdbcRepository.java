package com.backend.immilog.comment.infrastructure.jdbc;

import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.comment.domain.model.ReferenceType;
import com.backend.immilog.shared.enums.ContentStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CommentJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public CommentJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CommentResult> findCommentsByPostId(String postId) {
        var sql = """
                SELECT 
                    c.comment_id, 
                    c.user_id, 
                    c.content,
                    c.post_id,
                    c.parent_id, 
                    c.reference_type, 
                    c.reply_count,
                    c.status, 
                    c.created_at, 
                    c.updated_at,
                    u.nickname,
                    u.image_url,
                    u.country_id,
                    u.region
                FROM comment c
                LEFT JOIN user u ON c.user_id = u.user_id
                WHERE c.post_id = ? AND c.status = 'NORMAL'
                ORDER BY 
                    COALESCE(c.parent_id, c.comment_id) ASC,
                    c.parent_id IS NULL DESC,
                    c.created_at ASC
                """;
        return jdbcTemplate.query(sql, this::mapToCommentResult, postId);
    }

    private CommentResult mapToCommentResult(
            ResultSet rs,
            int rowNum
    ) throws SQLException {
        return new CommentResult(
                rs.getString("comment_id"),
                rs.getString("user_id"),
                rs.getString("nickname"),
                rs.getString("image_url"),
                rs.getString("country_id"),
                rs.getString("region"),
                rs.getString("content"),
                rs.getString("post_id"),
                rs.getString("parent_id"),
                ReferenceType.valueOf(rs.getString("reference_type")),
                rs.getInt("reply_count"),
                0, // likeCount는 실시간 계산으로 변경됨
                ContentStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null
        );
    }

    public CommentResult findCommentById(String commentId) {
        var sql = """
                SELECT 
                    c.comment_id, 
                    c.user_id, 
                    c.content,
                    c.post_id,
                    c.parent_id, 
                    c.reference_type, 
                    c.reply_count,
                    c.status, 
                    c.created_at, 
                    c.updated_at,
                    u.nickname,
                    u.image_url,
                    u.country_id,
                    u.region
                FROM comment c
                LEFT JOIN user u ON c.user_id = u.user_id
                WHERE c.comment_id = ?
                """;
        return jdbcTemplate.queryForObject(sql, this::mapToCommentResult, commentId);
    }
}