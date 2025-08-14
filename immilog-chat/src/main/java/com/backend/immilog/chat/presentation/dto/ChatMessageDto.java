package com.backend.immilog.chat.presentation.dto;

import com.backend.immilog.chat.domain.model.ChatMessage;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ChatMessageDto(
        String id,
        String chatRoomId,
        String senderId,
        String senderNickname,
        String content,
        String messageType,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime sentAt,
        boolean isDeleted
) {
    public static ChatMessageDto from(ChatMessage message) {
        return new ChatMessageDto(
                message.id(),
                message.chatRoomId(),
                message.senderId(),
                message.senderNickname(),
                message.content(),
                message.messageType().name(),
                message.sentAt(),
                message.isDeleted()
        );
    }
}