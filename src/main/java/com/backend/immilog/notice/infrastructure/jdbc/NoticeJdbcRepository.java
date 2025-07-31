package com.backend.immilog.notice.infrastructure.jdbc;

import com.backend.immilog.notice.application.dto.NoticeModelResult;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NoticeJdbcRepository {
    private final JdbcClient jdbcClient;

    public NoticeJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<NoticeModelResult> getNotices(
            String userId,
            int pageSize,
            long offset
    ) {
        String sql = """
                SELECT n.*
                FROM notice n
                LEFT JOIN notice_target_country ntc ON n.notice_id = ntc.notice_id
                LEFT JOIN user u ON u.country = ntc.country 
                LEFT JOIN notice_read_user nru ON n.notice_id = nru.notice_id
                WHERE (u.country = ntc.country
                   OR ntc.country = 'ALL')
                   AND n.status = 'NORMAL'
                   AND (nru.user_id IS NULL OR nru.user_id != ?)
                ORDER BY n.created_at DESC
                LIMIT ? OFFSET ?
                """;

        return jdbcClient.sql(sql)
                .param(userId)
                .param(pageSize)
                .param(offset)
                .query((rs, rowNum) -> NoticeModelResult.from(rs))
                .list();
    }

    public Long getTotal(String userId) {
        String sql = """
                SELECT COUNT(*) 
                FROM notice n
                LEFT JOIN immilog.notice_target_country ntc ON n.notice_id = ntc.notice_id
                LEFT JOIN user u ON u.country = ntc.country 
                LEFT JOIN notice_read_user nru ON n.notice_id = nru.notice_id
                WHERE (u.country = ntc.country
                   OR ntc.country = 'ALL')
                   AND n.status = 'NORMAL'
                   AND (nru.user_id IS NULL OR nru.user_id != ?)
                """;
        try {
            return jdbcClient.sql(sql)
                    .param(userId)
                    .query((rs, rowNum) -> rs.getLong(1))
                    .single();
        } catch (EmptyResultDataAccessException e) {
            return 0L;
        }
    }
}
