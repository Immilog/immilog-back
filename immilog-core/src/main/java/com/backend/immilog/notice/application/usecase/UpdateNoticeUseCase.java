package com.backend.immilog.notice.application.usecase;

import com.backend.immilog.notice.application.service.NoticeService;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.NoticeId;
import org.springframework.stereotype.Service;

@Service
public class UpdateNoticeUseCase {

    private final NoticeService noticeService;

    public UpdateNoticeUseCase(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    public void execute(
            String token,
            NoticeId noticeId,
            String title,
            String content,
            NoticeType type
    ) {
        noticeService.updateNotice(token, noticeId, title, content, type);
    }

    public void executeContentOnly(
            String token,
            NoticeId noticeId,
            String content
    ) {
        noticeService.updateNotice(token, noticeId, null, content, null);
    }

    public void executeTitleOnly(
            String token,
            NoticeId noticeId,
            String title
    ) {
        noticeService.updateNotice(token, noticeId, title, null, null);
    }
}