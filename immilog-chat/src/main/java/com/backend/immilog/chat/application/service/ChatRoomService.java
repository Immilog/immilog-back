package com.backend.immilog.chat.application.service;

import com.backend.immilog.chat.domain.event.ChatRoomEvent;
import com.backend.immilog.chat.domain.model.ChatRoom;
import com.backend.immilog.chat.domain.model.ChatMessage;
import com.backend.immilog.chat.infrastructure.repository.ChatRoomRepository;
import com.backend.immilog.chat.infrastructure.repository.ChatMessageRepository;
import com.backend.immilog.chat.presentation.dto.ChatRoomDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ChatRoomService {
    
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    public ChatRoomService(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository, ApplicationEventPublisher eventPublisher) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.eventPublisher = eventPublisher;
    }
    
    public Mono<ChatRoom> createChatRoom(String name, String countryId, String createdBy) {
        var chatRoom = ChatRoom.create(name, countryId, createdBy);
        return chatRoomRepository.save(chatRoom)
                .doOnNext(savedRoom -> eventPublisher.publishEvent(ChatRoomEvent.created(savedRoom)));
    }
    
    public Flux<ChatRoom> getChatRoomsByCountry(String countryId) {
        if ("ALL".equals(countryId)) {
            return chatRoomRepository.findByIsActiveTrue();
        }
        return chatRoomRepository.findByCountryIdAndIsActiveTrue(countryId);
    }
    
    public Flux<ChatRoom> getUserChatRooms(String userId) {
        return chatRoomRepository.findByParticipantIdsContaining(userId);
    }
    
    public Mono<ChatRoom> joinChatRoom(String chatRoomId, String userId) {
        return chatRoomRepository.findById(chatRoomId)
                .map(chatRoom -> chatRoom.addParticipant(userId))
                .flatMap(chatRoomRepository::save)
                .doOnNext(savedRoom -> eventPublisher.publishEvent(ChatRoomEvent.userJoined(savedRoom, userId)));
    }
    
    public Mono<ChatRoom> leaveChatRoom(String chatRoomId, String userId) {
        return chatRoomRepository.findById(chatRoomId)
                .map(chatRoom -> chatRoom.removeParticipant(userId))
                .flatMap(chatRoomRepository::save)
                .doOnNext(savedRoom -> eventPublisher.publishEvent(ChatRoomEvent.userLeft(savedRoom, userId)));
    }
    
    public Mono<ChatRoom> getChatRoom(String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId);
    }
    
    public Mono<Boolean> isUserAlreadyParticipant(String chatRoomId, String userId) {
        return chatRoomRepository.findById(chatRoomId)
                .map(chatRoom -> chatRoom.hasParticipant(userId))
                .defaultIfEmpty(false);
    }
    
    // 최근 메시지를 포함한 채팅방 조회 메서드들
    public Flux<ChatRoomDto> getChatRoomsByCountryWithLatestMessage(String countryId) {
        return getChatRoomsByCountry(countryId)
                .flatMap(this::enrichWithLatestMessage);
    }
    
    public Flux<ChatRoomDto> getUserChatRoomsWithLatestMessage(String userId) {
        return getUserChatRooms(userId)
                .flatMap(this::enrichWithLatestMessage);
    }
    
    private Mono<ChatRoomDto> enrichWithLatestMessage(ChatRoom chatRoom) {
        return chatMessageRepository
                .findByChatRoomIdAndIsDeletedFalseOrderBySentAtDesc(chatRoom.id())
                .take(1)
                .next()
                .map(latestMessage -> ChatRoomDto.from(chatRoom, latestMessage))
                .defaultIfEmpty(ChatRoomDto.from(chatRoom, null));
    }
}