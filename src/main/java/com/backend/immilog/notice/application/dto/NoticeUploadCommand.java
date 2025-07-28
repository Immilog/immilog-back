package com.backend.immilog.notice.application.dto;

import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.shared.enums.Country;

import java.util.List;

public record NoticeUploadCommand(
        String title,
        String content,
        NoticeType type,
        List<Country> targetCountry
) {
}
