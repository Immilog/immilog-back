package com.backend.immilog.chat.infrastructure.repository;

import com.backend.immilog.chat.domain.model.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage, String> {
    
    Flux<ChatMessage> findByChatRoomIdOrderBySentAtDesc(String chatRoomId, Pageable pageable);
    
    Flux<ChatMessage> findByChatRoomIdAndIsDeletedFalseOrderBySentAtDesc(String chatRoomId);
}