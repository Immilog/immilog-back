package com.backend.immilog.interaction.application.handlers;

import com.backend.immilog.interaction.application.services.InteractionUserQueryService;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.post.domain.events.PostEvent;
import com.backend.immilog.shared.enums.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostEventHandler 테스트")
class PostEventHandlerTest {

    @Mock
    private InteractionUserQueryService interactionUserQueryService;

    @InjectMocks
    private PostEventHandler postEventHandler;

    @Test
    @DisplayName("InteractionDataRequested 이벤트 처리 성공")
    void handleInteractionDataRequested_Success() {
        // given
        List<String> postIds = List.of("post1", "post2");
        PostEvent.InteractionDataRequested event = new PostEvent.InteractionDataRequested(postIds, "POST");
        LocalDateTime now = LocalDateTime.now();
        List<InteractionUser> mockInteractionUsers = List.of(
                new InteractionUser("1", "post1", "user1", ContentType.POST, InteractionType.LIKE, now),
                new InteractionUser("2", "post2", "user2", ContentType.POST, InteractionType.BOOKMARK, now)
        );

        when(interactionUserQueryService.getInteractionUsersByPostIdList(anyList(), any(ContentType.class)))
                .thenReturn(mockInteractionUsers);

        // when
        postEventHandler.handleInteractionDataRequested(event);

        // then
        verify(interactionUserQueryService).getInteractionUsersByPostIdList(postIds, ContentType.POST);
    }

    @Test
    @DisplayName("BookmarkPostsRequested 이벤트 처리 성공")
    void handleBookmarkPostsRequested_Success() {
        // given
        String userId = "user1";
        PostEvent.BookmarkPostsRequested event = new PostEvent.BookmarkPostsRequested(userId, "POST");
        LocalDateTime now = LocalDateTime.now();
        List<InteractionUser> mockBookmarks = List.of(
                new InteractionUser("1", "post1", userId, ContentType.POST, InteractionType.BOOKMARK, now),
                new InteractionUser("2", "post2", userId, ContentType.POST, InteractionType.BOOKMARK, now)
        );

        when(interactionUserQueryService.getBookmarkInteractions(userId, ContentType.POST))
                .thenReturn(mockBookmarks);

        // when
        postEventHandler.handleBookmarkPostsRequested(event);

        // then
        verify(interactionUserQueryService).getBookmarkInteractions(userId, ContentType.POST);
    }

    @Test
    @DisplayName("InteractionDataRequested 이벤트 처리 중 예외 발생")
    void handleInteractionDataRequested_Exception() {
        // given
        List<String> postIds = List.of("post1");
        PostEvent.InteractionDataRequested event = new PostEvent.InteractionDataRequested(postIds, "POST");

        when(interactionUserQueryService.getInteractionUsersByPostIdList(anyList(), any(ContentType.class)))
                .thenThrow(new RuntimeException("Database error"));

        // when & then (예외가 발생해도 핸들러가 정상적으로 처리해야 함)
        postEventHandler.handleInteractionDataRequested(event);

        verify(interactionUserQueryService).getInteractionUsersByPostIdList(postIds, ContentType.POST);
    }

    @Test
    @DisplayName("BookmarkPostsRequested 이벤트 처리 중 예외 발생")
    void handleBookmarkPostsRequested_Exception() {
        // given
        String userId = "user1";
        PostEvent.BookmarkPostsRequested event = new PostEvent.BookmarkPostsRequested(userId, "POST");

        when(interactionUserQueryService.getBookmarkInteractions(userId, ContentType.POST))
                .thenThrow(new RuntimeException("Database error"));

        // when & then (예외가 발생해도 핸들러가 정상적으로 처리해야 함)
        postEventHandler.handleBookmarkPostsRequested(event);

        verify(interactionUserQueryService).getBookmarkInteractions(userId, ContentType.POST);
    }
}