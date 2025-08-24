package com.backend.immilog.chat.application.service;

import com.backend.immilog.chat.domain.event.ChatRoomEvent;
import com.backend.immilog.chat.infrastructure.repository.ChatRoomRepository;
import com.backend.immilog.chat.presentation.dto.ChatRoomDto;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatRoomStreamService {
    
    private final Map<String, Sinks.Many<ChatRoomEvent>> countryStreams = new ConcurrentHashMap<>();
    private final Map<String, Sinks.Many<ChatRoomEvent>> userStreams = new ConcurrentHashMap<>();
    private final ChatRoomRepository chatRoomRepository;
    
    public ChatRoomStreamService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }
    
    public Flux<ChatRoomDto> streamChatRoomsByCountry(String countryId) {
        // 초기 채팅방 목록 + 실시간 업데이트 스트림
        Flux<ChatRoomDto> initialRooms = getChatRoomsByCountry(countryId)
                .map(ChatRoomDto::from);
                
        Flux<ChatRoomDto> eventStream = getOrCreateCountryStream(countryId)
                .asFlux()
                .filter(event -> shouldIncludeEvent(event, countryId))
                .map(event -> ChatRoomDto.from(event.chatRoom()));
        
        return Flux.concat(initialRooms, eventStream).distinct(ChatRoomDto::id);
    }
    
    public Flux<ChatRoomDto> streamUserChatRooms(String userId) {
        // 초기 사용자 참여 채팅방 목록 + 실시간 업데이트 스트림
        Flux<ChatRoomDto> initialRooms = getUserChatRooms(userId)
                .map(ChatRoomDto::from);
                
        Flux<ChatRoomDto> eventStream = getOrCreateUserStream(userId)
                .asFlux()
                .filter(event -> shouldIncludeUserEvent(event, userId))
                .map(event -> ChatRoomDto.from(event.chatRoom()));
        
        return Flux.concat(initialRooms, eventStream).distinct(ChatRoomDto::id);
    }
    
    public void publishEvent(ChatRoomEvent event) {
        String countryId = event.chatRoom().countryId();
        
        // 1:1 채팅방(countryId가 null)이 아닌 경우에만 국가 스트림에 발행
        if (countryId != null) {
            // 해당 국가 스트림에 이벤트 발행
            Sinks.Many<ChatRoomEvent> countryStream = countryStreams.get(countryId);
            if (countryStream != null) {
                countryStream.tryEmitNext(event);
            }
            
            // "ALL" 스트림에도 이벤트 발행
            Sinks.Many<ChatRoomEvent> allStream = countryStreams.get("ALL");
            if (allStream != null) {
                allStream.tryEmitNext(event);
            }
        }
        
        // 사용자별 스트림에 이벤트 발행 (1:1 채팅방도 포함)
        publishToUserStreams(event);
    }
    
    private Sinks.Many<ChatRoomEvent> getOrCreateCountryStream(String countryId) {
        return countryStreams.computeIfAbsent(countryId, 
            k -> Sinks.many().multicast().onBackpressureBuffer());
    }
    
    private boolean shouldIncludeEvent(ChatRoomEvent event, String requestedCountryId) {
        String eventCountryId = event.chatRoom().countryId();
        
        // "ALL" 요청이면 모든 이벤트 포함 (1:1 채팅방 제외)
        if ("ALL".equals(requestedCountryId)) {
            return eventCountryId != null;
        }
        
        // 특정 국가 요청이면 해당 국가 이벤트만 포함 (null 체크 추가)
        return eventCountryId != null && eventCountryId.equals(requestedCountryId);
    }
    
    private Sinks.Many<ChatRoomEvent> getOrCreateUserStream(String userId) {
        return userStreams.computeIfAbsent(userId, 
            k -> Sinks.many().multicast().onBackpressureBuffer());
    }
    
    private void publishToUserStreams(ChatRoomEvent event) {
        // 채팅방 참여자들에게 이벤트 발행
        for (String participantId : event.chatRoom().participantIds()) {
            Sinks.Many<ChatRoomEvent> userStream = userStreams.get(participantId);
            if (userStream != null) {
                userStream.tryEmitNext(event);
            }
        }
        
        // 채팅방 생성자에게도 이벤트 발행 (참여자에 포함되지 않을 수 있으므로)
        String creatorId = event.chatRoom().createdBy();
        Sinks.Many<ChatRoomEvent> creatorStream = userStreams.get(creatorId);
        if (creatorStream != null && !event.chatRoom().participantIds().contains(creatorId)) {
            creatorStream.tryEmitNext(event);
        }
    }
    
    private boolean shouldIncludeUserEvent(ChatRoomEvent event, String userId) {
        // 사용자가 참여 중인 채팅방의 이벤트만 포함
        return event.chatRoom().participantIds().contains(userId) || 
               event.chatRoom().createdBy().equals(userId);
    }
    
    private Flux<com.backend.immilog.chat.domain.model.ChatRoom> getChatRoomsByCountry(String countryId) {
        if ("ALL".equals(countryId)) {
            return chatRoomRepository.findByIsActiveTrue();
        }
        return chatRoomRepository.findByCountryIdAndIsActiveTrue(countryId);
    }
    
    private Flux<com.backend.immilog.chat.domain.model.ChatRoom> getUserChatRooms(String userId) {
        return chatRoomRepository.findByParticipantIdsContaining(userId);
    }
    
    @EventListener
    public void handleChatRoomEvent(ChatRoomEvent event) {
        publishEvent(event);
    }
}