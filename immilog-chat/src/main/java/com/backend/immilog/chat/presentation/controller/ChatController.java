package com.backend.immilog.chat.presentation.controller;

import com.backend.immilog.chat.application.service.ChatMessageService;
import com.backend.immilog.chat.application.service.ChatReadStatusService;
import com.backend.immilog.chat.application.service.ChatRoomService;
import com.backend.immilog.chat.application.service.ChatRoomStreamService;
import com.backend.immilog.chat.presentation.dto.ChatMessageDto;
import com.backend.immilog.chat.presentation.dto.ChatReadStatusDto;
import com.backend.immilog.chat.presentation.dto.ChatRoomCreateRequest;
import com.backend.immilog.chat.presentation.dto.PrivateChatRoomCreateRequest;
import com.backend.immilog.chat.presentation.dto.ChatRoomDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final ChatRoomStreamService chatRoomStreamService;
    private final ChatReadStatusService chatReadStatusService;
    
    public ChatController(
            ChatRoomService chatRoomService,
            ChatMessageService chatMessageService,
            ChatRoomStreamService chatRoomStreamService,
            ChatReadStatusService chatReadStatusService
    ) {
        this.chatRoomService = chatRoomService;
        this.chatMessageService = chatMessageService;
        this.chatRoomStreamService = chatRoomStreamService;
        this.chatReadStatusService = chatReadStatusService;
    }
    
    @PostMapping("/rooms")
    public Mono<ResponseEntity<ChatRoomDto>> createChatRoom(
            @RequestBody ChatRoomCreateRequest request
    ) {
        return chatRoomService
                .createChatRoom(request.name(), request.countryId(), request.createdBy())
                .map(ChatRoomDto::from)
                .map(ResponseEntity::ok);
    }
    
    @PostMapping("/rooms/private")
    public Mono<ResponseEntity<ChatRoomDto>> createPrivateChatRoom(
            @RequestBody PrivateChatRoomCreateRequest request,
            @RequestParam("createdBy") String createdBy
    ) {
        return chatRoomService
                .createPrivateChatRoom(createdBy, request.targetUserId())
                .map(ChatRoomDto::from)
                .map(ResponseEntity::ok);
    }
    
    @GetMapping("/rooms/country/{countryId}")
    public Flux<ChatRoomDto> getChatRoomsByCountry(
            @PathVariable("countryId") String countryId,
            @RequestParam(value = "includeLatestMessage", defaultValue = "true") boolean includeLatestMessage
    ) {
        if (includeLatestMessage) {
            return chatRoomService.getChatRoomsByCountryWithLatestMessage(countryId);
        }
        return chatRoomService.getChatRoomsByCountry(countryId).map(ChatRoomDto::from);
    }
    
    @GetMapping(value = "/rooms/country/{countryId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatRoomDto> streamChatRoomsByCountry(
            @PathVariable("countryId") String countryId
    ) {
        return chatRoomStreamService.streamChatRoomsByCountry(countryId);
    }
    
    @GetMapping("/rooms/user/{userId}")
    public Flux<ChatRoomDto> getUserChatRooms(
            @PathVariable("userId") String userId,
            @RequestParam(value = "includeLatestMessage", defaultValue = "true") boolean includeLatestMessage
    ) {
        if (includeLatestMessage) {
            return chatRoomService.getUserChatRoomsWithLatestMessage(userId);
        }
        return chatRoomService.getUserChatRooms(userId).map(ChatRoomDto::from);
    }
    
    @GetMapping("/rooms/private/user/{userId}")
    public Flux<ChatRoomDto> getPrivateChatRooms(
            @PathVariable("userId") String userId,
            @RequestParam(value = "includeLatestMessage", defaultValue = "true") boolean includeLatestMessage
    ) {
        if (includeLatestMessage) {
            return chatRoomService.getPrivateChatRoomsWithLatestMessage(userId);
        }
        return chatRoomService.getPrivateChatRooms(userId).map(ChatRoomDto::from);
    }
    
    @GetMapping(value = "/rooms/user/{userId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatRoomDto> streamUserChatRooms(
            @PathVariable("userId") String userId
    ) {
        return chatRoomStreamService.streamUserChatRooms(userId);
    }
    
    @PostMapping("/rooms/{chatRoomId}/join")
    public Mono<ResponseEntity<ChatRoomDto>> joinChatRoom(
            @PathVariable("chatRoomId") String chatRoomId,
            @RequestParam("userId") String userId
    ) {
        return chatRoomService.joinChatRoom(chatRoomId, userId)
                .map(ChatRoomDto::from)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/rooms/{chatRoomId}")
    public Mono<ResponseEntity<ChatRoomDto>> getChatRoom(
            @PathVariable("chatRoomId") String chatRoomId
    ) {
        return chatRoomService.getChatRoomWithParticipants(chatRoomId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/rooms/{chatRoomId}/messages")
    public Flux<ChatMessageDto> getChatHistory(
            @PathVariable("chatRoomId") String chatRoomId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size
    ) {
        return chatMessageService.getChatHistory(chatRoomId, page, size).map(ChatMessageDto::from);
    }
    
    @GetMapping("/rooms/{chatRoomId}/messages/recent")
    public Flux<ChatMessageDto> getRecentMessages(
            @PathVariable("chatRoomId") String chatRoomId
    ) {
        return chatMessageService.getRecentMessages(chatRoomId).map(ChatMessageDto::from);
    }
    
    @DeleteMapping("/messages/{messageId}")
    public Mono<ResponseEntity<ChatMessageDto>> deleteMessage(
            @PathVariable("messageId") String messageId
    ) {
        return chatMessageService.deleteMessage(messageId)
                .map(ChatMessageDto::from)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    
    /**
     * 채팅방의 모든 메시지를 읽음 처리
     */
    @PostMapping("/rooms/{chatRoomId}/read-all")
    public Mono<ResponseEntity<Void>> markAllMessagesAsRead(
            @PathVariable("chatRoomId") String chatRoomId,
            @RequestParam("userId") String userId
    ) {
        return chatReadStatusService.markAllMessagesAsRead(chatRoomId, userId)
                .then(Mono.just(ResponseEntity.ok().build()));
    }
    
    /**
     * 특정 채팅방의 안읽은 메시지 수 조회
     */
    @GetMapping("/rooms/{chatRoomId}/unread-count")
    public Mono<ResponseEntity<ChatReadStatusDto.UnreadCountResponse>> getUnreadCount(
            @PathVariable("chatRoomId") String chatRoomId,
            @RequestParam("userId") String userId
    ) {
        return chatReadStatusService.getUnreadCount(chatRoomId, userId)
                .map(unreadCount -> {
                    var response = new ChatReadStatusDto.UnreadCountResponse(chatRoomId, unreadCount);
                    return ResponseEntity.ok(response);
                });
    }
    
    /**
     * 사용자의 모든 채팅방 안읽은 메시지 수 조회
     */
    @GetMapping("/users/{userId}/unread-counts")
    public Mono<ResponseEntity<ChatReadStatusDto.AllUnreadCountsResponse>> getAllUnreadCounts(
            @PathVariable("userId") String userId
    ) {
        return Mono.zip(
                chatReadStatusService.getAllUnreadCounts(userId),
                chatReadStatusService.getTotalUnreadCount(userId)
        ).map(tuple -> {
            var response = new ChatReadStatusDto.AllUnreadCountsResponse(tuple.getT1(), tuple.getT2());
            return ResponseEntity.ok(response);
        });
    }
    
}