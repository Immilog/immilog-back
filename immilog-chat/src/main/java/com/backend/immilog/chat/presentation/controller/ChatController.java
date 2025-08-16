package com.backend.immilog.chat.presentation.controller;

import com.backend.immilog.chat.application.service.ChatMessageService;
import com.backend.immilog.chat.application.service.ChatRoomService;
import com.backend.immilog.chat.application.service.ChatRoomStreamService;
import com.backend.immilog.chat.presentation.dto.ChatMessageDto;
import com.backend.immilog.chat.presentation.dto.ChatRoomCreateRequest;
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
    
    public ChatController(
            ChatRoomService chatRoomService,
            ChatMessageService chatMessageService,
            ChatRoomStreamService chatRoomStreamService
    ) {
        this.chatRoomService = chatRoomService;
        this.chatMessageService = chatMessageService;
        this.chatRoomStreamService = chatRoomStreamService;
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
        return chatRoomService.getChatRoom(chatRoomId)
                .map(ChatRoomDto::from)
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
    
}