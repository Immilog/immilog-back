package com.backend.immilog.notice.application.usecase;

import com.backend.immilog.notice.application.service.NoticeService;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.NoticeId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreateNoticeUseCase {

    private final NoticeService noticeService;

    public CreateNoticeUseCase(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    public NoticeId execute(
            String token,
            String title,
            String content,
            NoticeType type,
            List<String> targetCountries
    ) {
        return noticeService.createNotice(token, title, content, type, targetCountries);
    }
}