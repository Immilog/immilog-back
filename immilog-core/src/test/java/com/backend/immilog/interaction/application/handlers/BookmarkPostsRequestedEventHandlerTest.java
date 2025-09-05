package com.backend.immilog.interaction.application.handlers;

import com.backend.immilog.interaction.application.services.InteractionUserQueryService;
import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.shared.domain.event.BookmarkDataRequestedEvent;
import com.backend.immilog.shared.enums.ContentType;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookmarkPostsRequestedEventHandler 테스트")
class BookmarkPostsRequestedEventHandlerTest {

    @Mock
    private InteractionUserQueryService interactionUserQueryService;
    
    @Mock
    private EventResultStorageService eventResultStorageService;

    private BookmarkPostsRequestedEventHandler bookmarkPostsRequestedEventHandler;

    @BeforeEach
    void setUp() {
        bookmarkPostsRequestedEventHandler = new BookmarkPostsRequestedEventHandler(
            interactionUserQueryService, eventResultStorageService);
    }

    @Test
    @DisplayName("북마크 게시물 요청 이벤트 처리 성공")
    void handleBookmarkPostsRequestedEventSuccessfully() {
        // given
        String requestId = "bookmark-request-123";
        String userId = "user1";
        String contentType = "POST";
        String requestingDomain = "post";
        
        BookmarkDataRequestedEvent event = new BookmarkDataRequestedEvent(
            requestId, userId, contentType, requestingDomain);
        
        List<InteractionUser> mockBookmarkInteractions = List.of(
            createMockInteractionUser("interaction1", "post1", userId),
            createMockInteractionUser("interaction2", "post2", userId),
            createMockInteractionUser("interaction3", "post3", userId)
        );
        
        when(interactionUserQueryService.getBookmarkInteractions(
            userId, ContentType.valueOf(contentType), InteractionStatus.ACTIVE))
            .thenReturn(mockBookmarkInteractions);

        // when
        bookmarkPostsRequestedEventHandler.handle(event);

        // then
        ArgumentCaptor<List<String>> postIdsCaptor = ArgumentCaptor.forClass(List.class);
        verify(eventResultStorageService).storeBookmarkData(eq(requestId), postIdsCaptor.capture());
        
        List<String> capturedPostIds = postIdsCaptor.getValue();
        assertThat(capturedPostIds).hasSize(3);
        assertThat(capturedPostIds).containsExactly("post1", "post2", "post3");
        
        verify(interactionUserQueryService).getBookmarkInteractions(
            userId, ContentType.POST, InteractionStatus.ACTIVE);
    }

    @Test
    @DisplayName("북마크된 게시물이 없는 경우 빈 리스트 저장")
    void handleBookmarkPostsRequestedEventWithNoBookmarks() {
        // given
        String requestId = "bookmark-request-empty";
        String userId = "user1";
        String contentType = "POST";
        String requestingDomain = "post";
        
        BookmarkDataRequestedEvent event = new BookmarkDataRequestedEvent(
            requestId, userId, contentType, requestingDomain);
        
        when(interactionUserQueryService.getBookmarkInteractions(
            userId, ContentType.valueOf(contentType), InteractionStatus.ACTIVE))
            .thenReturn(List.of());

        // when
        bookmarkPostsRequestedEventHandler.handle(event);

        // then
        ArgumentCaptor<List<String>> postIdsCaptor = ArgumentCaptor.forClass(List.class);
        verify(eventResultStorageService).storeBookmarkData(eq(requestId), postIdsCaptor.capture());
        
        List<String> capturedPostIds = postIdsCaptor.getValue();
        assertThat(capturedPostIds).isEmpty();
    }

    @Test
    @DisplayName("북마크 조회 실패 시 로그만 출력하고 종료")
    void handleBookmarkPostsRequestedEventWithFailure() {
        // given
        String requestId = "bookmark-request-fail";
        String userId = "user1";
        String contentType = "POST";
        String requestingDomain = "post";
        
        BookmarkDataRequestedEvent event = new BookmarkDataRequestedEvent(
            requestId, userId, contentType, requestingDomain);
        
        when(interactionUserQueryService.getBookmarkInteractions(
            userId, ContentType.valueOf(contentType), InteractionStatus.ACTIVE))
            .thenThrow(new RuntimeException("Database connection error"));

        // when
        bookmarkPostsRequestedEventHandler.handle(event);

        // then - 예외가 던져지지 않고 정상 종료됨
        verify(interactionUserQueryService).getBookmarkInteractions(
            userId, ContentType.POST, InteractionStatus.ACTIVE);
        
        // storeBookmarkData가 호출되지 않음을 확인
        verify(eventResultStorageService, never()).storeBookmarkData(any(), any());
    }

    @Test
    @DisplayName("COMMENT 타입 북마크 처리")
    void handleBookmarkPostsRequestedEventForComments() {
        // given
        String requestId = "comment-bookmark-request-123";
        String userId = "user1";
        String contentType = "COMMENT";
        String requestingDomain = "post";
        
        BookmarkDataRequestedEvent event = new BookmarkDataRequestedEvent(
            requestId, userId, contentType, requestingDomain);
        
        List<InteractionUser> mockBookmarkInteractions = List.of(
            createMockInteractionUser("interaction1", "comment1", userId)
        );
        
        when(interactionUserQueryService.getBookmarkInteractions(
            userId, ContentType.valueOf(contentType), InteractionStatus.ACTIVE))
            .thenReturn(mockBookmarkInteractions);

        // when
        bookmarkPostsRequestedEventHandler.handle(event);

        // then
        verify(interactionUserQueryService).getBookmarkInteractions(
            userId, ContentType.COMMENT, InteractionStatus.ACTIVE);
        
        ArgumentCaptor<List<String>> postIdsCaptor = ArgumentCaptor.forClass(List.class);
        verify(eventResultStorageService).storeBookmarkData(eq(requestId), postIdsCaptor.capture());
        
        List<String> capturedPostIds = postIdsCaptor.getValue();
        assertThat(capturedPostIds).containsExactly("comment1");
    }

    @Test
    @DisplayName("이벤트 타입 반환 확인")
    void getEventType() {
        // when
        Class<BookmarkDataRequestedEvent> eventType = bookmarkPostsRequestedEventHandler.getEventType();

        // then
        assertThat(eventType).isEqualTo(BookmarkDataRequestedEvent.class);
    }

    private InteractionUser createMockInteractionUser(String interactionId, String postId, String userId) {
        InteractionUser interactionUser = mock(InteractionUser.class);
        when(interactionUser.postId()).thenReturn(postId);
        when(interactionUser.userId()).thenReturn(userId);
        when(interactionUser.id()).thenReturn(interactionId);
        return interactionUser;
    }
}