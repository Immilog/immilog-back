package com.backend.immilog.notice.application.usecase;

import com.backend.immilog.notice.application.service.NoticeService;
import com.backend.immilog.notice.domain.model.NoticeId;
import com.backend.immilog.shared.enums.Country;
import org.springframework.stereotype.Service;

@Service
public class MarkNoticeAsReadUseCase {

    private final NoticeService noticeService;

    public MarkNoticeAsReadUseCase(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    public void execute(
            NoticeId noticeId,
            Long userSeq,
            Country userCountry
    ) {
        noticeService.markAsRead(noticeId, userSeq, userCountry);
    }

    public void execute(
            Long noticeSeq,
            Long userSeq,
            Country userCountry
    ) {
        NoticeId noticeId = NoticeId.of(noticeSeq);
        noticeService.markAsRead(noticeId, userSeq, userCountry);
    }
}