package com.backend.immilog.notice.application.dto;

import com.backend.immilog.notice.domain.enums.NoticeType;

import java.util.List;

public record NoticeUploadCommand(
        String title,
        String content,
        NoticeType type,
        List<String> targetCountry
) {
}
