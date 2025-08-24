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
        List<ParticipantInfo> participants,  // participantIds -> participants로 변경
        String createdBy,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        int participantCount,
        boolean isActive,
        boolean isPrivateChat,
        LatestMessageDto latestMessage,
        int unreadCount  // 안읽은 메시지 수
) {
    public static ChatRoomDto from(ChatRoom chatRoom) {
        // participantIds를 임시 ParticipantInfo로 변환 (실제 구현에서는 사용자 서비스에서 조회 필요)
        var tempParticipants = chatRoom.participantIds().stream()
                .map(userId -> new ParticipantInfo(userId, null, null))
                .toList();
                
        return new ChatRoomDto(
                chatRoom.id(),
                chatRoom.name(),
                chatRoom.countryId(),
                tempParticipants,
                chatRoom.createdBy(),
                chatRoom.createdAt(),
                chatRoom.participantIds().size(),
                chatRoom.isActive(),
                chatRoom.isPrivate(),
                null, // 최근 메시지는 별도 로직에서 설정
                0 // 안읽은 수는 별도 로직에서 설정
        );
    }
    
    public static ChatRoomDto from(ChatRoom chatRoom, ChatMessage latestMessage, int unreadCount, java.util.Map<String, ParticipantInfo> participantsMap) {
        var participants = chatRoom.participantIds().stream()
                .map(userId -> participantsMap.getOrDefault(userId, new ParticipantInfo(userId, null, null)))
                .toList();
                
        return new ChatRoomDto(
                chatRoom.id(),
                chatRoom.name(),
                chatRoom.countryId(),
                participants,
                chatRoom.createdBy(),
                chatRoom.createdAt(),
                chatRoom.participantIds().size(),
                chatRoom.isActive(),
                chatRoom.isPrivate(),
                latestMessage != null ? LatestMessageDto.from(latestMessage) : null,
                unreadCount
        );
    }
    
    public static ChatRoomDto from(ChatRoom chatRoom, ChatMessage latestMessage) {
        var tempParticipants = chatRoom.participantIds().stream()
                .map(userId -> new ParticipantInfo(userId, null, null))
                .toList();
                
        return new ChatRoomDto(
                chatRoom.id(),
                chatRoom.name(),
                chatRoom.countryId(),
                tempParticipants,
                chatRoom.createdBy(),
                chatRoom.createdAt(),
                chatRoom.participantIds().size(),
                chatRoom.isActive(),
                chatRoom.isPrivate(),
                latestMessage != null ? LatestMessageDto.from(latestMessage) : null,
                0 // 안읽은 수는 별도 로직에서 설정
        );
    }
    
    public static ChatRoomDto from(ChatRoom chatRoom, ChatMessage latestMessage, int unreadCount) {
        var tempParticipants = chatRoom.participantIds().stream()
                .map(userId -> new ParticipantInfo(userId, null, null))
                .toList();
                
        return new ChatRoomDto(
                chatRoom.id(),
                chatRoom.name(),
                chatRoom.countryId(),
                tempParticipants,
                chatRoom.createdBy(),
                chatRoom.createdAt(),
                chatRoom.participantIds().size(),
                chatRoom.isActive(),
                chatRoom.isPrivate(),
                latestMessage != null ? LatestMessageDto.from(latestMessage) : null,
                unreadCount
        );
    }
    
    // 개인채팅방용 생성자 추가
    public static ChatRoomDto fromPrivateChat(ChatRoom chatRoom, ParticipantInfo otherParticipant) {
        var participants = List.of(otherParticipant);
        
        return new ChatRoomDto(
                chatRoom.id(),
                chatRoom.name(),
                chatRoom.countryId(),
                participants,
                chatRoom.createdBy(),
                chatRoom.createdAt(),
                chatRoom.participantIds().size(),
                chatRoom.isActive(),
                chatRoom.isPrivate(),
                null,
                0
        );
    }
    
    public static ChatRoomDto fromPrivateChat(ChatRoom chatRoom, ChatMessage latestMessage, int unreadCount, ParticipantInfo otherParticipant) {
        var participants = List.of(otherParticipant);
        
        return new ChatRoomDto(
                chatRoom.id(),
                chatRoom.name(),
                chatRoom.countryId(),
                participants,
                chatRoom.createdBy(),
                chatRoom.createdAt(),
                chatRoom.participantIds().size(),
                chatRoom.isActive(),
                chatRoom.isPrivate(),
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
    
    public record ParticipantInfo(
            String userId,
            String nickname,
            String profileImage
    ) {}
}