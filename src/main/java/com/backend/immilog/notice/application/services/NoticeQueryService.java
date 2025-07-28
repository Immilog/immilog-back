package com.backend.immilog.notice.application.services;

import com.backend.immilog.notice.application.dto.NoticeModelResult;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.model.NoticeId;
import com.backend.immilog.notice.domain.repository.NoticeRepository;
import com.backend.immilog.notice.exception.NoticeErrorCode;
import com.backend.immilog.notice.exception.NoticeException;
import com.backend.immilog.shared.enums.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.backend.immilog.notice.exception.NoticeErrorCode.NOTICE_NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class NoticeQueryService {
    private final NoticeRepository noticeRepository;

    public NoticeQueryService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public Page<NoticeModelResult> getNotices(
            Long userSeq,
            Pageable pageable
    ) {
        return noticeRepository.getNotices(userSeq, pageable);
    }

    public Notice getById(NoticeId noticeId) {
        return noticeRepository.findById(noticeId.value())
                .orElseThrow(() -> new NoticeException(NoticeErrorCode.NOTICE_NOT_FOUND));
    }

    public List<Notice> getActiveNoticesForCountry(Country country) {
        return noticeRepository.findActiveNoticesForCountry(country);
    }

    public List<Notice> getAllActiveNotices() {
        return noticeRepository.findAllActiveNotices();
    }

    public List<Notice> getNoticesByType(NoticeType type) {
        return noticeRepository.findByType(type);
    }

    public List<Notice> getNoticesByAuthor(Long authorUserSeq) {
        return noticeRepository.findByAuthorUserSeq(authorUserSeq);
    }

    public boolean existsById(NoticeId noticeId) {
        return noticeRepository.existsById(noticeId.value());
    }

    @Transactional(readOnly = true)
    public Notice getNoticeBySeq(Long noticeSeq) {
        return noticeRepository
                .findBySeq(noticeSeq)
                .orElseThrow(() -> new NoticeException(NOTICE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Boolean areUnreadNoticesExist(
            Country Country,
            Long seq
    ) {
        return noticeRepository.areUnreadNoticesExist(Country, seq);
    }
}
