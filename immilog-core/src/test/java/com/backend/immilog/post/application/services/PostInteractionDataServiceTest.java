package com.backend.immilog.post.application.services;

import com.backend.immilog.shared.domain.event.DomainEventPublisher;
import com.backend.immilog.shared.domain.event.InteractionDataRequestedEvent;
import com.backend.immilog.shared.domain.model.InteractionData;
import com.backend.immilog.shared.enums.ContentType;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostInteractionDataService 테스트")
class PostInteractionDataServiceTest {

    @Mock
    private DomainEventPublisher eventPublisher;
    
    @Mock
    private EventResultStorageService eventResultStorageService;

    private PostInteractionDataService postInteractionDataService;

    @BeforeEach
    void setUp() {
        postInteractionDataService = new PostInteractionDataService(eventPublisher, eventResultStorageService);
    }

    @Test
    @DisplayName("인터랙션 데이터 조회 성공")
    void getInteractionDataSuccessfully() {
        // given
        List<String> postIds = List.of("post1", "post2");
        ContentType contentType = ContentType.POST;
        String requestId = "interaction-request-123";
        
        List<InteractionData> expectedInteractionData = List.of(
            new InteractionData("interaction1", "post1", "user1", "ACTIVE", "LIKE", "POST"),
            new InteractionData("interaction2", "post2", "user2", "ACTIVE", "BOOKMARK", "POST")
        );

        when(eventResultStorageService.generateRequestId("interaction")).thenReturn(requestId);
        when(eventResultStorageService.waitForInteractionData(requestId, Duration.ofSeconds(3)))
            .thenReturn(expectedInteractionData);

        // when
        List<InteractionData> result = postInteractionDataService.getInteractionData(postIds, contentType);

        // then
        assertThat(result).isEqualTo(expectedInteractionData);
        
        ArgumentCaptor<InteractionDataRequestedEvent> eventCaptor = 
            ArgumentCaptor.forClass(InteractionDataRequestedEvent.class);
        verify(eventPublisher).publishDomainEvent(eventCaptor.capture());
        
        InteractionDataRequestedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getRequestId()).isEqualTo(requestId);
        assertThat(capturedEvent.getPostIds()).isEqualTo(postIds);
        assertThat(capturedEvent.getContentType()).isEqualTo(contentType.name());
        assertThat(capturedEvent.getRequestingDomain()).isEqualTo("post");
        
        verify(eventResultStorageService).registerEventProcessing(requestId);
    }

    @Test
    @DisplayName("인터랙션 데이터 비동기 조회 성공")
    void getInteractionDataAsyncSuccessfully() {
        // given
        List<String> postIds = List.of("post1");
        ContentType contentType = ContentType.POST;
        String requestId = "async-interaction-123";
        
        List<InteractionData> expectedData = List.of(
            new InteractionData("interaction1", "post1", "user1", "ACTIVE", "LIKE", "POST")
        );

        when(eventResultStorageService.generateRequestId("interaction")).thenReturn(requestId);
        when(eventResultStorageService.waitForInteractionData(requestId, Duration.ofSeconds(3)))
            .thenReturn(expectedData);

        // when
        CompletableFuture<List<InteractionData>> resultFuture = 
            postInteractionDataService.getInteractionDataAsync(postIds, contentType);

        // then
        assertThat(resultFuture).isNotNull();
        assertThat(resultFuture.join()).isEqualTo(expectedData);
        
        verify(eventPublisher).publishDomainEvent(any(InteractionDataRequestedEvent.class));
    }

    @Test
    @DisplayName("이벤트 타임아웃 시 빈 리스트 반환")
    void getInteractionDataTimeoutReturnsEmptyList() {
        // given
        List<String> postIds = List.of("post1");
        ContentType contentType = ContentType.POST;
        String requestId = "timeout-request-123";

        when(eventResultStorageService.generateRequestId("interaction")).thenReturn(requestId);
        when(eventResultStorageService.waitForInteractionData(requestId, Duration.ofSeconds(3)))
            .thenThrow(new RuntimeException("Event processing timeout"));

        // when
        List<InteractionData> result = postInteractionDataService.getInteractionData(postIds, contentType);

        // then
        assertThat(result).isEmpty();
        
        verify(eventPublisher).publishDomainEvent(any(InteractionDataRequestedEvent.class));
        verify(eventResultStorageService).registerEventProcessing(requestId);
    }

    @Test
    @DisplayName("빈 postIds로 조회 시 빈 리스트 반환")
    void getInteractionDataWithEmptyPostIds() {
        // given
        List<String> emptyPostIds = List.of();
        ContentType contentType = ContentType.POST;

        // when
        List<InteractionData> result = postInteractionDataService.getInteractionData(emptyPostIds, contentType);

        // then
        assertThat(result).isEmpty();
        
        verifyNoInteractions(eventPublisher, eventResultStorageService);
    }

    @Test
    @DisplayName("null postIds로 조회 시 NullPointerException")
    void getInteractionDataWithNullPostIds() {
        // given
        ContentType contentType = ContentType.POST;

        // when & then
        assertThatThrownBy(() -> postInteractionDataService.getInteractionData(null, contentType))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("null ContentType으로 조회 시 NullPointerException")
    void getInteractionDataWithNullContentType() {
        // given
        List<String> postIds = List.of("post1");

        // when & then
        assertThatThrownBy(() -> postInteractionDataService.getInteractionData(postIds, null))
            .isInstanceOf(NullPointerException.class);
    }
}