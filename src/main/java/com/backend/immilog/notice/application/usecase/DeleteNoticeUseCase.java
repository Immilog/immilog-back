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
            Long noticeSeq
    ) {
        NoticeId noticeId = NoticeId.of(noticeSeq);
        noticeService.deleteNotice(token, noticeId);
    }
}