package com.backend.immilog.chat.application.service;

import com.backend.immilog.chat.domain.model.ChatRoomReadStatus;
import com.backend.immilog.chat.domain.service.ChatRoomReadStatusDomainService;
import com.backend.immilog.chat.infrastructure.repository.ChatMessageRepository;
import com.backend.immilog.chat.infrastructure.repository.ChatRoomReadStatusRepository;
import org.junit.jupiter.api.BeforeEach;
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
class ChatReadStatusServiceTest {

    @Mock
    ChatRoomReadStatusRepository readStatusRepository;
    @Mock
    ChatRoomReadStatusDomainService chatRoomReadStatusDomainService;

    @Mock
    ChatMessageRepository messageRepository;

    @Mock
    UserNotificationService userNotificationService;

    @InjectMocks
    ChatReadStatusService service;

    private final String chatRoomId = "room-1";
    private final String userId = "user-1";

    @BeforeEach
    void setUp() {
        // notification service should return Mono<Void> on notify
        when(userNotificationService.notifyUnreadCountUpdate(any(), any()))
                .thenReturn(Mono.empty());

        // switchIfEmpty requires a non-null Mono. Provide a deferred initializer that mimics real behavior:
        // - saves default status via repository
        // - notifies once
        // Using defer ensures no side effects unless subscribed (i.e., only when status is actually absent).
        when(chatRoomReadStatusDomainService.initializeReadStatus(any(), any()))
                .thenAnswer(invocation -> {
                    String room = invocation.getArgument(0);
                    String user = invocation.getArgument(1);
                    return Mono.defer(() ->
                            readStatusRepository.save(ChatRoomReadStatus.create(room, user))
                                    .flatMap(saved -> userNotificationService
                                            .notifyUnreadCountUpdate(user, room)
                                            .thenReturn(saved)
                                    )
                    );
                });
    }

    @Nested
    @DisplayName("메시지 읽음 처리 (markMessageAsRead)")
    class MarkMessageAsRead {
        private final String messageId = "msg-100";

        @Test
        @DisplayName("기존 읽음 상태가 있고 마지막 읽은 시간이 null이면, unreadCount=0으로 업데이트하고 알림을 1회 보낸다")
        void existingStatusWithNullLastReadAt() {
            // given
            var existing = new ChatRoomReadStatus(
                    "id-1",
                    chatRoomId,
                    userId,
                    null, // lastReadMessageId 없음
                    null, // 마지막 읽은 시간 없음
                    5     // 기존 안읽음 수 (업데이트 시 0으로 변경 기대)
            );
            when(readStatusRepository.findByChatRoomIdAndUserId(chatRoomId, userId))
                    .thenReturn(Mono.just(existing));
            // lastReadAt가 null이면 calculateUnreadCount는 0L을 반환하므로 repository count는 사용되지 않음
            // save는 업데이트된 엔티티를 반환
            ArgumentCaptor<ChatRoomReadStatus> captor = ArgumentCaptor.forClass(ChatRoomReadStatus.class);
            when(readStatusRepository.save(any(ChatRoomReadStatus.class)))
                    .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

            // when
            Mono<Void> result = service.markMessageAsRead(chatRoomId, userId, messageId);

            // then
            StepVerifier.create(result).verifyComplete();

            verify(readStatusRepository, times(1)).save(captor.capture());
            ChatRoomReadStatus updated = captor.getValue();
            assertThat(updated.chatRoomId()).isEqualTo(chatRoomId);
            assertThat(updated.userId()).isEqualTo(userId);
            assertThat(updated.lastReadMessageId()).isEqualTo(messageId);
            assertThat(updated.unreadCount()).isEqualTo(0);
            // lastReadAt가 업데이트되어 현재와 가까워야 함
            assertThat(Duration.between(updated.lastReadAt(), LocalDateTime.now()).abs())
                    .isLessThan(Duration.ofSeconds(5));

            // 알림이 1회 호출됨 (markMessageAsRead 체인의 마지막 flatMap)
            verify(userNotificationService, times(1))
                    .notifyUnreadCountUpdate(eq(userId), eq(chatRoomId));
        }

