package com.backend.immilog.chat.infrastructure.repository;

import com.backend.immilog.chat.domain.model.ChatRoomReadStatus;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ChatRoomReadStatusRepository extends ReactiveMongoRepository<ChatRoomReadStatus, String> {
    
    /**
     * 특정 채팅방의 특정 사용자 읽음 상태 조회
     */
    Mono<ChatRoomReadStatus> findByChatRoomIdAndUserId(String chatRoomId, String userId);
    
    /**
     * 특정 사용자의 모든 채팅방 읽음 상태 조회
     */
    Flux<ChatRoomReadStatus> findByUserId(String userId);
    
    /**
     * 특정 채팅방의 모든 사용자 읽음 상태 조회
     */
    Flux<ChatRoomReadStatus> findByChatRoomId(String chatRoomId);
    
    /**
     * 안읽은 메시지가 있는 채팅방 목록 조회
     */
    @Query("{ 'userId': ?0, 'unreadCount': { $gt: 0 } }")
    Flux<ChatRoomReadStatus> findByUserIdWithUnreadMessages(String userId);
    
    /**
     * 특정 사용자의 총 안읽은 메시지 수 조회
     */
    @Query(value = "{ 'userId': ?0 }", fields = "{ 'unreadCount': 1 }")
    Flux<ChatRoomReadStatus> findUnreadCountsByUserId(String userId);
    
    /**
     * 채팅방 삭제 시 읽음 상태도 함께 삭제
     */
    Mono<Void> deleteByChatRoomId(String chatRoomId);
    
    /**
     * 사용자가 채팅방을 나갔을 때 읽음 상태 삭제
     */
    Mono<Void> deleteByChatRoomIdAndUserId(String chatRoomId, String userId);
}