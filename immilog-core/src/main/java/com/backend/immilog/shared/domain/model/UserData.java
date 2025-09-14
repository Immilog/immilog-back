package com.backend.immilog.shared.domain.model;

public record UserData(
    String userId,
    String nickname,
    String profileImageUrl,
    String countryId,
    String region
) {
}