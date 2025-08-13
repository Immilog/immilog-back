package com.backend.immilog.notice.domain.repository;

import com.backend.immilog.notice.application.dto.NoticeModelResult;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository {
    Page<NoticeModelResult> getNotices(
            String userId,
            Pageable pageable
    );

    Notice save(Notice notice);

    void delete(Notice notice);

    void deleteById(String noticeId);

    Optional<Notice> findById(String noticeId);

    List<Notice> findActiveNoticesForCountryId(String country);

    List<Notice> findAllActiveNotices();

    List<Notice> findByType(NoticeType type);

    List<Notice> findByAuthorUserId(String authorUserId);

    boolean existsById(String noticeId);

    Boolean areUnreadNoticesExist(
            String countryId,
            String id
    );
}
