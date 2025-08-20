package com.backend.immilog.chat.application.service;

import com.backend.immilog.chat.infrastructure.repository.ChatRoomReadStatusRepository;
import com.backend.immilog.chat.presentation.dto.UserNotificationDto;
import com.backend.immilog.chat.websocket.UserNotificationWebSocketHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserNotificationService {

    private final UserNotificationWebSocketHandler webSocketHandler;
    private final ChatRoomReadStatusRepository readStatusRepository;

    public UserNotificationService(
            UserNotificationWebSocketHandler webSocketHandler,
            ChatRoomReadStatusRepository readStatusRepository
    ) {
        this.webSocketHandler = webSocketHandler;
        this.readStatusRepository = readStatusRepository;
    }

    public Mono<Void> notifyUnreadCountUpdate(String userId, String chatRoomId) {
        return getUnreadCount(chatRoomId, userId)
                .flatMap(unreadCount -> 
                    getTotalUnreadCount(userId)
                            .map(totalUnreadCount -> {
                                var message = UserNotificationDto.UnreadCountUpdateMessage.create(
                                        chatRoomId,
                                        unreadCount,
                                        totalUnreadCount
                                );
                                webSocketHandler.sendUnreadCountUpdate(userId, message);
                                return message;
                            })
                )
                .then();
    }

    public Mono<Void> notifyUnreadCountUpdateToUsers(String chatRoomId, java.util.List<String> userIds) {
        return reactor.core.publisher.Flux.fromIterable(userIds)
                .flatMap(userId -> notifyUnreadCountUpdate(userId, chatRoomId))
                .then();
    }

    private Mono<Integer> getUnreadCount(String chatRoomId, String userId) {
        return readStatusRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .map(readStatus -> readStatus.unreadCount())
                .defaultIfEmpty(0);
    }

    private Mono<Integer> getTotalUnreadCount(String userId) {
        return readStatusRepository.findByUserId(userId)
                .map(readStatus -> readStatus.unreadCount())
                .reduce(0, Integer::sum);
    }
}