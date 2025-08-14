package com.backend.immilog.chat.presentation.dto;

import com.backend.immilog.chat.domain.model.ChatRoom;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record ChatRoomDto(
        String id,
        String name,
        String countryId,
        List<String> participantIds,
        String createdBy,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        int participantCount,
        boolean isActive
) {
    public static ChatRoomDto from(ChatRoom chatRoom) {
        return new ChatRoomDto(
                chatRoom.id(),
                chatRoom.name(),
                chatRoom.countryId(),
                chatRoom.participantIds(),
                chatRoom.createdBy(),
                chatRoom.createdAt(),
                chatRoom.participantIds().size(),
                chatRoom.isActive()
        );
    }
}