package com.backend.immilog.interaction.application.usecase;

import com.backend.immilog.interaction.application.command.InteractionCreateCommand;
import com.backend.immilog.interaction.application.result.InteractionResult;
import com.backend.immilog.interaction.application.services.InteractionUserCommandService;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.model.InteractionUser;
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

class InteractionCreateUseCaseTest {

    private final InteractionUserCommandService mockInteractionUserCommandService = mock(InteractionUserCommandService.class);

    private InteractionCreateUseCase.InteractionCreator interactionCreator;

    @BeforeEach
    void setUp() {
        interactionCreator = new InteractionCreateUseCase.InteractionCreator(mockInteractionUserCommandService);
    }

    @Test
    @DisplayName("인터랙션 생성 - 정상 케이스")
    void createInteractionSuccessfully() {
        //given
        InteractionCreateCommand command = new InteractionCreateCommand(
                "userId",
                "postId",
                ContentType.POST,
                InteractionType.LIKE
        );
        InteractionUser savedInteraction = createTestInteractionWithId();
        
        when(mockInteractionUserCommandService.createInteraction(any(InteractionUser.class)))
                .thenReturn(savedInteraction);

        //when
        InteractionResult result = interactionCreator.createInteraction(command);

        //then
        assertThat(result.id()).isEqualTo(savedInteraction.id());
        assertThat(result.userId()).isEqualTo(command.userId());
        assertThat(result.postId()).isEqualTo(command.postId());
        assertThat(result.contentType()).isEqualTo(command.contentType());
        assertThat(result.interactionType()).isEqualTo(command.interactionType());
        verify(mockInteractionUserCommandService).createInteraction(any(InteractionUser.class));
    }

    @Test
    @DisplayName("좋아요 인터랙션 생성")
    void createLikeInteraction() {
        //given
        InteractionCreateCommand command = new InteractionCreateCommand(
                "userId",
                "postId",
                ContentType.POST,
                InteractionType.LIKE
        );
        InteractionUser savedInteraction = createLikeInteractionWithId();
        
        when(mockInteractionUserCommandService.createInteraction(any(InteractionUser.class)))
                .thenReturn(savedInteraction);

        //when
        InteractionResult result = interactionCreator.createInteraction(command);

        //then
        assertThat(result.interactionType()).isEqualTo(InteractionType.LIKE);
        assertThat(result.userId()).isEqualTo(command.userId());
        assertThat(result.postId()).isEqualTo(command.postId());
        verify(mockInteractionUserCommandService).createInteraction(any(InteractionUser.class));
    }

    @Test
    @DisplayName("북마크 인터랙션 생성")
    void createBookmarkInteraction() {
        //given
        InteractionCreateCommand command = new InteractionCreateCommand(
                "userId",
                "postId",
                ContentType.POST,
                InteractionType.BOOKMARK
        );
        InteractionUser savedInteraction = createBookmarkInteractionWithId();
        
        when(mockInteractionUserCommandService.createInteraction(any(InteractionUser.class)))
                .thenReturn(savedInteraction);

        //when
        InteractionResult result = interactionCreator.createInteraction(command);

        //then
        assertThat(result.interactionType()).isEqualTo(InteractionType.BOOKMARK);
        assertThat(result.userId()).isEqualTo(command.userId());
        assertThat(result.postId()).isEqualTo(command.postId());
        verify(mockInteractionUserCommandService).createInteraction(any(InteractionUser.class));
    }

    @Test
    @DisplayName("JOB_BOARD 타입 인터랙션 생성")
    void createJobBoardInteraction() {
        //given
        InteractionCreateCommand command = new InteractionCreateCommand(
                "userId",
                "jobBoardId",
                ContentType.JOB_BOARD,
                InteractionType.LIKE
        );
        InteractionUser savedInteraction = createJobBoardInteractionWithId();
        
        when(mockInteractionUserCommandService.createInteraction(any(InteractionUser.class)))
                .thenReturn(savedInteraction);

        //when
        InteractionResult result = interactionCreator.createInteraction(command);

        //then
        assertThat(result.contentType()).isEqualTo(ContentType.JOB_BOARD);
        assertThat(result.interactionType()).isEqualTo(InteractionType.LIKE);
        assertThat(result.postId()).isEqualTo("jobBoardId");
        verify(mockInteractionUserCommandService).createInteraction(any(InteractionUser.class));
    }

