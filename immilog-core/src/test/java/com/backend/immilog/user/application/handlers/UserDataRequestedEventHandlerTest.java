package com.backend.immilog.user.application.handlers;

import com.backend.immilog.shared.domain.event.UserDataRequestedEvent;
import com.backend.immilog.shared.domain.model.UserData;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import com.backend.immilog.user.application.services.query.UserQueryService;
import com.backend.immilog.user.domain.model.User;
import com.backend.immilog.user.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDataRequestedEventHandler 테스트")
class UserDataRequestedEventHandlerTest {

    @Mock
    private UserQueryService userQueryService;
    
    @Mock
    private EventResultStorageService eventResultStorageService;

    private UserDataRequestedEventHandler userDataRequestedEventHandler;

    @BeforeEach
    void setUp() {
        userDataRequestedEventHandler = new UserDataRequestedEventHandler(
            userQueryService, eventResultStorageService);
    }

    @Test
    @DisplayName("사용자 데이터 요청 이벤트 처리 성공")
    void handleUserDataRequestedEventSuccessfully() {
        // given
        String requestId = "user-request-123";
        List<String> userIds = List.of("user1", "user2");
        String requestingDomain = "post";
        
        UserDataRequestedEvent event = new UserDataRequestedEvent(requestId, userIds, requestingDomain);
        
        User user1 = createMockUser("user1", "닉네임1", "http://image1.jpg");
        User user2 = createMockUser("user2", "닉네임2", "http://image2.jpg");
        
        when(userQueryService.getUserById("user1")).thenReturn(user1);
        when(userQueryService.getUserById("user2")).thenReturn(user2);

        // when
        userDataRequestedEventHandler.handle(event);

        // then
        ArgumentCaptor<List<UserData>> userDataCaptor = ArgumentCaptor.forClass(List.class);
        verify(eventResultStorageService).storeUserData(eq(requestId), userDataCaptor.capture());
        
        List<UserData> capturedUserData = userDataCaptor.getValue();
        assertThat(capturedUserData).hasSize(2);
        
        UserData userData1 = capturedUserData.get(0);
        assertThat(userData1.userId()).isEqualTo("user1");
        assertThat(userData1.nickname()).isEqualTo("닉네임1");
        assertThat(userData1.imageUrl()).isEqualTo("http://image1.jpg");
        
        UserData userData2 = capturedUserData.get(1);
        assertThat(userData2.userId()).isEqualTo("user2");
        assertThat(userData2.nickname()).isEqualTo("닉네임2");
        assertThat(userData2.imageUrl()).isEqualTo("http://image2.jpg");
        
        verify(userQueryService).getUserById("user1");
        verify(userQueryService).getUserById("user2");
    }

    @Test
    @DisplayName("사용자 조회 실패 시 Unknown 데이터로 처리")
    void handleUserDataRequestedEventWithUserNotFound() {
        // given
        String requestId = "user-request-123";
        List<String> userIds = List.of("user1", "user2");
        String requestingDomain = "post";
        
        UserDataRequestedEvent event = new UserDataRequestedEvent(requestId, userIds, requestingDomain);
        
        User user1 = createMockUser("user1", "닉네임1", "http://image1.jpg");
        
        when(userQueryService.getUserById("user1")).thenReturn(user1);
        when(userQueryService.getUserById("user2")).thenThrow(new RuntimeException("User not found"));

        // when
        userDataRequestedEventHandler.handle(event);

        // then
        ArgumentCaptor<List<UserData>> userDataCaptor = ArgumentCaptor.forClass(List.class);
        verify(eventResultStorageService).storeUserData(eq(requestId), userDataCaptor.capture());
        
        List<UserData> capturedUserData = userDataCaptor.getValue();
        assertThat(capturedUserData).hasSize(2);
        
        UserData userData1 = capturedUserData.get(0);
        assertThat(userData1.userId()).isEqualTo("user1");
        assertThat(userData1.nickname()).isEqualTo("닉네임1");
        
        UserData userData2 = capturedUserData.get(1);
        assertThat(userData2.userId()).isEqualTo("user2");
        assertThat(userData2.nickname()).isEqualTo("Unknown");
        assertThat(userData2.imageUrl()).isNull();
    }

    @Test
    @DisplayName("전체 처리 실패 시 로그만 출력하고 종료")
    void handleUserDataRequestedEventWithGlobalFailure() {
        // given
        String requestId = "user-request-123";
        List<String> userIds = List.of("user1");
        String requestingDomain = "post";
        
        UserDataRequestedEvent event = new UserDataRequestedEvent(requestId, userIds, requestingDomain);
        
        when(userQueryService.getUserById("user1")).thenThrow(new RuntimeException("Database error"));
        doThrow(new RuntimeException("Storage error")).when(eventResultStorageService)
            .storeUserData(eq(requestId), any());

        // when
        userDataRequestedEventHandler.handle(event);

        // then - 예외가 던져지지 않고 정상 종료됨
        verify(userQueryService).getUserById("user1");
    }

    @Test
    @DisplayName("이벤트 타입 반환 확인")
    void getEventType() {
        // when
        Class<UserDataRequestedEvent> eventType = userDataRequestedEventHandler.getEventType();

        // then
        assertThat(eventType).isEqualTo(UserDataRequestedEvent.class);
    }

    private User createMockUser(String userId, String nickname, String imageUrl) {
        User user = mock(User.class);
        UserId userIdObj = mock(UserId.class);
        
        when(user.getUserId()).thenReturn(userIdObj);
        when(userIdObj.value()).thenReturn(userId);
        when(user.getNickname()).thenReturn(nickname);
        when(user.getImageUrl()).thenReturn(imageUrl);
        
        return user;
    }
}