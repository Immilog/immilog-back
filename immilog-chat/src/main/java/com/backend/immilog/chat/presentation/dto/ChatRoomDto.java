package com.backend.immilog.chat.presentation.dto;

import com.backend.immilog.chat.domain.model.ChatRoom;
import com.backend.immilog.chat.domain.model.ChatMessage;
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
        boolean isActive,
        LatestMessageDto latestMessage,
        int unreadCount  // 안읽은 메시지 수
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
                chatRoom.isActive(),
                null, // 최근 메시지는 별도 로직에서 설정
                0 // 안읽은 수는 별도 로직에서 설정
        );
    }
    
    public static ChatRoomDto from(ChatRoom chatRoom, ChatMessage latestMessage) {
        return new ChatRoomDto(
                chatRoom.id(),
                chatRoom.name(),
                chatRoom.countryId(),
                chatRoom.participantIds(),
                chatRoom.createdBy(),
                chatRoom.createdAt(),
                chatRoom.participantIds().size(),
                chatRoom.isActive(),
                latestMessage != null ? LatestMessageDto.from(latestMessage) : null,
                0 // 안읽은 수는 별도 로직에서 설정
        );
    }
    
    public static ChatRoomDto from(ChatRoom chatRoom, ChatMessage latestMessage, int unreadCount) {
        return new ChatRoomDto(
                chatRoom.id(),
                chatRoom.name(),
                chatRoom.countryId(),
                chatRoom.participantIds(),
                chatRoom.createdBy(),
                chatRoom.createdAt(),
                chatRoom.participantIds().size(),
                chatRoom.isActive(),
                latestMessage != null ? LatestMessageDto.from(latestMessage) : null,
                unreadCount
        );
    }
    
    public record LatestMessageDto(
            String content,
            String senderNickname,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime sentAt,
            String messageType
    ) {
        public static LatestMessageDto from(ChatMessage message) {
            return new LatestMessageDto(
                    message.content(),
                    message.senderNickname(),
                    message.sentAt(),
                    message.messageType().name()
            );
        }
    }
}