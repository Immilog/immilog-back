package com.backend.immilog.notice.infrastructure.jpa;

import com.backend.immilog.notice.domain.enums.NoticeStatus;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.shared.enums.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeJpaRepository extends JpaRepository<NoticeJpaEntity, String> {
    Boolean existsByTargetCountryContainingAndReadUsersNotContaining(
            Country country,
            String id
    );

    Optional<NoticeJpaEntity> findByIdAndStatusIsNot(
            String noticeId,
            NoticeStatus status
    );

    List<NoticeJpaEntity> findByStatusNotAndTargetCountryContaining(
            NoticeStatus status,
            Country country
    );

    List<NoticeJpaEntity> findByStatusNot(NoticeStatus status);

    List<NoticeJpaEntity> findByTypeAndStatusNot(
            NoticeType type,
            NoticeStatus status
    );

    List<NoticeJpaEntity> findByUserIdAndStatusNot(
            String authorId,
            NoticeStatus status
    );
}