    @Test
    @DisplayName("POST 타입 인터랙션 생성")
    void createPostInteraction() {
        //given
        InteractionCreateCommand command = new InteractionCreateCommand(
                "userId",
                "postId",
                ContentType.POST,
                InteractionType.BOOKMARK
        );
        InteractionUser savedInteraction = new InteractionUser(
                "interactionId",
                "userId",
                "postId",
                ContentType.POST,
                InteractionType.BOOKMARK,
                LocalDateTime.now()
        );
        
        when(mockInteractionUserCommandService.createInteraction(any(InteractionUser.class)))
                .thenReturn(savedInteraction);

        //when
        InteractionResult result = interactionCreator.createInteraction(command);

        //then
        assertThat(result.contentType()).isEqualTo(ContentType.POST);
        assertThat(result.interactionType()).isEqualTo(InteractionType.BOOKMARK);
        verify(mockInteractionUserCommandService).createInteraction(any(InteractionUser.class));
    }

    @Test
    @DisplayName("null 사용자 ID로 인터랙션 생성")
    void createInteractionWithNullUserId() {
        //given
        InteractionCreateCommand command = new InteractionCreateCommand(
                null,
                "postId",
                ContentType.POST,
                InteractionType.LIKE
        );
        InteractionUser savedInteraction = createTestInteractionWithNullUserId();
        
        when(mockInteractionUserCommandService.createInteraction(any(InteractionUser.class)))
                .thenReturn(savedInteraction);

        //when
        InteractionResult result = interactionCreator.createInteraction(command);

        //then
        assertThat(result.userId()).isNull();
        assertThat(result.postId()).isEqualTo(command.postId());
        assertThat(result.interactionType()).isEqualTo(command.interactionType());
        verify(mockInteractionUserCommandService).createInteraction(any(InteractionUser.class));
    }

    @Test
    @DisplayName("null 게시물 ID로 인터랙션 생성")
    void createInteractionWithNullPostId() {
        //given
        InteractionCreateCommand command = new InteractionCreateCommand(
                "userId",
                null,
                ContentType.POST,
                InteractionType.LIKE
        );
        InteractionUser savedInteraction = createTestInteractionWithNullPostId();
        
        when(mockInteractionUserCommandService.createInteraction(any(InteractionUser.class)))
                .thenReturn(savedInteraction);

        //when
        InteractionResult result = interactionCreator.createInteraction(command);

        //then
        assertThat(result.userId()).isEqualTo(command.userId());
        assertThat(result.postId()).isNull();
        assertThat(result.interactionType()).isEqualTo(command.interactionType());
        verify(mockInteractionUserCommandService).createInteraction(any(InteractionUser.class));
    }

    @Test
    @DisplayName("빈 문자열 사용자 ID로 인터랙션 생성")
    void createInteractionWithEmptyUserId() {
        //given
        InteractionCreateCommand command = new InteractionCreateCommand(
                "",
                "postId",
                ContentType.POST,
                InteractionType.BOOKMARK
        );
        InteractionUser savedInteraction = createTestInteractionWithEmptyUserId();
        
        when(mockInteractionUserCommandService.createInteraction(any(InteractionUser.class)))
                .thenReturn(savedInteraction);

        //when
        InteractionResult result = interactionCreator.createInteraction(command);

        //then
        assertThat(result.userId()).isEmpty();
        assertThat(result.postId()).isEqualTo(command.postId());
        assertThat(result.interactionType()).isEqualTo(InteractionType.BOOKMARK);
        verify(mockInteractionUserCommandService).createInteraction(any(InteractionUser.class));
    }

    @Test
    @DisplayName("빈 문자열 게시물 ID로 인터랙션 생성")
    void createInteractionWithEmptyPostId() {
        //given
        InteractionCreateCommand command = new InteractionCreateCommand(
                "userId",
                "",
                ContentType.JOB_BOARD,
                InteractionType.LIKE
        );
        InteractionUser savedInteraction = createTestInteractionWithEmptyPostId();
        
        when(mockInteractionUserCommandService.createInteraction(any(InteractionUser.class)))
                .thenReturn(savedInteraction);

        //when
        InteractionResult result = interactionCreator.createInteraction(command);

        //then
        assertThat(result.userId()).isEqualTo(command.userId());
        assertThat(result.postId()).isEmpty();
        assertThat(result.contentType()).isEqualTo(ContentType.JOB_BOARD);
        verify(mockInteractionUserCommandService).createInteraction(any(InteractionUser.class));
    }

    @Test
    @DisplayName("다양한 사용자와 게시물로 인터랙션 생성")
    void createInteractionsWithDifferentUsersAndPosts() {
        //given
        String[] userIds = {"user1", "user2", "user3"};
        String[] postIds = {"post1", "post2", "post3"};
        InteractionUser savedInteraction = createTestInteractionWithId();
        
        when(mockInteractionUserCommandService.createInteraction(any(InteractionUser.class)))
                .thenReturn(savedInteraction);

        //when & then
        for (int i = 0; i < userIds.length; i++) {
            InteractionCreateCommand command = new InteractionCreateCommand(
                    userIds[i],
                    postIds[i],
                    ContentType.POST,
                    InteractionType.LIKE
            );
            
            InteractionResult result = interactionCreator.createInteraction(command);
            
            assertThat(result.userId()).isEqualTo(savedInteraction.userId());
            assertThat(result.postId()).isEqualTo(savedInteraction.postId());
        }
        
        verify(mockInteractionUserCommandService, org.mockito.Mockito.times(3))
                .createInteraction(any(InteractionUser.class));
    }

