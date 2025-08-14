package com.backend.immilog.chat.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat_messages")
public record ChatMessage(
        @Id
        String id,
        String chatRoomId,
        String senderId,
        String senderNickname,
        String content,
        MessageType messageType,
        LocalDateTime sentAt,
        boolean isDeleted
) {
    public enum MessageType {
        TEXT,
        IMAGE,
        FILE,
        SYSTEM_JOIN,
        SYSTEM_LEAVE
    }
    
    public static ChatMessage createTextMessage(
            String chatRoomId,
            String senderId,
            String senderNickname,
            String content
    ) {
        return new ChatMessage(
                null,
                chatRoomId,
                senderId,
                senderNickname,
                content,
                MessageType.TEXT,
                LocalDateTime.now(),
                false
        );
    }
    
    public static ChatMessage createSystemMessage(
            String chatRoomId,
            String userId,
            String nickname,
            MessageType systemType
    ) {
        String content = switch (systemType) {
            case SYSTEM_JOIN -> nickname + "님이 채팅방에 참여했습니다.";
            case SYSTEM_LEAVE -> nickname + "님이 채팅방을 나갔습니다.";
            default -> throw new IllegalArgumentException("Invalid system message type");
        };
        
        return new ChatMessage(
                null,
                chatRoomId,
                userId,
                nickname,
                content,
                systemType,
                LocalDateTime.now(),
                false
        );
    }
    
    public ChatMessage delete() {
        return new ChatMessage(
                id,
                chatRoomId,
                senderId,
                senderNickname,
                "[삭제된 메시지]",
                messageType,
                sentAt,
                true
        );
    }
    
    public boolean isSystemMessage() {
        return messageType == MessageType.SYSTEM_JOIN || messageType == MessageType.SYSTEM_LEAVE;
    }
}