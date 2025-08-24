package com.backend.immilog.chat.application.service;

import com.backend.immilog.chat.application.port.UserService;
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
    private final ChatReadStatusService chatReadStatusService;
    private final ApplicationEventPublisher eventPublisher;
    private final UserService userService;
    
    public ChatRoomService(
            ChatRoomRepository chatRoomRepository, 
            ChatMessageRepository chatMessageRepository,
            ChatReadStatusService chatReadStatusService,
            ApplicationEventPublisher eventPublisher,
            UserService userService
    ) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatReadStatusService = chatReadStatusService;
        this.eventPublisher = eventPublisher;
        this.userService = userService;
    }
    
    public Mono<ChatRoom> createChatRoom(String name, String countryId, String createdBy) {
        var chatRoom = ChatRoom.create(name, countryId, createdBy);
        return chatRoomRepository.save(chatRoom)
                .doOnNext(savedRoom -> eventPublisher.publishEvent(ChatRoomEvent.created(savedRoom)));
    }
    
    public Mono<ChatRoom> createPrivateChatRoom(String createdBy, String targetUserId) {
        // 이미 존재하는 1:1 채팅방 확인
        return chatRoomRepository.findPrivateChatRoomByParticipants(createdBy, targetUserId)
                .cast(ChatRoom.class)
                .switchIfEmpty(
                    Mono.defer(() -> {
                        var chatRoom = ChatRoom.createPrivateChat(createdBy, targetUserId);
                        return chatRoomRepository.save(chatRoom)
                                .doOnNext(savedRoom -> eventPublisher.publishEvent(ChatRoomEvent.created(savedRoom)));
                    })
                );
    }
    
    public Flux<ChatRoom> getChatRoomsByCountry(String countryId) {
        if ("ALL".equals(countryId)) {
            return chatRoomRepository.findByIsPrivateChatFalseAndIsActiveTrue();
        }
        return chatRoomRepository.findByCountryIdAndIsActiveTrue(countryId);
    }
    
    public Flux<ChatRoom> getPrivateChatRooms(String userId) {
        return chatRoomRepository.findByIsPrivateChatTrueAndParticipantIdsContaining(userId);
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
    
    public Mono<ChatRoomDto> getChatRoomWithParticipants(String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .flatMap(this::enrichWithParticipants);
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
                .flatMap(chatRoom -> enrichWithLatestMessageAndUnreadCount(chatRoom, userId));
    }
    
    public Flux<ChatRoomDto> getPrivateChatRoomsWithLatestMessage(String userId) {
        return getPrivateChatRooms(userId)
                .flatMap(chatRoom -> enrichWithLatestMessageAndUnreadCount(chatRoom, userId));
    }
    
    private Mono<ChatRoomDto> enrichWithLatestMessage(ChatRoom chatRoom) {
        var latestMessageMono = chatMessageRepository
                .findByChatRoomIdAndIsDeletedFalseOrderBySentAtDesc(chatRoom.id())
                .take(1)
                .next()
                .switchIfEmpty(Mono.empty());
                
        var participantsMono = userService.getParticipantsInfo(chatRoom.participantIds());
        
        return Mono.zip(latestMessageMono, participantsMono)
                .map(tuple -> ChatRoomDto.from(chatRoom, tuple.getT1(), 0, tuple.getT2()))
                .switchIfEmpty(participantsMono.map(participants -> 
                    ChatRoomDto.from(chatRoom, null, 0, participants)));
    }
    
    private Mono<ChatRoomDto> enrichWithLatestMessageAndUnreadCount(ChatRoom chatRoom, String userId) {
        System.out.println("enrichWithLatestMessageAndUnreadCount called for chatRoom: " + chatRoom.id() + ", participants: " + chatRoom.participantIds());
        
        var latestMessageMono = chatMessageRepository
                .findByChatRoomIdAndIsDeletedFalseOrderBySentAtDesc(chatRoom.id())
                .take(1)
                .next()
                .switchIfEmpty(Mono.empty());
        
        var unreadCountMono = chatReadStatusService.getUnreadCount(chatRoom.id(), userId);
        var participantsMono = userService.getParticipantsInfo(chatRoom.participantIds())
                .doOnNext(participants -> System.out.println("Got participants: " + participants));
        
        return Mono.zip(latestMessageMono, unreadCountMono, participantsMono)
                .map(tuple -> ChatRoomDto.from(chatRoom, tuple.getT1(), tuple.getT2(), tuple.getT3()))
                .switchIfEmpty(
                    Mono.zip(unreadCountMono, participantsMono)
                        .map(tuple -> ChatRoomDto.from(chatRoom, null, tuple.getT1(), tuple.getT2()))
                );
    }
    
    private Mono<ChatRoomDto> enrichWithParticipants(ChatRoom chatRoom) {
        System.out.println("enrichWithParticipants called for chatRoom: " + chatRoom.id() + ", participants: " + chatRoom.participantIds());
        
        var participantsMono = userService.getParticipantsInfo(chatRoom.participantIds())
                .doOnNext(participants -> System.out.println("Got participants: " + participants));
        
        return participantsMono.map(participants -> ChatRoomDto.from(chatRoom, null, 0, participants));
    }
}