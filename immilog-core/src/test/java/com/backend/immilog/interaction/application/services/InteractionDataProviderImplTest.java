package com.backend.immilog.interaction.application.services;

import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import com.backend.immilog.shared.domain.model.InteractionData;
import com.backend.immilog.shared.enums.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InteractionDataProviderImplTest {

    private final InteractionUserRepository mockInteractionUserRepository = mock(InteractionUserRepository.class);
    private final InteractionDataProviderImpl interactionDataProvider = new InteractionDataProviderImpl(mockInteractionUserRepository);

    @Nested
    @DisplayName("InteractionDataProvider getInteractionData 메서드 테스트")
    class GetInteractionDataTest {

        @Test
        @DisplayName("활성화된 좋아요 인터랙션이 존재할 때 데이터를 반환한다")
        void returnInteractionDataWhenActiveLikeExists() {
            String contentId = "post123";
            ContentType contentType = ContentType.POST;
            
            InteractionUser likeInteraction = InteractionUser.builder()
                    .id("like123")
                    .userId("user456")
                    .postId(contentId)
                    .contentType(contentType)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    eq(List.of(contentId)),
                    eq(contentType),
                    eq(InteractionStatus.ACTIVE)
            )).thenReturn(List.of(likeInteraction));

            InteractionData result = interactionDataProvider.getInteractionData(contentId, contentType);

            assertThat(result.id()).isEqualTo("like123");
            assertThat(result.postId()).isEqualTo(contentId);
            assertThat(result.userId()).isEqualTo("user456");
            assertThat(result.interactionStatus()).isEqualTo("ACTIVE");
            assertThat(result.interactionType()).isEqualTo("LIKE");
            assertThat(result.contentType()).isEqualTo("POST");

            verify(mockInteractionUserRepository).findByPostIdListAndContentTypeAndInteractionStatus(
                    List.of(contentId), contentType, InteractionStatus.ACTIVE
            );
        }

        @Test
        @DisplayName("좋아요 인터랙션이 존재하지 않을 때 기본 데이터를 반환한다")
        void returnDefaultDataWhenNoLikeInteractionExists() {
            String contentId = "post123";
            ContentType contentType = ContentType.POST;

            when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    eq(List.of(contentId)),
                    eq(contentType),
                    eq(InteractionStatus.ACTIVE)
            )).thenReturn(Collections.emptyList());

            InteractionData result = interactionDataProvider.getInteractionData(contentId, contentType);

            assertThat(result.id()).isNull();
            assertThat(result.postId()).isEqualTo(contentId);
            assertThat(result.userId()).isNull();
            assertThat(result.interactionStatus()).isEqualTo("INACTIVE");
            assertThat(result.interactionType()).isEqualTo("NONE");
            assertThat(result.contentType()).isEqualTo("POST");
        }

        @Test
        @DisplayName("북마크만 존재하고 좋아요가 없을 때 기본 데이터를 반환한다")
        void returnDefaultDataWhenOnlyBookmarkExists() {
            String contentId = "post123";
            ContentType contentType = ContentType.POST;
            
            InteractionUser bookmarkInteraction = InteractionUser.builder()
                    .id("bookmark123")
                    .userId("user456")
                    .postId(contentId)
                    .contentType(contentType)
                    .interactionType(InteractionType.BOOKMARK)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    eq(List.of(contentId)),
                    eq(contentType),
                    eq(InteractionStatus.ACTIVE)
            )).thenReturn(List.of(bookmarkInteraction));

            InteractionData result = interactionDataProvider.getInteractionData(contentId, contentType);

            assertThat(result.id()).isNull();
            assertThat(result.interactionStatus()).isEqualTo("INACTIVE");
            assertThat(result.interactionType()).isEqualTo("NONE");
        }

        @Test
        @DisplayName("좋아요와 북마크가 모두 존재할 때 좋아요 데이터를 반환한다")
        void returnLikeDataWhenBothLikeAndBookmarkExist() {
            String contentId = "post123";
            ContentType contentType = ContentType.POST;
            
            InteractionUser likeInteraction = InteractionUser.builder()
                    .id("like123")
                    .userId("user456")
                    .postId(contentId)
                    .contentType(contentType)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();
                    
            InteractionUser bookmarkInteraction = InteractionUser.builder()
                    .id("bookmark123")
                    .userId("user789")
                    .postId(contentId)
                    .contentType(contentType)
                    .interactionType(InteractionType.BOOKMARK)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    eq(List.of(contentId)),
                    eq(contentType),
                    eq(InteractionStatus.ACTIVE)
            )).thenReturn(List.of(likeInteraction, bookmarkInteraction));

            InteractionData result = interactionDataProvider.getInteractionData(contentId, contentType);

            assertThat(result.id()).isEqualTo("like123");
            assertThat(result.userId()).isEqualTo("user456");
            assertThat(result.interactionType()).isEqualTo("LIKE");
        }

        @Test
        @DisplayName("레포지토리에서 예외 발생 시 기본값을 반환한다")
        void returnDefaultValueWhenRepositoryThrowsException() {
            String contentId = "post123";
            ContentType contentType = ContentType.POST;

            when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    any(), any(), any()
            )).thenThrow(new RuntimeException("Database error"));

            InteractionData result = interactionDataProvider.getInteractionData(contentId, contentType);

            assertThat(result.id()).isNull();
            assertThat(result.postId()).isEqualTo(contentId);
            assertThat(result.userId()).isNull();
            assertThat(result.interactionStatus()).isEqualTo("INACTIVE");
            assertThat(result.interactionType()).isEqualTo("NONE");
            assertThat(result.contentType()).isEqualTo("POST");
        }
    }

    @Nested
    @DisplayName("InteractionDataProvider getInteractionDataBatch 메서드 테스트")
    class GetInteractionDataBatchTest {

        @Test
        @DisplayName("여러 컨텐츠 ID에 대한 인터랙션 데이터를 배치로 조회한다")
        void getBatchInteractionDataForMultipleContentIds() {
            List<String> contentIds = List.of("post1", "post2", "post3");
            ContentType contentType = ContentType.POST;

            InteractionUser likeInteraction1 = InteractionUser.builder()
                    .id("like1")
                    .userId("user1")
                    .postId("post1")
                    .contentType(contentType)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    eq(List.of("post1")), eq(contentType), eq(InteractionStatus.ACTIVE)
            )).thenReturn(List.of(likeInteraction1));

            when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    eq(List.of("post2")), eq(contentType), eq(InteractionStatus.ACTIVE)
            )).thenReturn(Collections.emptyList());

            when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    eq(List.of("post3")), eq(contentType), eq(InteractionStatus.ACTIVE)
            )).thenReturn(Collections.emptyList());

            List<InteractionData> results = interactionDataProvider.getInteractionDataBatch(contentIds, contentType);

            assertThat(results).hasSize(3);
            
            assertThat(results.get(0).id()).isEqualTo("like1");
            assertThat(results.get(0).postId()).isEqualTo("post1");
            assertThat(results.get(0).interactionType()).isEqualTo("LIKE");
            
            assertThat(results.get(1).id()).isNull();
            assertThat(results.get(1).postId()).isEqualTo("post2");
            assertThat(results.get(1).interactionType()).isEqualTo("NONE");
            
            assertThat(results.get(2).id()).isNull();
            assertThat(results.get(2).postId()).isEqualTo("post3");
            assertThat(results.get(2).interactionType()).isEqualTo("NONE");
        }

        @Test
        @DisplayName("빈 컨텐츠 ID 리스트로 배치 조회 시 빈 결과를 반환한다")
        void returnEmptyListForEmptyContentIdsList() {
            List<String> emptyContentIds = Collections.emptyList();
            ContentType contentType = ContentType.POST;

            List<InteractionData> results = interactionDataProvider.getInteractionDataBatch(emptyContentIds, contentType);

            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("단일 컨텐츠 ID로 배치 조회가 가능하다")
        void batchQueryWorksWithSingleContentId() {
            List<String> singleContentId = List.of("post123");
            ContentType contentType = ContentType.COMMENT;

            when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    eq(List.of("post123")), eq(contentType), eq(InteractionStatus.ACTIVE)
            )).thenReturn(Collections.emptyList());

            List<InteractionData> results = interactionDataProvider.getInteractionDataBatch(singleContentId, contentType);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).postId()).isEqualTo("post123");
            assertThat(results.get(0).contentType()).isEqualTo("COMMENT");
        }
    }

    @Nested
    @DisplayName("InteractionDataProvider getUserInteractionData 메서드 테스트")
    class GetUserInteractionDataTest {

        @Test
        @DisplayName("특정 사용자의 활성화된 인터랙션 데이터를 반환한다")
        void returnUserInteractionDataWhenActiveInteractionExists() {
            String userId = "user123";
            String contentId = "post456";
            ContentType contentType = ContentType.POST;
            
            InteractionUser userInteraction = InteractionUser.builder()
                    .id("interaction123")
                    .userId(userId)
                    .postId(contentId)
                    .contentType(contentType)
                    .interactionType(InteractionType.BOOKMARK)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    eq(List.of(contentId)),
                    eq(contentType),
                    eq(InteractionStatus.ACTIVE)
            )).thenReturn(List.of(userInteraction));

            InteractionData result = interactionDataProvider.getUserInteractionData(userId, contentId, contentType);

            assertThat(result.id()).isEqualTo("interaction123");
            assertThat(result.postId()).isEqualTo(contentId);
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.interactionStatus()).isEqualTo("ACTIVE");
            assertThat(result.interactionType()).isEqualTo("BOOKMARK");
            assertThat(result.contentType()).isEqualTo("POST");
        }

        @Test
        @DisplayName("사용자의 인터랙션이 존재하지 않을 때 기본 데이터를 반환한다")
        void returnDefaultDataWhenUserInteractionDoesNotExist() {
            String userId = "user123";
            String contentId = "post456";
            ContentType contentType = ContentType.POST;

            when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    eq(List.of(contentId)),
                    eq(contentType),
                    eq(InteractionStatus.ACTIVE)
            )).thenReturn(Collections.emptyList());

            InteractionData result = interactionDataProvider.getUserInteractionData(userId, contentId, contentType);

            assertThat(result.id()).isNull();
            assertThat(result.postId()).isEqualTo(contentId);
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.interactionStatus()).isEqualTo("INACTIVE");
            assertThat(result.interactionType()).isEqualTo("NONE");
            assertThat(result.contentType()).isEqualTo("POST");
        }

        @Test
        @DisplayName("다른 사용자의 인터랙션만 존재할 때 기본 데이터를 반환한다")
        void returnDefaultDataWhenOnlyOtherUserInteractionExists() {
            String userId = "user123";
            String contentId = "post456";
            ContentType contentType = ContentType.POST;
            
            InteractionUser otherUserInteraction = InteractionUser.builder()
                    .id("interaction789")
                    .userId("otherUser")
                    .postId(contentId)
                    .contentType(contentType)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    eq(List.of(contentId)),
                    eq(contentType),
                    eq(InteractionStatus.ACTIVE)
            )).thenReturn(List.of(otherUserInteraction));

            InteractionData result = interactionDataProvider.getUserInteractionData(userId, contentId, contentType);

            assertThat(result.id()).isNull();
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.interactionStatus()).isEqualTo("INACTIVE");
            assertThat(result.interactionType()).isEqualTo("NONE");
        }

        @Test
        @DisplayName("여러 인터랙션 중 특정 사용자의 것만 반환한다")
        void returnOnlySpecificUserInteractionFromMultiple() {
            String userId = "user123";
            String contentId = "post456";
            ContentType contentType = ContentType.POST;
            
            InteractionUser userInteraction = InteractionUser.builder()
                    .id("userInteraction123")
                    .userId(userId)
                    .postId(contentId)
                    .contentType(contentType)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();
                    
            InteractionUser otherUserInteraction = InteractionUser.builder()
                    .id("otherInteraction456")
                    .userId("otherUser")
                    .postId(contentId)
                    .contentType(contentType)
                    .interactionType(InteractionType.BOOKMARK)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    eq(List.of(contentId)),
                    eq(contentType),
                    eq(InteractionStatus.ACTIVE)
            )).thenReturn(List.of(userInteraction, otherUserInteraction));

            InteractionData result = interactionDataProvider.getUserInteractionData(userId, contentId, contentType);

            assertThat(result.id()).isEqualTo("userInteraction123");
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.interactionType()).isEqualTo("LIKE");
        }

        @Test
        @DisplayName("레포지토리에서 예외 발생 시 기본값을 반환한다")
        void returnDefaultValueWhenRepositoryThrowsExceptionForUser() {
            String userId = "user123";
            String contentId = "post456";
            ContentType contentType = ContentType.POST;

            when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    any(), any(), any()
            )).thenThrow(new RuntimeException("Database error"));

            InteractionData result = interactionDataProvider.getUserInteractionData(userId, contentId, contentType);

            assertThat(result.id()).isNull();
            assertThat(result.postId()).isEqualTo(contentId);
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.interactionStatus()).isEqualTo("INACTIVE");
            assertThat(result.interactionType()).isEqualTo("NONE");
            assertThat(result.contentType()).isEqualTo("POST");
        }
    }

    @Nested
    @DisplayName("InteractionDataProvider 통합 시나리오 테스트")
    class IntegrationScenarioTest {

        @Test
        @DisplayName("댓글 타입 컨텐츠에 대한 인터랙션 데이터 조회")
        void getInteractionDataForCommentContent() {
            String contentId = "comment123";
            ContentType contentType = ContentType.COMMENT;
            
            InteractionUser commentLike = InteractionUser.builder()
                    .id("commentLike123")
                    .userId("user456")
                    .postId(contentId)
                    .contentType(contentType)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    eq(List.of(contentId)),
                    eq(contentType),
                    eq(InteractionStatus.ACTIVE)
            )).thenReturn(List.of(commentLike));

            InteractionData result = interactionDataProvider.getInteractionData(contentId, contentType);

            assertThat(result.contentType()).isEqualTo("COMMENT");
            assertThat(result.interactionType()).isEqualTo("LIKE");
        }

        @Test
        @DisplayName("모든 메서드가 일관된 예외 처리를 수행한다")
        void consistentExceptionHandlingAcrossAllMethods() {
            String contentId = "test123";
            String userId = "user123";
            ContentType contentType = ContentType.POST;

            when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    any(), any(), any()
            )).thenThrow(new RuntimeException("Consistent error"));

            InteractionData generalResult = interactionDataProvider.getInteractionData(contentId, contentType);
            InteractionData userResult = interactionDataProvider.getUserInteractionData(userId, contentId, contentType);
            List<InteractionData> batchResult = interactionDataProvider.getInteractionDataBatch(List.of(contentId), contentType);

            assertThat(generalResult.interactionStatus()).isEqualTo("INACTIVE");
            assertThat(userResult.interactionStatus()).isEqualTo("INACTIVE");
            assertThat(batchResult.get(0).interactionStatus()).isEqualTo("INACTIVE");
        }
    }
}