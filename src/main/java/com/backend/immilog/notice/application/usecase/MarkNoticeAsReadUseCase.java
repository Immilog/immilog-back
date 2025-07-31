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
            String userId,
            Country userCountry
    ) {
        noticeService.markAsRead(noticeId, userId, userCountry);
    }

    public void execute(
            String noticeId,
            String userId,
            Country userCountry
    ) {
        noticeService.markAsRead(NoticeId.of(noticeId), userId, userCountry);
    }
}