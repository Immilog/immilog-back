package com.backend.immilog.chat.domain.service.usecase;

import com.backend.immilog.chat.application.service.UserNotificationService;
import com.backend.immilog.chat.domain.model.ChatRoomReadStatus;
import com.backend.immilog.chat.infrastructure.repository.ChatRoomReadStatusRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomReadStatusInitializeUseCaseTest {

    @Mock
    ChatRoomReadStatusRepository chatRoomReadStatusRepository;

    @Mock
    UserNotificationService userNotificationService;

    @InjectMocks
    ChatRoomReadStatusInitializeUseCase.InitializeChatRoomReadStatus useCase;

    private final String chatRoomId = "room-1";
    private final String userId = "user-1";

    @Nested
    @DisplayName("읽음 상태 초기화 UseCase (initializeReadStatus)")
    class InitializeReadStatus {
        @Test
        @DisplayName("이미 존재하면 기존 읽음 상태를 반환하고 저장하지 않으며, 알림은 1회 전송된다")
        void returnsExistingWhenPresent() {
            // given
            var existing = new ChatRoomReadStatus(
                    "id-1",
                    chatRoomId,
                    userId,
                    "lastMsg",
                    LocalDateTime.now().minusMinutes(2),
                    5
            );
            when(chatRoomReadStatusRepository.findByChatRoomIdAndUserId(chatRoomId, userId))
                    .thenReturn(Mono.just(existing));
            when(userNotificationService.notifyUnreadCountUpdate(any(), any()))
                    .thenReturn(Mono.empty());

            // when
            Mono<ChatRoomReadStatus> result = useCase.initializeReadStatus(chatRoomId, userId);

            // then
            StepVerifier.create(result)
                    .expectNext(existing)
                    .verifyComplete();

            verify(chatRoomReadStatusRepository, never()).save(any());
            verify(userNotificationService, times(1))
                    .notifyUnreadCountUpdate(eq(userId), eq(chatRoomId));
        }

        @Test
        @DisplayName("없으면 기본값으로 생성·저장하고 반환하며, 알림은 1회 전송된다")
        void createsWhenAbsent() {
            // given
            when(chatRoomReadStatusRepository.findByChatRoomIdAndUserId(chatRoomId, userId))
                    .thenReturn(Mono.empty());
            when(userNotificationService.notifyUnreadCountUpdate(any(), any()))
                    .thenReturn(Mono.empty());

            ArgumentCaptor<ChatRoomReadStatus> saveCaptor = ArgumentCaptor.forClass(ChatRoomReadStatus.class);
            when(chatRoomReadStatusRepository.save(any(ChatRoomReadStatus.class)))
                    .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

            // when
            Mono<ChatRoomReadStatus> result = useCase.initializeReadStatus(chatRoomId, userId);

            // then
            StepVerifier.create(result)
                    .assertNext(rs -> {
                        assertThat(rs.id()).isNull();
                        assertThat(rs.chatRoomId()).isEqualTo(chatRoomId);
                        assertThat(rs.userId()).isEqualTo(userId);
                        assertThat(rs.lastReadMessageId()).isNull();
                        assertThat(rs.unreadCount()).isZero();
                        assertThat(Duration.between(rs.lastReadAt(), LocalDateTime.now()).abs())
                                .isLessThan(Duration.ofSeconds(5));
                    })
                    .verifyComplete();

            verify(chatRoomReadStatusRepository, times(1)).save(saveCaptor.capture());
            ChatRoomReadStatus saved = saveCaptor.getValue();
            assertThat(saved.chatRoomId()).isEqualTo(chatRoomId);
            assertThat(saved.userId()).isEqualTo(userId);
            assertThat(saved.lastReadMessageId()).isNull();
            assertThat(saved.unreadCount()).isZero();

            verify(userNotificationService, times(1))
                    .notifyUnreadCountUpdate(eq(userId), eq(chatRoomId));
        }
    }
}
