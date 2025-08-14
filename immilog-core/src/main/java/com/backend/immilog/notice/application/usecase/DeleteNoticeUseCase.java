package com.backend.immilog.notice.application.usecase;

import com.backend.immilog.notice.application.service.NoticeService;
import com.backend.immilog.notice.domain.model.NoticeId;
import org.springframework.stereotype.Service;

@Service
public class DeleteNoticeUseCase {

    private final NoticeService noticeService;

    public DeleteNoticeUseCase(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    public void execute(
            String token,
            NoticeId noticeId
    ) {
        noticeService.deleteNotice(token, noticeId);
    }

    public void execute(
            String token,
            String id
    ) {
        NoticeId noticeId = NoticeId.of(id);
        noticeService.deleteNotice(token, noticeId);
    }
}