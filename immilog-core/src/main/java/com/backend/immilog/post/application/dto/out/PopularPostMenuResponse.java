package com.backend.immilog.post.application.dto.out;

import java.util.List;

public record PopularPostMenuResponse(
        List<PostResult> hot,
        List<PostResult> weeklyBest
) {
}