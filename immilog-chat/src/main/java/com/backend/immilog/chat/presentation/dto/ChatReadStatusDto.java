package com.backend.immilog.chat.presentation.dto;

import com.backend.immilog.chat.domain.model.ChatRoomReadStatus;

import java.time.LocalDateTime;
import java.util.Map;

public class ChatReadStatusDto {
    
    public record ReadStatusResponse(
            String chatRoomId,
            String userId,
            String lastReadMessageId,
            LocalDateTime lastReadAt,
            int unreadCount
    ) {
        public static ReadStatusResponse from(ChatRoomReadStatus readStatus) {
            return new ReadStatusResponse(
                    readStatus.chatRoomId(),
                    readStatus.userId(),
                    readStatus.lastReadMessageId(),
                    readStatus.lastReadAt(),
                    readStatus.unreadCount()
            );
        }
    }
    
    public record MarkAsReadRequest(
            String messageId
    ) {}
    
    public record UnreadCountResponse(
            String chatRoomId,
            int unreadCount
    ) {}
    
    public record AllUnreadCountsResponse(
            Map<String, Integer> unreadCounts,
            int totalUnreadCount
    ) {}
    
    public record ReadStatusUpdateEvent(
            String chatRoomId,
            String userId,
            String messageId,
            int remainingUnreadCount
    ) {}
}