package com.backend.immilog.chat.application.service;

import com.backend.immilog.chat.domain.model.ChatMessage;
import com.backend.immilog.chat.infrastructure.repository.ChatMessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ChatMessageService {
    
    private final ChatMessageRepository chatMessageRepository;
    
    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }
    
    public Mono<ChatMessage> sendMessage(
            String chatRoomId,
            String senderId, 
            String senderNickname,
            String content
    ) {
        var message = ChatMessage.createTextMessage(chatRoomId, senderId, senderNickname, content);
        return chatMessageRepository.save(message);
    }
    
    public Mono<ChatMessage> sendSystemMessage(
            String chatRoomId,
            String userId,
            String nickname,
            ChatMessage.MessageType systemType
    ) {
        var message = ChatMessage.createSystemMessage(chatRoomId, userId, nickname, systemType);
        return chatMessageRepository.save(message);
    }
    
    public Flux<ChatMessage> getChatHistory(String chatRoomId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return chatMessageRepository.findByChatRoomIdOrderBySentAtDesc(chatRoomId, pageRequest);
    }
    
    public Flux<ChatMessage> getRecentMessages(String chatRoomId) {
        return chatMessageRepository.findByChatRoomIdAndIsDeletedFalseOrderBySentAtDesc(chatRoomId).take(50);
    }
    
    public Mono<ChatMessage> deleteMessage(String messageId) {
        return chatMessageRepository.findById(messageId)
                .map(ChatMessage::delete)
                .flatMap(chatMessageRepository::save);
    }
}