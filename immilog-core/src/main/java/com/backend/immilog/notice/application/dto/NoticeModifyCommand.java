package com.backend.immilog.notice.application.dto;

import com.backend.immilog.notice.domain.enums.NoticeStatus;
import com.backend.immilog.notice.domain.enums.NoticeType;

public record NoticeModifyCommand(
        String title,
        String content,
        NoticeType type,
        NoticeStatus status
) {
}
