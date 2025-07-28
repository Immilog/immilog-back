package com.backend.immilog.notice.application.usecase;

import com.backend.immilog.notice.application.service.NoticeService;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.model.NoticeId;
import com.backend.immilog.user.domain.model.enums.Country;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetNoticeUseCase {

    private final NoticeService noticeService;

    public GetNoticeUseCase(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    public Notice execute(NoticeId noticeId) {
        return noticeService.getNoticeById(noticeId);
    }

    public Notice execute(Long noticeSeq) {
        NoticeId noticeId = NoticeId.of(noticeSeq);
        return noticeService.getNoticeById(noticeId);
    }

    public List<Notice> executeForCountry(Country country) {
        return noticeService.getNoticesForCountry(country);
    }

    public List<Notice> executeAllActive() {
        return noticeService.getAllActiveNotices();
    }

    public List<Notice> executeByType(NoticeType type) {
        return noticeService.getNoticesByType(type);
    }

    public boolean isReadBy(
            NoticeId noticeId,
            Long userSeq
    ) {
        return noticeService.isNoticeReadBy(noticeId, userSeq);
    }
}