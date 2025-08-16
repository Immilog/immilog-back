package com.backend.immilog.chat.presentation.dto;

public record ChatRoomCreateRequest(
        String name,
        String countryId,
        String createdBy
) {}

