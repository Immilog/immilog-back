package com.backend.immilog.chat.domain.service.usecase;

import com.backend.immilog.chat.application.service.UserNotificationService;
import com.backend.immilog.chat.domain.model.ChatRoomReadStatus;
import com.backend.immilog.chat.infrastructure.repository.ChatRoomReadStatusRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static com.backend.immilog.chat.domain.service.ChatRoomReadStatusDomainService.log;

public interface ChatRoomReadStatusInitializeUseCase {

    Mono<ChatRoomReadStatus> initializeReadStatus(String chatRoomId, String userId);

    @Component
    class InitializeChatRoomReadStatus implements ChatRoomReadStatusInitializeUseCase {
        private final ChatRoomReadStatusRepository chatRoomReadStatusRepository;
        private final UserNotificationService userNotificationService;

        InitializeChatRoomReadStatus(
                ChatRoomReadStatusRepository chatRoomReadStatusRepository,
                UserNotificationService userNotificationService
        ) {
            this.chatRoomReadStatusRepository = chatRoomReadStatusRepository;
            this.userNotificationService = userNotificationService;
        }

        @Override
        public Mono<ChatRoomReadStatus> initializeReadStatus(String chatRoomId, String userId) {
            return chatRoomReadStatusRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                    .switchIfEmpty(Mono.defer(() -> chatRoomReadStatusRepository.save(ChatRoomReadStatus.create(chatRoomId, userId))))
                    .publishOn(Schedulers.boundedElastic())
                    .doOnSuccess(result -> userNotificationService.notifyUnreadCountUpdate(userId, chatRoomId).subscribe())
                    .doOnError(signal -> {
                        log.error("Failed to initialize read status for user {} in chat room {}: {}", userId, chatRoomId, signal.getMessage());
                    });
        }
    }
}
