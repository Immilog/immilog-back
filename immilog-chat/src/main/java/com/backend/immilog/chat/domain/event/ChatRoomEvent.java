package com.backend.immilog.chat.domain.event;

import com.backend.immilog.chat.domain.model.ChatRoom;

public record ChatRoomEvent(
        EventType type,
        ChatRoom chatRoom,
        String userId
) {
    public enum EventType {
        CREATED,
        UPDATED,
        USER_JOINED,
        USER_LEFT
    }
    
    public static ChatRoomEvent created(ChatRoom chatRoom) {
        return new ChatRoomEvent(EventType.CREATED, chatRoom, null);
    }
    
    public static ChatRoomEvent updated(ChatRoom chatRoom) {
        return new ChatRoomEvent(EventType.UPDATED, chatRoom, null);
    }
    
    public static ChatRoomEvent userJoined(ChatRoom chatRoom, String userId) {
        return new ChatRoomEvent(EventType.USER_JOINED, chatRoom, userId);
    }
    
    public static ChatRoomEvent userLeft(ChatRoom chatRoom, String userId) {
        return new ChatRoomEvent(EventType.USER_LEFT, chatRoom, userId);
    }
}