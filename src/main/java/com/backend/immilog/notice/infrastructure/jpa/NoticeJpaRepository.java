package com.backend.immilog.notice.infrastructure.jpa;

import com.backend.immilog.notice.domain.enums.NoticeStatus;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.user.domain.model.enums.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeJpaRepository extends JpaRepository<NoticeJpaEntity, Long> {
    Boolean existsByTargetCountryContainingAndReadUsersNotContaining(
            Country country,
            Long seq
    );

    Optional<NoticeJpaEntity> findBySeqAndStatusIsNot(
            Long noticeSeq,
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

    List<NoticeJpaEntity> findByUserSeqAndStatusNot(
            Long authorSeq,
            NoticeStatus status
    );
}