        @Test
        @DisplayName("기존 읽음 상태가 있고 마지막 읽은 시간 이후 N개의 메시지가 있으면, unreadCount=N으로 업데이트하고 알림을 1회 보낸다")
        void existingStatusWithUnreadMessages() {
            // given
            var lastReadAt = LocalDateTime.now().minusMinutes(10);
            var existing = new ChatRoomReadStatus(
                    "id-2",
                    chatRoomId,
                    userId,
                    "prev-msg",
                    lastReadAt,
                    7
            );
            when(readStatusRepository.findByChatRoomIdAndUserId(chatRoomId, userId))
                    .thenReturn(Mono.just(existing));

            long unread = 4L;
            when(messageRepository.countByChatRoomIdAndSentAtAfter(eq(chatRoomId), any(LocalDateTime.class)))
                    .thenReturn(Mono.just(unread));

            ArgumentCaptor<ChatRoomReadStatus> captor = ArgumentCaptor.forClass(ChatRoomReadStatus.class);
            when(readStatusRepository.save(any(ChatRoomReadStatus.class)))
                    .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

            // when
            Mono<Void> result = service.markMessageAsRead(chatRoomId, userId, messageId);

            // then
            StepVerifier.create(result).verifyComplete();

            verify(readStatusRepository, times(1)).save(captor.capture());
            ChatRoomReadStatus updated = captor.getValue();
            assertThat(updated.lastReadMessageId()).isEqualTo(messageId);
            assertThat(updated.unreadCount()).isEqualTo((int) unread);
            assertThat(updated.chatRoomId()).isEqualTo(chatRoomId);
            assertThat(updated.userId()).isEqualTo(userId);

            verify(userNotificationService, times(1))
                    .notifyUnreadCountUpdate(eq(userId), eq(chatRoomId));
        }

        @Test
        @DisplayName("읽음 상태가 없으면 초기화 후 업데이트되며, 저장은 2회(초기화+업데이트), 알림은 2회(초기화+읽음 처리) 호출된다")
        void absentStatusTriggersInitializeAndUpdate() {
            // given: 처음 조회는 비어있음
            when(readStatusRepository.findByChatRoomIdAndUserId(chatRoomId, userId))
                    .thenReturn(Mono.empty());

            // initializeReadStatus 내 save 동작과 markMessageAsRead 내 save 동작을 모두 에코하도록 설정
            ArgumentCaptor<ChatRoomReadStatus> captor = ArgumentCaptor.forClass(ChatRoomReadStatus.class);
            when(readStatusRepository.save(any(ChatRoomReadStatus.class)))
                    .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

            // 초기화 후 lastReadAt은 now로 세팅되므로, count는 임의 값으로 반환 (0 또는 임의 수 모두 가능)
            long unread = 2L;
            when(messageRepository.countByChatRoomIdAndSentAtAfter(eq(chatRoomId), any(LocalDateTime.class)))
                    .thenReturn(Mono.just(unread));

            // when
            Mono<Void> result = service.markMessageAsRead(chatRoomId, userId, messageId);

            // then
            StepVerifier.create(result).verifyComplete();

            // save는 2회 호출: 1) initializeReadStatus에서 기본값 저장, 2) 업데이트 저장
            verify(readStatusRepository, times(2)).save(captor.capture());
            var savedEntities = captor.getAllValues();
            assertThat(savedEntities).hasSize(2);

            ChatRoomReadStatus initialized = savedEntities.get(0);
            assertThat(initialized.chatRoomId()).isEqualTo(chatRoomId);
            assertThat(initialized.userId()).isEqualTo(userId);
            assertThat(initialized.lastReadMessageId()).isNull();
            assertThat(initialized.unreadCount()).isZero();

            ChatRoomReadStatus updated = savedEntities.get(1);
            assertThat(updated.lastReadMessageId()).isEqualTo(messageId);
            assertThat(updated.unreadCount()).isEqualTo((int) unread);

            // 알림은 initializeReadStatus 1회 + markMessageAsRead 1회 = 총 2회
            verify(userNotificationService, times(2))
                    .notifyUnreadCountUpdate(eq(userId), eq(chatRoomId));
        }
    }
}
