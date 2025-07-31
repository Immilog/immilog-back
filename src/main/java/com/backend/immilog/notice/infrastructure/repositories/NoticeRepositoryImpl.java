package com.backend.immilog.notice.infrastructure.repositories;

import com.backend.immilog.notice.application.dto.NoticeModelResult;
import com.backend.immilog.notice.domain.enums.NoticeStatus;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.repository.NoticeRepository;
import com.backend.immilog.notice.infrastructure.jdbc.NoticeJdbcRepository;
import com.backend.immilog.notice.infrastructure.jpa.NoticeJpaEntity;
import com.backend.immilog.notice.infrastructure.jpa.NoticeJpaRepository;
import com.backend.immilog.shared.enums.Country;
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
            String userId,
            Pageable pageable
    ) {
        var result = noticeJdbcRepository.getNotices(
                userId,
                pageable.getPageSize(),
                pageable.getOffset()
        );
        var total = noticeJdbcRepository.getTotal(userId);
        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public Notice save(Notice notice) {
        var saved = noticeJpaRepository.save(NoticeJpaEntity.from(notice));
        return saved.toDomain();
    }

    @Override
    public void delete(Notice notice) {
        noticeJpaRepository.delete(NoticeJpaEntity.from(notice));
    }

    @Override
    public void deleteById(String noticeId) {
        noticeJpaRepository.deleteById(noticeId);
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
    public List<Notice> findByAuthorUserId(String authorUserId) {
        return noticeJpaRepository
                .findByUserIdAndStatusNot(authorUserId, NoticeStatus.DELETED)
                .stream()
                .map(NoticeJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(String noticeId) {
        return noticeJpaRepository.existsById(noticeId);
    }

    @Override
    public Optional<Notice> findById(String noticeId) {
        return noticeJpaRepository
                .findByIdAndStatusIsNot(noticeId, NoticeStatus.DELETED)
                .map(NoticeJpaEntity::toDomain);
    }

    @Override
    public Boolean areUnreadNoticesExist(
            Country country,
            String id
    ) {
        return noticeJpaRepository.existsByTargetCountryContainingAndReadUsersNotContaining(country, id);
    }
}
