package com.backend.immilog.notice.infrastructure.repositories;

import com.backend.immilog.notice.application.dto.NoticeModelResult;
import com.backend.immilog.notice.domain.enums.NoticeStatus;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.repository.NoticeRepository;
import com.backend.immilog.notice.infrastructure.jdbc.NoticeJdbcRepository;
import com.backend.immilog.notice.infrastructure.jpa.NoticeJpaEntity;
import com.backend.immilog.notice.infrastructure.jpa.NoticeJpaRepository;
import com.backend.immilog.user.domain.model.enums.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class NoticeRepositoryImpl implements NoticeRepository {
    private final NoticeJdbcRepository noticeJdbcRepository;
    private final NoticeJpaRepository noticeJpaRepository;

    public NoticeRepositoryImpl(
            NoticeJdbcRepository noticeJdbcRepository,
            NoticeJpaRepository noticeJpaRepository
    ) {
        this.noticeJdbcRepository = noticeJdbcRepository;
        this.noticeJpaRepository = noticeJpaRepository;
    }

    @Override
    public Page<NoticeModelResult> getNotices(
            Long userSeq,
            Pageable pageable
    ) {
        List<NoticeModelResult> result = noticeJdbcRepository.getNotices(
                userSeq,
                pageable.getPageSize(),
                pageable.getOffset()
        );
        Long total = noticeJdbcRepository.getTotal(userSeq);
        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public Notice save(Notice notice) {
        NoticeJpaEntity saved = noticeJpaRepository.save(NoticeJpaEntity.from(notice));
        return saved.toDomain();
    }

    @Override
    public void delete(Notice notice) {
        noticeJpaRepository.delete(NoticeJpaEntity.from(notice));
    }

    @Override
    public void deleteById(Long noticeId) {
        noticeJpaRepository.deleteById(noticeId);
    }

    @Override
    public Optional<Notice> findById(Long noticeId) {
        return noticeJpaRepository
                .findById(noticeId)
                .map(NoticeJpaEntity::toDomain);
    }

    @Override
    public List<Notice> findActiveNoticesForCountry(Country country) {
        return noticeJpaRepository
                .findByStatusNotAndTargetCountryContaining(NoticeStatus.DELETED, country)
                .stream()
                .map(NoticeJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Notice> findAllActiveNotices() {
        return noticeJpaRepository
                .findByStatusNot(NoticeStatus.DELETED)
                .stream()
                .map(NoticeJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Notice> findByType(NoticeType type) {
        return noticeJpaRepository
                .findByTypeAndStatusNot(type, NoticeStatus.DELETED)
                .stream()
                .map(NoticeJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Notice> findByAuthorUserSeq(Long authorUserSeq) {
        return noticeJpaRepository
                .findByUserSeqAndStatusNot(authorUserSeq, NoticeStatus.DELETED)
                .stream()
                .map(NoticeJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(Long noticeId) {
        return noticeJpaRepository.existsById(noticeId);
    }

    @Override
    public Optional<Notice> findBySeq(Long noticeSeq) {
        return noticeJpaRepository
                .findBySeqAndStatusIsNot(noticeSeq, NoticeStatus.DELETED)
                .map(NoticeJpaEntity::toDomain);
    }

    @Override
    public Boolean areUnreadNoticesExist(
            Country country,
            Long seq
    ) {
        return noticeJpaRepository.existsByTargetCountryContainingAndReadUsersNotContaining(country, seq);
    }
}
