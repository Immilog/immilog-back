package com.backend.immilog.chat.infrastructure.repository;

import com.backend.immilog.chat.domain.model.ChatRoom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatRoomRepository extends ReactiveMongoRepository<ChatRoom, String> {
    
    Flux<ChatRoom> findByCountryIdAndIsActiveTrue(String countryId);
    
    Flux<ChatRoom> findByIsActiveTrue();
    
    Flux<ChatRoom> findByParticipantIdsContaining(String userId);
    
    Flux<ChatRoom> findByCreatedBy(String userId);
}