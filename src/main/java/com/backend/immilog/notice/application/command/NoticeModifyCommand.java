package com.backend.immilog.notice.application.command;

import com.backend.immilog.notice.domain.model.enums.NoticeStatus;
import com.backend.immilog.notice.domain.model.enums.NoticeType;

public record NoticeModifyCommand(
        String title,
        String content,
        NoticeType type,
        NoticeStatus status
) {
}