    @Test
    @DisplayName("모든 InteractionType으로 인터랙션 생성")
    void createInteractionsWithAllTypes() {
        //given
        InteractionUser likeInteraction = createLikeInteractionWithId();
        InteractionUser bookmarkInteraction = createBookmarkInteractionWithId();
        
        when(mockInteractionUserCommandService.createInteraction(any(InteractionUser.class)))
                .thenReturn(likeInteraction)
                .thenReturn(bookmarkInteraction);

        //when & then
        for (InteractionType type : InteractionType.values()) {
            InteractionCreateCommand command = new InteractionCreateCommand(
                    "userId",
                    "postId",
                    ContentType.POST,
                    type
            );
            
            InteractionResult result = interactionCreator.createInteraction(command);
            
            assertThat(result.interactionType()).isIn(InteractionType.LIKE, InteractionType.BOOKMARK);
        }
        
        verify(mockInteractionUserCommandService, org.mockito.Mockito.times(2))
                .createInteraction(any(InteractionUser.class));
    }

    @Test
    @DisplayName("모든 PostType으로 인터랙션 생성")
    void createInteractionsWithAllPostTypes() {
        //given
        InteractionUser postInteraction = createTestInteractionWithId();
        InteractionUser jobBoardInteraction = createJobBoardInteractionWithId();
        
        when(mockInteractionUserCommandService.createInteraction(any(InteractionUser.class)))
                .thenReturn(postInteraction)
                .thenReturn(jobBoardInteraction)
                .thenReturn(postInteraction);

        //when & then
        for (ContentType type : ContentType.values()) {
            InteractionCreateCommand command = new InteractionCreateCommand(
                    "userId",
                    "postId",
                    type,
                    InteractionType.LIKE
            );
            
            InteractionResult result = interactionCreator.createInteraction(command);
            
            assertThat(result.contentType()).isIn(ContentType.POST, ContentType.JOB_BOARD);
        }
        
        verify(mockInteractionUserCommandService, org.mockito.Mockito.times(ContentType.values().length))
                .createInteraction(any(InteractionUser.class));
    }

    @Test
    @DisplayName("서비스 호출 검증")
    void verifyServiceCall() {
        //given
        InteractionCreateCommand command = new InteractionCreateCommand(
                "userId",
                "postId",
                ContentType.POST,
                InteractionType.LIKE
        );
        InteractionUser savedInteraction = createTestInteractionWithId();
        
        when(mockInteractionUserCommandService.createInteraction(any(InteractionUser.class)))
                .thenReturn(savedInteraction);

        //when
        interactionCreator.createInteraction(command);

        //then
        verify(mockInteractionUserCommandService).createInteraction(any(InteractionUser.class));
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

    private InteractionUser createLikeInteractionWithId() {
        return new InteractionUser(
                "likeInteractionId",
                "userId",
                "postId",
                ContentType.POST,
                InteractionType.LIKE,
                LocalDateTime.now()
        );
    }

    private InteractionUser createBookmarkInteractionWithId() {
        return new InteractionUser(
                "bookmarkInteractionId",
                "userId",
                "postId",
                ContentType.POST,
                InteractionType.BOOKMARK,
                LocalDateTime.now()
        );
    }

    private InteractionUser createJobBoardInteractionWithId() {
        return new InteractionUser(
                "jobBoardInteractionId",
                "userId",
                "jobBoardId",
                ContentType.JOB_BOARD,
                InteractionType.LIKE,
                LocalDateTime.now()
        );
    }

    private InteractionUser createTestInteractionWithNullUserId() {
        return new InteractionUser(
                "interactionId",
                null,
                "postId",
                ContentType.POST,
                InteractionType.LIKE,
                LocalDateTime.now()
        );
    }

    private InteractionUser createTestInteractionWithNullPostId() {
        return new InteractionUser(
                "interactionId",
                "userId",
                null,
                ContentType.POST,
                InteractionType.LIKE,
                LocalDateTime.now()
        );
    }

    private InteractionUser createTestInteractionWithEmptyUserId() {
        return new InteractionUser(
                "interactionId",
                "",
                "postId",
                ContentType.POST,
                InteractionType.BOOKMARK,
                LocalDateTime.now()
        );
    }

    private InteractionUser createTestInteractionWithEmptyPostId() {
        return new InteractionUser(
                "interactionId",
                "userId",
                "",
                ContentType.JOB_BOARD,
                InteractionType.LIKE,
                LocalDateTime.now()
        );
    }
}