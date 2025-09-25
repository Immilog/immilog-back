package com.backend.immilog.chat.application.service;

import com.backend.immilog.chat.domain.model.ChatRoomReadStatus;
import com.backend.immilog.chat.domain.service.ChatRoomReadStatusDomainService;
import com.backend.immilog.chat.infrastructure.repository.ChatMessageRepository;
import com.backend.immilog.chat.infrastructure.repository.ChatRoomReadStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Service
public class ChatReadStatusService {
    private static final Logger log = LoggerFactory.getLogger(ChatReadStatusService.class);
    private static final Duration API_LATENCY = Duration.ofSeconds(5);

    private final ChatRoomReadStatusDomainService chatRoomReadStatusDomainService;
    private final ChatRoomReadStatusRepository readStatusRepository;
    private final ChatMessageRepository messageRepository;
    private final UserNotificationService userNotificationService;
    private final Executor executor;

    public ChatReadStatusService(
            ChatRoomReadStatusDomainService chatRoomReadStatusDomainService,
            ChatRoomReadStatusRepository readStatusRepository,
            ChatMessageRepository messageRepository,
            UserNotificationService userNotificationService,
            @Qualifier("webfluxExecutor") Executor executor
    ) {
        this.chatRoomReadStatusDomainService = chatRoomReadStatusDomainService;
        this.readStatusRepository = readStatusRepository;
        this.messageRepository = messageRepository;
        this.userNotificationService = userNotificationService;
        this.executor = executor;
    }

    /**
     * 메시지 읽음 처리
     */
    public Mono<Void> markMessageAsRead(String chatRoomId, String userId, String messageId) {
        return readStatusRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .switchIfEmpty(chatRoomReadStatusDomainService.initializeReadStatus(chatRoomId, userId))
                .flatMap(readStatus -> {
                    // 안읽은 메시지 수 계산 (마지막 읽은 메시지 이후의 메시지 수)
                    return calculateUnreadCount(chatRoomId, messageId, readStatus.lastReadAt())
                            .flatMap(unreadCount -> {
                                var updatedStatus = readStatus.updateLastRead(messageId, unreadCount.intValue());
                                return readStatusRepository.save(updatedStatus);
                            });
                })
                .flatMap(result -> userNotificationService.notifyUnreadCountUpdate(userId, chatRoomId))
                .doOnSuccess(result -> { /* Marked message as read and notified */ });
    }

    /**
     * 채팅방의 모든 메시지를 읽음 처리
     */
    public Mono<Void> markAllMessagesAsRead(String chatRoomId, String userId) {
        return readStatusRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .switchIfEmpty(chatRoomReadStatusDomainService.initializeReadStatus(chatRoomId, userId))
                .cache(API_LATENCY)
                .flatMap(readStatus -> {
                    // 가장 최근 메시지 ID 조회
                    return messageRepository.findFirstByChatRoomIdOrderBySentAtDesc(chatRoomId)
                            .map(latestMessage -> {
                                var updatedStatus = readStatus.resetUnreadCount(latestMessage.id());
                                return readStatusRepository.save(updatedStatus);
                            });
                })
                .flatMap(result -> userNotificationService.notifyUnreadCountUpdate(userId, chatRoomId))
                .doOnSuccess(result -> { /* Marked all messages as read and notified */ });
    }

    /**
     * 새 메시지 발송 시 모든 참여자의 안읽은 수 증가
     */
    @Transactional
    public Mono<Void> incrementUnreadCountForParticipants(String chatRoomId, List<String> participantIds, String senderId) {
        var participants = participantIds.stream()
                .filter(userId -> !userId.equals(senderId)) // 발신자 제외
                .toList();

        return Flux.fromIterable(participants)
                .flatMap(userId ->
                    readStatusRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                            .switchIfEmpty(chatRoomReadStatusDomainService.initializeReadStatus(chatRoomId, userId))
                            .flatMap(readStatus -> {
                                var updatedStatus = readStatus.incrementUnreadCount();
                                return readStatusRepository.save(updatedStatus);
                            })
                )
                .then();
    }

    /**
     * 특정 사용자의 안읽은 메시지 수 조회
     */
    public Mono<Integer> getUnreadCount(String chatRoomId, String userId) {
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
    public Mono<Void> removeReadStatus(String chatRoomId, String userId) {
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
    private Mono<Long> calculateUnreadCount(String chatRoomId, String lastReadMessageId, LocalDateTime lastReadAt) {
        if (lastReadAt == null) {
            // 처음 읽는 경우, 현재 메시지까지 모든 메시지를 읽음으로 처리
            return Mono.just(0L);
        }

        // 마지막 읽은 시간 이후의 메시지 수 계산
        return messageRepository.countByChatRoomIdAndSentAtAfter(chatRoomId, lastReadAt);
    }
}