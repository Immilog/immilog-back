package com.backend.immilog.post.infrastructure.jdbc;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.post.domain.model.enums.PostStatus;
import com.backend.immilog.post.infrastructure.result.CommentEntityResult;
import com.backend.immilog.user.application.result.UserInfoResult;
import com.backend.immilog.user.domain.enums.UserCountry;
import com.backend.immilog.user.domain.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CommentJdbcRepository {
    private final JdbcClient jdbcClient;

    public List<CommentEntityResult> getComments(
            Long postSeq
    ) {
        String sql = """
                         SELECT c.*, u.*, cc.*, cu.*
                         FROM comment c
                         LEFT JOIN user u ON c.user_seq = u.seq
                         LEFT JOIN comment cc ON cc.post_seq = ?
                                             AND cc.parent_seq = c.seq
                                             AND cc.reference_type = 'COMMENT'
                         LEFT JOIN user cu ON cc.user_seq = cu.seq
                         WHERE c.post_seq = ?
                             AND c.parent_seq IS NULL
                             AND c.reference_type = 'POST'
                         ORDER BY c.created_at DESC
                     """;

        return jdbcClient.sql(sql)
                .param(postSeq)
                .param(postSeq)
                .query((rs, rowNum) -> mapParentCommentWithChildren(rs))
                .stream()
                .flatMap(List::stream)
                .toList();
    }

    private List<CommentEntityResult> mapParentCommentWithChildren(ResultSet rs) throws SQLException {
        Map<Long, CommentEntityResult> commentMap = new HashMap<>();
        do {
            Long commentSeq = rs.getLong("c.seq");
            commentMap.putIfAbsent(commentSeq, getCommentEntityResult(rs, commentSeq, getUserInfoResult(rs, "u")));

            long childCommentSeq = rs.getLong("cc.seq");
            if (childCommentSeq != 0) {
                CommentEntityResult childComment = getCommentEntityResult(rs, childCommentSeq, getUserInfoResult(rs, "cu"));
                commentMap.get(commentSeq).addChildComment(childComment);
            }
        } while (rs.next());
        return new ArrayList<>(commentMap.values());
    }

    private static CommentEntityResult getCommentEntityResult(
            ResultSet rs,
            long commentSeq,
            UserInfoResult user
    ) throws SQLException {
        String prefix = commentSeq == rs.getLong("c.seq") ? "c" : "cc";
        return new CommentEntityResult(
                commentSeq,
                user,
                rs.getString(prefix + ".content"),
                new ArrayList<>(),
                rs.getInt(prefix + ".upVotes"),
                rs.getInt(prefix + ".downVotes"),
                rs.getInt(prefix + ".replyCount"),
                new ArrayList<>(),
                PostStatus.valueOf(rs.getString(prefix + ".status")),
                rs.getTimestamp(prefix + ".createdAt").toLocalDateTime()
        );
    }

    private static UserInfoResult getUserInfoResult(
            ResultSet rs,
            String prefix
    ) throws SQLException {
        return new UserInfoResult(
                rs.getLong(prefix + ".seq"),
                rs.getString(prefix + ".nickName"),
                rs.getString(prefix + ".email"),
                rs.getString(prefix + ".profileImage"),
                rs.getLong(prefix + ".reportedCount"),
                rs.getDate(prefix + ".reportedDate"),
                UserCountry.valueOf(rs.getString(prefix + ".country")),
                UserCountry.valueOf(rs.getString(prefix + ".interestCountry")),
                rs.getString(prefix + ".region"),
                UserRole.valueOf(rs.getString(prefix + ".userRole")),
                UserStatus.valueOf(rs.getString(prefix + ".userStatus"))
        );
    }
}