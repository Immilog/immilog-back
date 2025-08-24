package com.backend.immilog.chat.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "chat_rooms")
public record ChatRoom(
        @Id
        String id,
        String name,
        String countryId,
        List<String> participantIds,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean isActive,
        Boolean isPrivateChat
) {
    
    public boolean isPrivate() {
        return Boolean.TRUE.equals(isPrivateChat);
    }
    public static ChatRoom create(
            String name,
            String countryId,
            String createdBy
    ) {
        return new ChatRoom(
                null,
                name,
                countryId,
                List.of(createdBy),
                createdBy,
                LocalDateTime.now(),
                LocalDateTime.now(),
                true,
                Boolean.FALSE
        );
    }
    
    public static ChatRoom createPrivateChat(
            String createdBy,
            String targetUserId
    ) {
        return new ChatRoom(
                null,
                null, // 1:1 채팅방은 이름 없음
                null, // 1:1 채팅방은 국가 없음
                List.of(createdBy, targetUserId),
                createdBy,
                LocalDateTime.now(),
                LocalDateTime.now(),
                true,
                Boolean.TRUE
        );
    }
    
    public ChatRoom addParticipant(String userId) {
        if (participantIds.contains(userId)) {
            return this;
        }
        
        var newParticipants = new java.util.ArrayList<>(participantIds);
        newParticipants.add(userId);
        
        return new ChatRoom(
                id,
                name,
                countryId,
                newParticipants,
                createdBy,
                createdAt,
                LocalDateTime.now(),
                isActive,
                isPrivateChat
        );
    }
    
    public ChatRoom removeParticipant(String userId) {
        var newParticipants = new java.util.ArrayList<>(participantIds);
        newParticipants.remove(userId);
        
        return new ChatRoom(
                id,
                name,
                countryId,
                newParticipants,
                createdBy,
                createdAt,
                LocalDateTime.now(),
                isActive,
                isPrivateChat
        );
    }
    
    public boolean hasParticipant(String userId) {
        return participantIds.contains(userId);
    }
    
    public ChatRoom markAsInactive() {
        return new ChatRoom(
                id,
                name,
                countryId,
                participantIds,
                createdBy,
                createdAt,
                LocalDateTime.now(),
                false, // isActive = false
                isPrivateChat
        );
    }
    
    public boolean isOlderThanMinutes(int minutes) {
        return createdAt.isBefore(LocalDateTime.now().minusMinutes(minutes));
    }
}