package com.backend.immilog.post.infrastructure.jdbc;

import com.backend.immilog.post.domain.model.post.Badge;
import com.backend.immilog.post.domain.model.post.Categories;
import com.backend.immilog.post.domain.model.post.Post;
import com.backend.immilog.post.domain.model.post.SortingMethods;
import com.backend.immilog.post.infrastructure.jpa.entity.post.PostEntity;
import com.backend.immilog.post.infrastructure.jpa.entity.post.PostInfoValue;
import com.backend.immilog.post.infrastructure.jpa.entity.post.PostUserInfoValue;
import com.backend.immilog.shared.enums.ContentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PostJdbcRepository {
    private final JdbcClient jdbcClient;

    public PostJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Page<Post> getPosts(
            String countryId,
            SortingMethods sortingMethod,
            String isPublic,
            Categories category,
            Pageable pageable
    ) {
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        conditions.add("p.is_public = ?");
        params.add(isPublic);

        if (category != null && category != Categories.ALL) {
            conditions.add("p.category = ?");
            params.add(category.name());
        }

        if (countryId != null && !countryId.equals("ALL")) {
            conditions.add("p.country_id = ?");
            params.add(countryId);
        }

        String whereClause = conditions.isEmpty() ? "" : "WHERE " + String.join(" AND ", conditions);
        String orderByClause = getOrderByClause(sortingMethod);

        String sql = String.format("""
                SELECT p.*, u.nickname, u.image_url
                FROM post p
                LEFT JOIN user u ON p.user_id = u.user_id
                %s
                %s
                LIMIT ? OFFSET ?
                """, whereClause, orderByClause);

        params.add(pageable.getPageSize());
        params.add(pageable.getOffset());

        List<PostEntity> postEntities = jdbcClient.sql(sql)
                .params(params.toArray())
                .query(POST_ENTITY_ROW_MAPPER)
                .list();

        String countSql = String.format("""
                SELECT COUNT(*)
                FROM post p
                LEFT JOIN user u ON p.user_id = u.user_id
                %s
                """, whereClause);

        int count = jdbcClient.sql(countSql)
                .params(params.subList(0, params.size() - 2).toArray())
                .query(Integer.class)
                .single();

        List<Post> posts = postEntities.stream().map(PostEntity::toDomain).toList();

        return new PageImpl<>(posts, pageable, count);
    }

    public Page<Post> getPostsByUserId(
            String userId,
            Pageable pageable
    ) {
        String sql = """
                SELECT p.*, u.nickname, u.image_url
                FROM post p
                LEFT JOIN user u ON p.user_id = u.user_id
                WHERE p.user_id = ?
                ORDER BY p.created_at DESC
                LIMIT ? OFFSET ?
                """;

        List<PostEntity> postEntities = jdbcClient.sql(sql)
                .param(userId)
                .param(pageable.getPageSize())
                .param(pageable.getOffset())
                .query(POST_ENTITY_ROW_MAPPER)
                .list();

        int count = jdbcClient.sql("""
                        SELECT COUNT(*)
                        FROM post p
                        LEFT JOIN user u ON p.user_id = u.user_id
                        WHERE p.user_id = ?
                        """)
                .param(userId)
                .query(Integer.class)
                .single();

        List<Post> posts = postEntities.stream().map(PostEntity::toDomain).toList();

        return new PageImpl<>(posts, pageable, count);
    }

    public Page<Post> getPostsByKeyword(
            String keyword,
            Pageable pageable
    ) {
        String sql = """
                SELECT DISTINCT p.*, u.nickname, u.image_url
                FROM post p
                LEFT JOIN user u ON p.user_id = u.user_id
                LEFT JOIN content_resource cr ON p.post_id = cr.content_id AND cr.content_type = 'POST' AND cr.resource_type = 'TAG'
                WHERE p.content LIKE ? OR p.title LIKE ? OR cr.content LIKE ?
                LIMIT ? OFFSET ?
                """;

        List<PostEntity> postEntities = jdbcClient.sql(sql)
                .param("%" + keyword + "%")
                .param("%" + keyword + "%")
                .param("%" + keyword + "%")
                .param(pageable.getPageSize())
                .param(pageable.getOffset())
                .query(POST_ENTITY_ROW_MAPPER)
                .list();

        String countSql = """
                SELECT COUNT(DISTINCT p.post_id)
                FROM post p
                LEFT JOIN user u ON p.user_id = u.user_id
                LEFT JOIN content_resource cr ON p.post_id = cr.content_id AND cr.content_type = 'POST' AND cr.resource_type = 'TAG'
                WHERE p.content LIKE ? OR p.title LIKE ? OR cr.content LIKE ?
                """;

        int count = jdbcClient.sql(countSql)
                .param("%" + keyword + "%")
                .param("%" + keyword + "%")
                .param("%" + keyword + "%")
                .query(Integer.class)
                .single();

        List<Post> posts = postEntities.stream().map(PostEntity::toDomain).toList();

        return new PageImpl<>(posts, pageable, count);
    }

    public Optional<Post> getSinglePost(String postId) {
        String sql = """
                SELECT p.*, u.nickname, u.image_url
                FROM post p
                LEFT JOIN user u ON p.user_id = u.user_id
                WHERE p.post_id = ?
                """;

        return jdbcClient.sql(sql)
                .param(postId)
                .query(POST_ENTITY_ROW_MAPPER)
                .optional()
                .map(PostEntity::toDomain);
    }

    public List<Post> getPopularPosts(
            LocalDateTime from,
            LocalDateTime to,
            SortingMethods sortingMethods
    ) {
        String sql = String.format("""
                SELECT p.*, u.nickname, u.image_url
                FROM post p
                LEFT JOIN user u ON p.user_id = u.user_id
                WHERE p.created_at BETWEEN ? AND ?
                %s
                LIMIT 10
                """, getOrderByClause(sortingMethods));

        List<PostEntity> postEntities = jdbcClient.sql(sql)
                .param(from)
                .param(to)
                .query(POST_ENTITY_ROW_MAPPER)
                .list();

        return postEntities.stream().map(PostEntity::toDomain).toList();
    }

    public List<Post> getPostsByPostIdList(List<String> postIdList) {
        if (postIdList.isEmpty()) {
            return List.of();
        }

        String inClause = String.join(",", postIdList.stream().map(id -> "?").toList());
        String sql = String.format("""
                SELECT p.*, u.nickname, u.image_url
                FROM post p
                LEFT JOIN user u ON p.user_id = u.user_id
                WHERE p.post_id IN (%s)
                """, inClause);

        List<PostEntity> postEntities = jdbcClient.sql(sql)
                .params(postIdList.toArray())
                .query(POST_ENTITY_ROW_MAPPER)
                .list();

        return postEntities.stream().map(PostEntity::toDomain).toList();
    }

    private static final RowMapper<PostEntity> POST_ENTITY_ROW_MAPPER = (rs, rowNum) -> {
        String id = rs.getString("post_id");

        PostUserInfoValue postUserInfo = new PostUserInfoValue(
                rs.getString("user_id")
        );

        PostInfoValue postInfo = new PostInfoValue(
                rs.getString("title"),
                rs.getString("content"),
                getNullableLong(rs, "view_count"),
                rs.getString("region"),
                getEnum(rs, "status", ContentStatus.class),
                rs.getString("country_id")
        );

        return new PostEntity(
                id,
                postUserInfo.toDomain(),
                postInfo.toDomain(),
                getEnum(rs, "category", Categories.class),
                rs.getString("is_public"),
                getEnum(rs, "badge", Badge.class),
                getNullableLong(rs, "comment_count"),
                getNullableTimestamp(rs, "created_at"),
                getNullableTimestamp(rs, "updated_at")
        );
    };

    private static Long getNullableLong(
            ResultSet rs,
            String columnName
    ) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }

    private static <T extends Enum<T>> T getEnum(
            ResultSet rs,
            String columnName,
            Class<T> enumClass
    ) throws SQLException {
        String value = rs.getString(columnName);
        if (value == null || value.trim().isEmpty() || value.equals("0")) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            // 잘못된 enum 값이 있는 경우 null 반환하고 로그 출력
            System.err.println("Invalid enum value for " + columnName + ": " + value);
            return null;
        }
    }

    private static LocalDateTime getNullableTimestamp(
            ResultSet rs,
            String columnName
    ) throws SQLException {
        return rs.getTimestamp(columnName) != null ? rs.getTimestamp(columnName).toLocalDateTime() : null;
    }

    private String getOrderByClause(SortingMethods sortingMethod) {
        String column = switch (sortingMethod) {
            case CREATED_DATE -> "p.created_at";
            case COMMENT_COUNT -> "p.comment_count";
            case LIKE_COUNT -> "(SELECT COUNT(*) FROM interaction_user iu WHERE iu.post_id = p.post_id AND iu.interaction_type = 'LIKE' AND iu.interaction_status = 'ACTIVE')";
            case VIEW_COUNT -> "p.view_count";
        };
        return "ORDER BY " + column + " DESC ";
    }
}
