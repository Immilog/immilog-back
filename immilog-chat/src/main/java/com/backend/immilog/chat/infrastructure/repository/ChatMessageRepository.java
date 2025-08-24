package com.backend.immilog.chat.infrastructure.repository;

import com.backend.immilog.chat.domain.model.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage, String> {
    
    Flux<ChatMessage> findByChatRoomIdOrderBySentAtDesc(String chatRoomId, Pageable pageable);
    
    Flux<ChatMessage> findByChatRoomIdAndIsDeletedFalseOrderBySentAtDesc(String chatRoomId);
    
    /**
     * 가장 최근 메시지 조회
     */
    Mono<ChatMessage> findFirstByChatRoomIdOrderBySentAtDesc(String chatRoomId);
    
    /**
     * 특정 시간 이후의 메시지 수 조회
     */
    Mono<Long> countByChatRoomIdAndSentAtAfter(String chatRoomId, LocalDateTime sentAt);
    
    /**
     * 채팅방의 전체 메시지 수 조회
     */
    Mono<Long> countByChatRoomId(String chatRoomId);
}