package com.backend.immilog.chat.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;

import java.time.LocalDateTime;

@Document(collection = "chat_room_read_status")
@CompoundIndex(name = "chatroom_user_idx", def = "{'chatRoomId': 1, 'userId': 1}", unique = true)
public record ChatRoomReadStatus(
        @Id
        String id,
        String chatRoomId,
        String userId,
        String lastReadMessageId,    // 마지막으로 읽은 메시지 ID
        LocalDateTime lastReadAt,    // 마지막 읽은 시간
        int unreadCount             // 안읽은 메시지 수
) {
    
    public static ChatRoomReadStatus create(
            String chatRoomId,
            String userId
    ) {
        return new ChatRoomReadStatus(
                null,
                chatRoomId,
                userId,
                null,  // 아직 읽은 메시지 없음
                LocalDateTime.now(),
                0
        );
    }
    
    public ChatRoomReadStatus updateLastRead(
            String lastReadMessageId,
            int unreadCount
    ) {
        return new ChatRoomReadStatus(
                id,
                chatRoomId,
                userId,
                lastReadMessageId,
                LocalDateTime.now(),
                unreadCount
        );
    }
    
    public ChatRoomReadStatus incrementUnreadCount() {
        return new ChatRoomReadStatus(
                id,
                chatRoomId,
                userId,
                lastReadMessageId,
                lastReadAt,
                unreadCount + 1
        );
    }
    
    public ChatRoomReadStatus resetUnreadCount(String lastReadMessageId) {
        return new ChatRoomReadStatus(
                id,
                chatRoomId,
                userId,
                lastReadMessageId,
                LocalDateTime.now(),
                0
        );
    }
}