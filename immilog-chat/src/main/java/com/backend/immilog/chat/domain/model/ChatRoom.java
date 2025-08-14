package com.backend.immilog.chat.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
        boolean isActive
) {
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
                true
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
                isActive
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
                isActive
        );
    }
    
    public boolean hasParticipant(String userId) {
        return participantIds.contains(userId);
    }
}