package com.backend.immilog.chat.presentation.dto;

public class UserNotificationDto {
    
    public record UnreadCountUpdateMessage(
            String type,
            String chatRoomId,
            int unreadCount,
            int totalUnreadCount
    ) {
        public static UnreadCountUpdateMessage create(
                String chatRoomId,
                int unreadCount,
                int totalUnreadCount
        ) {
            return new UnreadCountUpdateMessage(
                    "UNREAD_COUNT_UPDATE",
                    chatRoomId,
                    unreadCount,
                    totalUnreadCount
            );
        }
    }
}