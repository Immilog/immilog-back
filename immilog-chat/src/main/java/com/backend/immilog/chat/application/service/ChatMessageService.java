package com.backend.immilog.chat.application.service;

import com.backend.immilog.chat.domain.model.ChatMessage;
import com.backend.immilog.chat.infrastructure.repository.ChatMessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ChatMessageService {
    
    private final ChatMessageRepository chatMessageRepository;
    private final ChatReadStatusService chatReadStatusService;
    private final ChatRoomService chatRoomService;
    private final UserNotificationService userNotificationService;
    
    public ChatMessageService(
            ChatMessageRepository chatMessageRepository,
            ChatReadStatusService chatReadStatusService,
            ChatRoomService chatRoomService,
            UserNotificationService userNotificationService
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatReadStatusService = chatReadStatusService;
        this.chatRoomService = chatRoomService;
        this.userNotificationService = userNotificationService;
    }
    
    public Mono<ChatMessage> sendMessage(
            String chatRoomId,
            String senderId, 
            String senderNickname,
            String content
    ) {
        var message = ChatMessage.createTextMessage(chatRoomId, senderId, senderNickname, content);
        return chatMessageRepository.save(message)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(savedMessage -> {
                    // 새 메시지 발송 시 다른 참여자들의 안읽은 수 증가 및 실시간 알림
                    chatRoomService.getChatRoom(chatRoomId)
                            .flatMap(chatRoom -> {
                                var participantIds = chatRoom.participantIds().stream()
                                        .filter(userId -> !userId.equals(senderId)) // 발신자 제외
                                        .toList();
                                
                                return chatReadStatusService.incrementUnreadCountForParticipants(
                                        chatRoomId, 
                                        chatRoom.participantIds(), 
                                        senderId
                                )
                                .then(userNotificationService.notifyUnreadCountUpdateToUsers(chatRoomId, participantIds));
                            })
                            .subscribe();
                });
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