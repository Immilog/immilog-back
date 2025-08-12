package com.backend.immilog.notice.application.usecase;

import com.backend.immilog.notice.application.service.NoticeService;
import com.backend.immilog.notice.domain.model.NoticeId;
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
            String userCountryId
    ) {
        noticeService.markAsRead(noticeId, userId, userCountryId);
    }

    public void execute(
            String noticeId,
            String userId,
            String userCountryId
    ) {
        noticeService.markAsRead(NoticeId.of(noticeId), userId, userCountryId);
    }
}