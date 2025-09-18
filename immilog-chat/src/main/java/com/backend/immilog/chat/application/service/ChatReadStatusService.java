package com.backend.immilog.chat.application.service;

import com.backend.immilog.chat.domain.model.ChatRoomReadStatus;
import com.backend.immilog.chat.infrastructure.repository.ChatMessageRepository;
import com.backend.immilog.chat.infrastructure.repository.ChatRoomReadStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ChatReadStatusService {
    
    private final ChatRoomReadStatusRepository readStatusRepository;
    private final ChatMessageRepository messageRepository;
    private final UserNotificationService userNotificationService;
    
    public ChatReadStatusService(
            ChatRoomReadStatusRepository readStatusRepository,
            ChatMessageRepository messageRepository,
            UserNotificationService userNotificationService
    ) {
        this.readStatusRepository = readStatusRepository;
        this.messageRepository = messageRepository;
        this.userNotificationService = userNotificationService;
    }
    
    /**
     * 사용자가 채팅방에 처음 입장할 때 읽음 상태 초기화
     */
    public Mono<ChatRoomReadStatus> initializeReadStatus(
            String chatRoomId,
            String userId
    ) {
        return readStatusRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .switchIfEmpty(Mono.defer(() -> {
                    var newStatus = ChatRoomReadStatus.create(chatRoomId, userId);
                    return readStatusRepository.save(newStatus);
                }))
                .doOnSuccess(result -> userNotificationService.notifyUnreadCountUpdate(userId, chatRoomId).subscribe());
    }
    
    /**
     * 메시지 읽음 처리
     */
    public Mono<Void> markMessageAsRead(
            String chatRoomId,
            String userId,
            String messageId
    ) {
        return readStatusRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .switchIfEmpty(initializeReadStatus(chatRoomId, userId))
                .flatMap(readStatus ->
                        calculateUnreadCount(chatRoomId, readStatus.lastReadAt())
                                .map(unreadCount -> readStatus.updateLastRead(messageId, unreadCount.intValue()))
                                .flatMap(readStatusRepository::save)
                )
                .flatMap(result -> userNotificationService.notifyUnreadCountUpdate(userId, chatRoomId));
    }
    
    /**
     * 채팅방의 모든 메시지를 읽음 처리
     */
    public Mono<Void> markAllMessagesAsRead(
            String chatRoomId,
            String userId
    ) {
        return readStatusRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .switchIfEmpty(initializeReadStatus(chatRoomId, userId))
                .flatMap(readStatus ->
                        messageRepository.findByChatRoomIdAndIsDeletedFalseOrderBySentAtDesc(chatRoomId)
                                .take(1)
                                .next()
                                .map(latestMessage -> readStatus.resetUnreadCount(latestMessage.id()))
                                .flatMap(readStatusRepository::save)
                                .switchIfEmpty(readStatusRepository.save(readStatus.resetUnreadCount(null)))
                )
                .flatMap(result -> userNotificationService.notifyUnreadCountUpdate(userId, chatRoomId));
    }
    
    /**
     * 새 메시지 발송 시 모든 참여자의 안읽은 수 증가
     */
    @Transactional
    public Mono<Void> incrementUnreadCountForParticipants(
            String chatRoomId,
            List<String> participantIds,
            String senderId
    ) {
        var participants = participantIds.stream()
                .filter(userId -> !userId.equals(senderId))
                .toList();
        
        return Flux.fromIterable(participants)
                .flatMap(userId -> 
                    readStatusRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                            .switchIfEmpty(initializeReadStatus(chatRoomId, userId))
                            .map(ChatRoomReadStatus::incrementUnreadCount)
                            .flatMap(readStatusRepository::save)
                )
                .then();
    }
    
    /**
     * 특정 사용자의 안읽은 메시지 수 조회
     */
    public Mono<Integer> getUnreadCount(
            String chatRoomId,
            String userId
    ) {
        return readStatusRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .map(ChatRoomReadStatus::unreadCount)
                .defaultIfEmpty(0);
    }
    
    /**
     * 사용자의 모든 채팅방 안읽은 메시지 수 조회
     */
    public Mono<Map<String, Integer>> getAllUnreadCounts(String userId) {
        return readStatusRepository.findByUserId(userId)
                .collectMap(
                        ChatRoomReadStatus::chatRoomId,
                        ChatRoomReadStatus::unreadCount
                );
    }
    
    /**
     * 사용자의 총 안읽은 메시지 수 조회
     */
    public Mono<Integer> getTotalUnreadCount(String userId) {
        return readStatusRepository.findByUserId(userId)
                .map(ChatRoomReadStatus::unreadCount)
                .reduce(0, Integer::sum);
    }
    
    /**
     * 채팅방 나가기 시 읽음 상태 삭제
     */
    @Transactional
    public Mono<Void> removeReadStatus(
            String chatRoomId,
            String userId
    ) {
        return readStatusRepository.deleteByChatRoomIdAndUserId(chatRoomId, userId);
    }
    
    /**
     * 채팅방 삭제 시 모든 읽음 상태 삭제
     */
    @Transactional
    public Mono<Void> removeAllReadStatusForRoom(String chatRoomId) {
        return readStatusRepository.deleteByChatRoomId(chatRoomId);
    }
    
    /**
     * 안읽은 메시지 수 계산 (마지막 읽은 시간 이후의 메시지 수)
     */
    private Mono<Long> calculateUnreadCount(
            String chatRoomId,
            LocalDateTime lastReadAt
    ) {
        if (lastReadAt == null) {
            // 처음 읽는 경우, 현재 메시지까지 모든 메시지를 읽음으로 처리
            return Mono.just(0L);
        }
        
        // 마지막 읽은 시간 이후의 메시지 수 계산
        return messageRepository.countByChatRoomIdAndSentAtAfter(chatRoomId, lastReadAt);
    }
}