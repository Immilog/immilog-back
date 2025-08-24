package com.backend.immilog.chat.application.service;

import com.backend.immilog.chat.domain.model.ChatRoom;
import com.backend.immilog.chat.infrastructure.repository.ChatMessageRepository;
import com.backend.immilog.chat.infrastructure.repository.ChatRoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class ChatRoomCleanupService {
    
    private static final Logger log = LoggerFactory.getLogger(ChatRoomCleanupService.class);
    
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    
    public ChatRoomCleanupService(
            ChatRoomRepository chatRoomRepository, 
            ChatMessageRepository chatMessageRepository
    ) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
    }
    
    /**
     * 빈 채팅방을 비활성화 (10분마다 실행)
     * 조건: 생성된 지 30분 이상 && 메시지 0개 && 개인채팅방만
     */
    @Scheduled(fixedRate = 600000) // 10분마다
    public void markInactiveEmptyRooms() {
        var cutoffTime = LocalDateTime.now().minusMinutes(30);
        
        log.info("Starting empty room cleanup for rooms older than: {}", cutoffTime);
        
        chatRoomRepository.findByCreatedAtBeforeAndIsActiveTrue(cutoffTime)
                .filter(this::isPrivateChat) // 개인채팅방만
                .filterWhen(this::hasNoMessages) // 메시지 없는 방만
                .flatMap(this::markAsInactiveWithLogging)
                .doOnComplete(() -> log.info("Empty room cleanup completed"))
                .subscribe();
    }
    
    /**
     * 비활성화된 채팅방을 완전 삭제 (1시간마다 실행)
     * 조건: 비활성화된 지 24시간 이상
     */
    @Scheduled(fixedRate = 3600000) // 1시간마다
    public void deleteInactiveRooms() {
        var cutoffTime = LocalDateTime.now().minusDays(1);
        
        log.info("Starting inactive room deletion for rooms inactive since: {}", cutoffTime);
        
        chatRoomRepository.findByIsActiveFalseAndUpdatedAtBefore(cutoffTime)
                .flatMap(this::deleteRoomWithLogging)
                .doOnComplete(() -> log.info("Inactive room deletion completed"))
                .subscribe();
    }
    
    private boolean isPrivateChat(ChatRoom chatRoom) {
        return Boolean.TRUE.equals(chatRoom.isPrivate());
    }
    
    private Mono<Boolean> hasNoMessages(ChatRoom chatRoom) {
        return chatMessageRepository.countByChatRoomId(chatRoom.id())
                .map(count -> count == 0)
                .defaultIfEmpty(true);
    }
    
    private Mono<ChatRoom> markAsInactiveWithLogging(ChatRoom chatRoom) {
        log.warn("Marking empty room as inactive: {} (created: {}, participants: {})", 
                chatRoom.id(), chatRoom.createdAt(), chatRoom.participantIds().size());
        
        return chatRoomRepository.save(chatRoom.markAsInactive())
                .doOnSuccess(room -> log.info("Successfully marked room {} as inactive", room.id()));
    }
    
    private Mono<Void> deleteRoomWithLogging(ChatRoom chatRoom) {
        log.warn("Permanently deleting inactive room: {} (inactive since: {})", 
                chatRoom.id(), chatRoom.updatedAt());
        
        return chatRoomRepository.delete(chatRoom)
                .doOnSuccess(v -> log.info("Successfully deleted room {}", chatRoom.id()));
    }
}