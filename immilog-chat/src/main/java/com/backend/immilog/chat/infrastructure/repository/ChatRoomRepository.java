package com.backend.immilog.chat.infrastructure.repository;

import com.backend.immilog.chat.domain.model.ChatRoom;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ChatRoomRepository extends ReactiveMongoRepository<ChatRoom, String> {
    
    Flux<ChatRoom> findByCountryIdAndIsActiveTrue(String countryId);
    
    Flux<ChatRoom> findByIsActiveTrue();
    
    Flux<ChatRoom> findByParticipantIdsContaining(String userId);

    @Query("{ 'isPrivateChat': true, 'isActive': true, 'participantIds': { $all: [?0, ?1] } }")
    Mono<ChatRoom> findPrivateChatRoomByParticipants(String userId1, String userId2);
    
    @Query("{ 'isActive': true, $or: [{'isPrivateChat': false}, {'isPrivateChat': {$exists: false}}] }")
    Flux<ChatRoom> findByIsPrivateChatFalseAndIsActiveTrue();
    
    @Query("{ 'isPrivateChat': true, 'isActive': true, 'participantIds': ?0 }")
    Flux<ChatRoom> findByIsPrivateChatTrueAndParticipantIdsContaining(String userId);
    
    @Query("{ 'createdAt': { $lt: ?0 }, 'isActive': true }")
    Flux<ChatRoom> findByCreatedAtBeforeAndIsActiveTrue(java.time.LocalDateTime cutoffTime);
    
    @Query("{ 'isActive': false, 'updatedAt': { $lt: ?0 } }")
    Flux<ChatRoom> findByIsActiveFalseAndUpdatedAtBefore(java.time.LocalDateTime cutoffTime);
}