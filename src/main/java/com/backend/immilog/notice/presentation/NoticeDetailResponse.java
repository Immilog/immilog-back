package com.backend.immilog.notice.presentation;

import com.backend.immilog.notice.application.dto.NoticeModelResult;
import com.backend.immilog.notice.domain.enums.NoticeStatus;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.shared.enums.Country;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record NoticeDetailResponse(
        @Schema(description = "상태 코드", example = "200") Integer status,
        @Schema(description = "메시지", example = "success") String message,
        @Schema(description = "공지사항 상세 정보") NoticeInformation data
) {
    public static NoticeDetailResponse of(Notice notice) {
        return new NoticeDetailResponse(
                200,
                "success",
                NoticeModelResult.from(notice).toInfraDTO()
        );
    }

    public record NoticeInformation(
            @Schema(description = "공지사항 번호") String id,
            @Schema(description = "작성자 번호") String authorUserId,
            @Schema(description = "제목") String title,
            @Schema(description = "내용") String content,
            @Schema(description = "공지사항 타입") NoticeType type,
            @Schema(description = "공지사항 상태") NoticeStatus status,
            @Schema(description = "대상 국가") List<Country> targetCountry,
            @Schema(description = "읽은 사용자 목록") List<String> readUsers,
            @Schema(description = "생성일") LocalDateTime createdAt
    ) {
    }
}