package com.backend.immilog.chat.application.service;

import com.backend.immilog.chat.domain.model.ChatRoom;
import com.backend.immilog.chat.infrastructure.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ChatRoomService {
    
    private final ChatRoomRepository chatRoomRepository;
    
    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }
    
    public Mono<ChatRoom> createChatRoom(String name, String countryId, String createdBy) {
        var chatRoom = ChatRoom.create(name, countryId, createdBy);
        return chatRoomRepository.save(chatRoom);
    }
    
    public Flux<ChatRoom> getChatRoomsByCountry(String countryId) {
        return chatRoomRepository.findByCountryIdAndIsActiveTrue(countryId);
    }
    
    public Flux<ChatRoom> getUserChatRooms(String userId) {
        return chatRoomRepository.findByParticipantIdsContaining(userId);
    }
    
    public Mono<ChatRoom> joinChatRoom(String chatRoomId, String userId) {
        return chatRoomRepository.findById(chatRoomId)
                .map(chatRoom -> chatRoom.addParticipant(userId))
                .flatMap(chatRoomRepository::save);
    }
    
    public Mono<ChatRoom> leaveChatRoom(String chatRoomId, String userId) {
        return chatRoomRepository.findById(chatRoomId)
                .map(chatRoom -> chatRoom.removeParticipant(userId))
                .flatMap(chatRoomRepository::save);
    }
    
    public Mono<ChatRoom> getChatRoom(String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId);
    }
}