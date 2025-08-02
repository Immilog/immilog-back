package com.backend.immilog.interaction.application.services;

import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import com.backend.immilog.shared.enums.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InteractionUserCommandServiceTest {

    private final InteractionUserRepository mockInteractionUserRepository = mock(InteractionUserRepository.class);

    private InteractionUserCommandService interactionUserCommandService;

    @BeforeEach
    void setUp() {
        interactionUserCommandService = new InteractionUserCommandService(mockInteractionUserRepository);
    }

    @Test
    @DisplayName("인터랙션 생성 - 정상 케이스")
    void createInteractionSuccessfully() {
        //given
        InteractionUser interaction = createTestInteraction();
        InteractionUser savedInteraction = createTestInteractionWithId();
        
        when(mockInteractionUserRepository.save(interaction)).thenReturn(savedInteraction);

        //when
        InteractionUser result = interactionUserCommandService.createInteraction(interaction);

        //then
        assertThat(result).isEqualTo(savedInteraction);
        assertThat(result.id()).isNotNull();
        verify(mockInteractionUserRepository).save(interaction);
    }

    @Test
    @DisplayName("좋아요 인터랙션 생성")
    void createLikeInteraction() {
        //given
        InteractionUser likeInteraction = createTestLikeInteraction();
        InteractionUser savedInteraction = createSavedLikeInteraction();
        
        when(mockInteractionUserRepository.save(likeInteraction)).thenReturn(savedInteraction);

        //when
        InteractionUser result = interactionUserCommandService.createInteraction(likeInteraction);

        //then
        assertThat(result.interactionType()).isEqualTo(InteractionType.LIKE);
        assertThat(result.id()).isNotNull();
        verify(mockInteractionUserRepository).save(likeInteraction);
    }

    @Test
    @DisplayName("북마크 인터랙션 생성")
    void createBookmarkInteraction() {
        //given
        InteractionUser bookmarkInteraction = createTestBookmarkInteraction();
        InteractionUser savedInteraction = createSavedBookmarkInteraction();
        
        when(mockInteractionUserRepository.save(bookmarkInteraction)).thenReturn(savedInteraction);

        //when
        InteractionUser result = interactionUserCommandService.createInteraction(bookmarkInteraction);

        //then
        assertThat(result.interactionType()).isEqualTo(InteractionType.BOOKMARK);
        assertThat(result.id()).isNotNull();
        verify(mockInteractionUserRepository).save(bookmarkInteraction);
    }

    @Test
    @DisplayName("JOB_BOARD 타입 인터랙션 생성")
    void createJobBoardInteraction() {
        //given
        InteractionUser jobBoardInteraction = createTestJobBoardInteraction();
        InteractionUser savedInteraction = createSavedJobBoardInteraction();
        
        when(mockInteractionUserRepository.save(jobBoardInteraction)).thenReturn(savedInteraction);

        //when
        InteractionUser result = interactionUserCommandService.createInteraction(jobBoardInteraction);

        //then
        assertThat(result.contentType()).isEqualTo(ContentType.JOB_BOARD);
        assertThat(result.id()).isNotNull();
        verify(mockInteractionUserRepository).save(jobBoardInteraction);
    }

    @Test
    @DisplayName("null 인터랙션 생성")
    void createNullInteraction() {
        //given
        InteractionUser interaction = null;
        
        when(mockInteractionUserRepository.save(interaction)).thenReturn(null);

        //when
        InteractionUser result = interactionUserCommandService.createInteraction(interaction);

        //then
        assertThat(result).isNull();
        verify(mockInteractionUserRepository).save(interaction);
    }

    @Test
    @DisplayName("인터랙션 삭제 - 정상 케이스")
    void deleteInteractionSuccessfully() {
        //given
        String interactionId = "interactionId";

        //when
        interactionUserCommandService.deleteInteraction(interactionId);

        //then
        verify(mockInteractionUserRepository).deleteById(interactionId);
    }

    @Test
    @DisplayName("null ID로 인터랙션 삭제")
    void deleteInteractionWithNullId() {
        //given
        String interactionId = null;

        //when
        interactionUserCommandService.deleteInteraction(interactionId);

        //then
        verify(mockInteractionUserRepository).deleteById(interactionId);
    }

    @Test
    @DisplayName("빈 문자열 ID로 인터랙션 삭제")
    void deleteInteractionWithEmptyId() {
        //given
        String interactionId = "";

        //when
        interactionUserCommandService.deleteInteraction(interactionId);

        //then
        verify(mockInteractionUserRepository).deleteById(interactionId);
    }

    @Test
    @DisplayName("여러 인터랙션 연속 생성")
    void createMultipleInteractionsSequentially() {
        //given
        InteractionUser interaction1 = createTestInteraction();
        InteractionUser interaction2 = createTestLikeInteraction();
        InteractionUser savedInteraction1 = createTestInteractionWithId();
        InteractionUser savedInteraction2 = createSavedLikeInteraction();
        
        when(mockInteractionUserRepository.save(interaction1)).thenReturn(savedInteraction1);
        when(mockInteractionUserRepository.save(interaction2)).thenReturn(savedInteraction2);

        //when
        InteractionUser result1 = interactionUserCommandService.createInteraction(interaction1);
        InteractionUser result2 = interactionUserCommandService.createInteraction(interaction2);

        //then
        assertThat(result1).isEqualTo(savedInteraction1);
        assertThat(result2).isEqualTo(savedInteraction2);
        verify(mockInteractionUserRepository).save(interaction1);
        verify(mockInteractionUserRepository).save(interaction2);
    }

    @Test
    @DisplayName("여러 인터랙션 연속 삭제")
    void deleteMultipleInteractionsSequentially() {
        //given
        String interactionId1 = "interactionId1";
        String interactionId2 = "interactionId2";
        String interactionId3 = "interactionId3";

        //when
        interactionUserCommandService.deleteInteraction(interactionId1);
        interactionUserCommandService.deleteInteraction(interactionId2);
        interactionUserCommandService.deleteInteraction(interactionId3);

        //then
        verify(mockInteractionUserRepository).deleteById(interactionId1);
        verify(mockInteractionUserRepository).deleteById(interactionId2);
        verify(mockInteractionUserRepository).deleteById(interactionId3);
    }

    @Test
    @DisplayName("생성과 삭제 혼합 작업")
    void mixedCreateAndDeleteOperations() {
        //given
        InteractionUser interactionToCreate = createTestInteraction();
        InteractionUser savedInteraction = createTestInteractionWithId();
        String interactionIdToDelete = "deleteInteractionId";
        
        when(mockInteractionUserRepository.save(interactionToCreate)).thenReturn(savedInteraction);

        //when
        InteractionUser createResult = interactionUserCommandService.createInteraction(interactionToCreate);
        interactionUserCommandService.deleteInteraction(interactionIdToDelete);

        //then
        assertThat(createResult).isEqualTo(savedInteraction);
        verify(mockInteractionUserRepository).save(interactionToCreate);
        verify(mockInteractionUserRepository).deleteById(interactionIdToDelete);
    }

    @Test
    @DisplayName("다양한 PostType으로 인터랙션 생성")
    void createInteractionsWithDifferentPostTypes() {
        //given
        InteractionUser postInteraction = createTestInteraction();
        InteractionUser jobBoardInteraction = createTestJobBoardInteraction();
        InteractionUser savedPostInteraction = createTestInteractionWithId();
        InteractionUser savedJobBoardInteraction = createSavedJobBoardInteraction();
        
        when(mockInteractionUserRepository.save(postInteraction)).thenReturn(savedPostInteraction);
        when(mockInteractionUserRepository.save(jobBoardInteraction)).thenReturn(savedJobBoardInteraction);

        //when
        InteractionUser postResult = interactionUserCommandService.createInteraction(postInteraction);
        InteractionUser jobBoardResult = interactionUserCommandService.createInteraction(jobBoardInteraction);

        //then
        assertThat(postResult.contentType()).isEqualTo(ContentType.POST);
        assertThat(jobBoardResult.contentType()).isEqualTo(ContentType.JOB_BOARD);
        verify(mockInteractionUserRepository).save(postInteraction);
        verify(mockInteractionUserRepository).save(jobBoardInteraction);
    }

    @Test
    @DisplayName("모든 InteractionType으로 인터랙션 생성")
    void createInteractionsWithAllTypes() {
        //given
        InteractionUser likeInteraction = createTestLikeInteraction();
        InteractionUser bookmarkInteraction = createTestBookmarkInteraction();
        InteractionUser savedLikeInteraction = createSavedLikeInteraction();
        InteractionUser savedBookmarkInteraction = createSavedBookmarkInteraction();
        
        when(mockInteractionUserRepository.save(any(InteractionUser.class)))
                .thenReturn(savedLikeInteraction)
                .thenReturn(savedBookmarkInteraction);

        //when
        InteractionUser likeResult = interactionUserCommandService.createInteraction(likeInteraction);
        InteractionUser bookmarkResult = interactionUserCommandService.createInteraction(bookmarkInteraction);

        //then
        assertThat(likeResult.interactionType()).isEqualTo(InteractionType.LIKE);
        assertThat(bookmarkResult.interactionType()).isEqualTo(InteractionType.BOOKMARK);
        verify(mockInteractionUserRepository, org.mockito.Mockito.times(2)).save(any(InteractionUser.class));
    }

    private InteractionUser createTestInteraction() {
        return InteractionUser.of(
                "userId",
                "postId",
                ContentType.POST,
                InteractionType.LIKE
        );
    }

    private InteractionUser createTestInteractionWithId() {
        return new InteractionUser(
                "interactionId",
                "userId",
                "postId",
                ContentType.POST,
                InteractionType.LIKE,
                LocalDateTime.now()
        );
    }

    private InteractionUser createTestLikeInteraction() {
        return InteractionUser.of(
                "userId",
                "postId",
                ContentType.POST,
                InteractionType.LIKE
        );
    }

    private InteractionUser createSavedLikeInteraction() {
        return new InteractionUser(
                "likeInteractionId",
                "userId",
                "postId",
                ContentType.POST,
                InteractionType.LIKE,
                LocalDateTime.now()
        );
    }

    private InteractionUser createTestBookmarkInteraction() {
        return InteractionUser.of(
                "userId",
                "postId",
                ContentType.POST,
                InteractionType.BOOKMARK
        );
    }

    private InteractionUser createSavedBookmarkInteraction() {
        return new InteractionUser(
                "bookmarkInteractionId",
                "userId",
                "postId",
                ContentType.POST,
                InteractionType.BOOKMARK,
                LocalDateTime.now()
        );
    }

    private InteractionUser createTestJobBoardInteraction() {
        return InteractionUser.of(
                "userId",
                "jobBoardId",
                ContentType.JOB_BOARD,
                InteractionType.LIKE
        );
    }

    private InteractionUser createSavedJobBoardInteraction() {
        return new InteractionUser(
                "jobBoardInteractionId",
                "userId",
                "jobBoardId",
                ContentType.JOB_BOARD,
                InteractionType.LIKE,
                LocalDateTime.now()
        );
    }
}