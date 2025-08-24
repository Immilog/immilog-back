package com.backend.immilog.chat.presentation.dto;

public record PrivateChatRoomCreateRequest(
        String targetUserId
) {}