package com.backend.immilog.notice.presentation;

import com.backend.immilog.notice.application.dto.NoticeModifyCommand;
import com.backend.immilog.notice.domain.enums.NoticeStatus;
import com.backend.immilog.notice.domain.enums.NoticeType;
import io.swagger.v3.oas.annotations.media.Schema;

public record NoticeModifyRequest(
        @Schema(description = "제목", example = "제목") String title,
        @Schema(description = "내용", example = "내용") String content,
        @Schema(description = "공지사항 타입", example = "NOTICE") NoticeType type,
        @Schema(description = "공지사항 상태", example = "NORMAL") NoticeStatus status
) {
    public NoticeModifyCommand toCommand() {
        return new NoticeModifyCommand(title, content, type, status);
    }
}
