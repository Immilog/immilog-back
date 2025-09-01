package com.backend.immilog.post.application.dto;

import java.util.List;

/**
 * 인기글 메뉴 응답 DTO
 * HOT 게시물과 주간베스트를 통합 제공
 */
public record PopularPostMenuResponse(
    List<PostResult> hot,           // 핫게시물 (최대 5개)
    List<PostResult> weeklyBest     // 주간베스트 (최대 5개)
) {
    public static PopularPostMenuResponse of(
            List<PostResult> hot,
            List<PostResult> weeklyBest
    ) {
        return new PopularPostMenuResponse(hot, weeklyBest);
    }
}