package com.backend.immilog.chat.presentation.controller;

import com.backend.immilog.chat.application.service.ChatMessageService;
import com.backend.immilog.chat.application.service.ChatRoomService;
import com.backend.immilog.chat.presentation.dto.ChatMessageDto;
import com.backend.immilog.chat.presentation.dto.ChatRoomDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    
    public ChatController(
            ChatRoomService chatRoomService,
            ChatMessageService chatMessageService
    ) {
        this.chatRoomService = chatRoomService;
        this.chatMessageService = chatMessageService;
    }
    
    @PostMapping("/rooms")
    public Mono<ResponseEntity<ChatRoomDto>> createChatRoom(
            @RequestParam("name") String name,
            @RequestParam("countryId") String countryId,
            @RequestParam("createdBy") String createdBy
    ) {
        return chatRoomService.createChatRoom(name, countryId, createdBy)
                .map(ChatRoomDto::from)
                .map(ResponseEntity::ok);
    }
    
    @GetMapping("/rooms/country/{countryId}")
    public Flux<ChatRoomDto> getChatRoomsByCountry(
            @PathVariable("countryId") String countryId
    ) {
        return chatRoomService.getChatRoomsByCountry(countryId).map(ChatRoomDto::from);
    }
    
    @GetMapping("/rooms/user/{userId}")
    public Flux<ChatRoomDto> getUserChatRooms(
            @PathVariable("userId") String userId
    ) {
        return chatRoomService.getUserChatRooms(userId).map(ChatRoomDto::from);
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