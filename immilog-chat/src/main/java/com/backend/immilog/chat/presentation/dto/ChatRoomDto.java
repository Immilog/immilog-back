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
        return new Builder(chatRoom).build();
    }
    
    public static Builder builder(ChatRoom chatRoom) {
        return new Builder(chatRoom);
    }

    public static class Builder {
        private final ChatRoom chatRoom;
        private ChatMessage latestMessage;
        private int unreadCount = 0;
        private java.util.Map<String, ParticipantInfo> participantsMap;
        private ParticipantInfo otherParticipant;
        private boolean isPrivateChat = false;

        private Builder(ChatRoom chatRoom) {
            this.chatRoom = chatRoom;
        }

        public Builder withLatestMessage(ChatMessage latestMessage) {
            this.latestMessage = latestMessage;
            return this;
        }

        public Builder withUnreadCount(int unreadCount) {
            this.unreadCount = unreadCount;
            return this;
        }

        public Builder withParticipants(java.util.Map<String, ParticipantInfo> participantsMap) {
            this.participantsMap = participantsMap;
            return this;
        }

        public Builder asPrivateChat(ParticipantInfo otherParticipant) {
            this.otherParticipant = otherParticipant;
            this.isPrivateChat = true;
            return this;
        }

        public ChatRoomDto build() {
            List<ParticipantInfo> participants = buildParticipants();
            
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

        private List<ParticipantInfo> buildParticipants() {
            if (isPrivateChat && otherParticipant != null) {
                return List.of(otherParticipant);
            }
            
            if (participantsMap != null) {
                return chatRoom.participantIds().stream()
                        .map(userId -> participantsMap.getOrDefault(userId, new ParticipantInfo(userId, null, null)))
                        .toList();
            }
            
            return chatRoom.participantIds().stream()
                    .map(userId -> new ParticipantInfo(userId, null, null))
                    .toList();
        }
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