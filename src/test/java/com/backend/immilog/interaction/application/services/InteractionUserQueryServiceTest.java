package com.backend.immilog.interaction.application.services;

import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import com.backend.immilog.shared.enums.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InteractionUserQueryServiceTest {

    private final InteractionUserRepository mockInteractionUserRepository = mock(InteractionUserRepository.class);

    private InteractionUserQueryService interactionUserQueryService;

    @BeforeEach
    void setUp() {
        interactionUserQueryService = new InteractionUserQueryService(mockInteractionUserRepository);
    }

    @Test
    @DisplayName("게시물 ID 목록으로 인터랙션 조회 - 정상 케이스")
    void getInteractionUsersByPostIdListSuccessfully() {
        //given
        List<String> postIdList = Arrays.asList("post1", "post2", "post3");
        ContentType contentType = ContentType.POST;
        List<InteractionUser> expectedInteractions = createTestInteractionList();
        
        when(mockInteractionUserRepository.findByPostIdListAndContentType(postIdList, contentType))
                .thenReturn(expectedInteractions);

        //when
        List<InteractionUser> result = interactionUserQueryService.getInteractionUsersByPostIdList(postIdList, contentType);

        //then
        assertThat(result).isEqualTo(expectedInteractions);
        assertThat(result).hasSize(3);
        verify(mockInteractionUserRepository).findByPostIdListAndContentType(postIdList, contentType);
    }

    @Test
    @DisplayName("빈 게시물 ID 목록으로 인터랙션 조회")
    void getInteractionUsersWithEmptyPostIdList() {
        //given
        List<String> emptyPostIdList = Collections.emptyList();
        ContentType contentType = ContentType.POST;
        List<InteractionUser> expectedInteractions = Collections.emptyList();
        
        when(mockInteractionUserRepository.findByPostIdListAndContentType(emptyPostIdList, contentType))
                .thenReturn(expectedInteractions);

        //when
        List<InteractionUser> result = interactionUserQueryService.getInteractionUsersByPostIdList(emptyPostIdList, contentType);

        //then
        assertThat(result).isEmpty();
        verify(mockInteractionUserRepository).findByPostIdListAndContentType(emptyPostIdList, contentType);
    }

    @Test
    @DisplayName("JOB_BOARD 타입으로 인터랙션 조회")
    void getInteractionUsersForJobBoardType() {
        //given
        List<String> postIdList = Arrays.asList("jobBoard1", "jobBoard2");
        ContentType contentType = ContentType.JOB_BOARD;
        List<InteractionUser> expectedInteractions = createJobBoardInteractionList();
        
        when(mockInteractionUserRepository.findByPostIdListAndContentType(postIdList, contentType))
                .thenReturn(expectedInteractions);

        //when
        List<InteractionUser> result = interactionUserQueryService.getInteractionUsersByPostIdList(postIdList, contentType);

        //then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).contentType()).isEqualTo(ContentType.JOB_BOARD);
        verify(mockInteractionUserRepository).findByPostIdListAndContentType(postIdList, contentType);
    }

    @Test
    @DisplayName("단일 게시물 ID로 인터랙션 조회")
    void getInteractionUsersForSinglePost() {
        //given
        List<String> singlePostIdList = Collections.singletonList("singlePost");
        ContentType contentType = ContentType.POST;
        List<InteractionUser> expectedInteractions = Collections.singletonList(createTestInteraction());
        
        when(mockInteractionUserRepository.findByPostIdListAndContentType(singlePostIdList, contentType))
                .thenReturn(expectedInteractions);

        //when
        List<InteractionUser> result = interactionUserQueryService.getInteractionUsersByPostIdList(singlePostIdList, contentType);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).postId()).isEqualTo("singlePost");
        verify(mockInteractionUserRepository).findByPostIdListAndContentType(singlePostIdList, contentType);
    }

    @Test
    @DisplayName("북마크 인터랙션 조회 - 정상 케이스")
    void getBookmarkInteractionsSuccessfully() {
        //given
        String userId = "userId";
        ContentType contentType = ContentType.POST;
        List<InteractionUser> expectedBookmarks = createBookmarkInteractionList();
        
        when(mockInteractionUserRepository.findBookmarksByUserIdAndContentType(userId, contentType))
                .thenReturn(expectedBookmarks);

        //when
        List<InteractionUser> result = interactionUserQueryService.getBookmarkInteractions(userId, contentType);

        //then
        assertThat(result).isEqualTo(expectedBookmarks);
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(interaction -> interaction.interactionType() == InteractionType.BOOKMARK);
        verify(mockInteractionUserRepository).findBookmarksByUserIdAndContentType(userId, contentType);
    }

    @Test
    @DisplayName("빈 북마크 목록 조회")
    void getEmptyBookmarkInteractions() {
        //given
        String userId = "userWithNoBookmarks";
        ContentType contentType = ContentType.POST;
        List<InteractionUser> emptyBookmarks = Collections.emptyList();
        
        when(mockInteractionUserRepository.findBookmarksByUserIdAndContentType(userId, contentType))
                .thenReturn(emptyBookmarks);

        //when
        List<InteractionUser> result = interactionUserQueryService.getBookmarkInteractions(userId, contentType);

        //then
        assertThat(result).isEmpty();
        verify(mockInteractionUserRepository).findBookmarksByUserIdAndContentType(userId, contentType);
    }

    @Test
    @DisplayName("JOB_BOARD 타입 북마크 조회")
    void getJobBoardBookmarkInteractions() {
        //given
        String userId = "userId";
        ContentType contentType = ContentType.JOB_BOARD;
        List<InteractionUser> expectedBookmarks = createJobBoardBookmarkList();
        
        when(mockInteractionUserRepository.findBookmarksByUserIdAndContentType(userId, contentType))
                .thenReturn(expectedBookmarks);

        //when
        List<InteractionUser> result = interactionUserQueryService.getBookmarkInteractions(userId, contentType);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).contentType()).isEqualTo(ContentType.JOB_BOARD);
        assertThat(result.get(0).interactionType()).isEqualTo(InteractionType.BOOKMARK);
        verify(mockInteractionUserRepository).findBookmarksByUserIdAndContentType(userId, contentType);
    }

    @Test
    @DisplayName("null 사용자 ID로 북마크 조회")
    void getBookmarkInteractionsWithNullUserId() {
        //given
        String userId = null;
        ContentType contentType = ContentType.POST;
        List<InteractionUser> emptyBookmarks = Collections.emptyList();
        
        when(mockInteractionUserRepository.findBookmarksByUserIdAndContentType(userId, contentType))
                .thenReturn(emptyBookmarks);

        //when
        List<InteractionUser> result = interactionUserQueryService.getBookmarkInteractions(userId, contentType);

        //then
        assertThat(result).isEmpty();
        verify(mockInteractionUserRepository).findBookmarksByUserIdAndContentType(userId, contentType);
    }

    @Test
    @DisplayName("빈 문자열 사용자 ID로 북마크 조회")
    void getBookmarkInteractionsWithEmptyUserId() {
        //given
        String userId = "";
        ContentType contentType = ContentType.POST;
        List<InteractionUser> emptyBookmarks = Collections.emptyList();
        
        when(mockInteractionUserRepository.findBookmarksByUserIdAndContentType(userId, contentType))
                .thenReturn(emptyBookmarks);

        //when
        List<InteractionUser> result = interactionUserQueryService.getBookmarkInteractions(userId, contentType);

        //then
        assertThat(result).isEmpty();
        verify(mockInteractionUserRepository).findBookmarksByUserIdAndContentType(userId, contentType);
    }

    @Test
    @DisplayName("다양한 PostType으로 조회")
    void getInteractionsWithDifferentPostTypes() {
        //given
        List<String> postIdList = Arrays.asList("post1", "post2");
        List<InteractionUser> postInteractions = createTestInteractionList();
        List<InteractionUser> jobBoardInteractions = createJobBoardInteractionList();
        
        when(mockInteractionUserRepository.findByPostIdListAndContentType(postIdList, ContentType.POST))
                .thenReturn(postInteractions);
        when(mockInteractionUserRepository.findByPostIdListAndContentType(postIdList, ContentType.JOB_BOARD))
                .thenReturn(jobBoardInteractions);

        //when
        List<InteractionUser> postResults = interactionUserQueryService.getInteractionUsersByPostIdList(postIdList, ContentType.POST);
        List<InteractionUser> jobBoardResults = interactionUserQueryService.getInteractionUsersByPostIdList(postIdList, ContentType.JOB_BOARD);

        //then
        assertThat(postResults).allMatch(interaction -> interaction.contentType() == ContentType.POST);
        assertThat(jobBoardResults).allMatch(interaction -> interaction.contentType() == ContentType.JOB_BOARD);
        verify(mockInteractionUserRepository).findByPostIdListAndContentType(postIdList, ContentType.POST);
        verify(mockInteractionUserRepository).findByPostIdListAndContentType(postIdList, ContentType.JOB_BOARD);
    }

    private List<InteractionUser> createTestInteractionList() {
        return Arrays.asList(
                new InteractionUser("id1", "user1", "post1", ContentType.POST, InteractionType.LIKE, LocalDateTime.now()),
                new InteractionUser("id2", "user2", "post2", ContentType.POST, InteractionType.BOOKMARK, LocalDateTime.now()),
                new InteractionUser("id3", "user3", "post3", ContentType.POST, InteractionType.LIKE, LocalDateTime.now())
        );
    }

    private InteractionUser createTestInteraction() {
        return new InteractionUser(
                "interactionId",
                "userId",
                "singlePost",
                ContentType.POST,
                InteractionType.LIKE,
                LocalDateTime.now()
        );
    }

    private List<InteractionUser> createJobBoardInteractionList() {
        return Arrays.asList(
                new InteractionUser("id1", "user1", "jobBoard1", ContentType.JOB_BOARD, InteractionType.LIKE, LocalDateTime.now()),
                new InteractionUser("id2", "user2", "jobBoard2", ContentType.JOB_BOARD, InteractionType.BOOKMARK, LocalDateTime.now())
        );
    }

    private List<InteractionUser> createBookmarkInteractionList() {
        return Arrays.asList(
                new InteractionUser("id1", "userId", "post1", ContentType.POST, InteractionType.BOOKMARK, LocalDateTime.now()),
                new InteractionUser("id2", "userId", "post2", ContentType.POST, InteractionType.BOOKMARK, LocalDateTime.now())
        );
    }

    private List<InteractionUser> createJobBoardBookmarkList() {
        return Collections.singletonList(
                new InteractionUser("id1", "userId", "jobBoard1", ContentType.JOB_BOARD, InteractionType.BOOKMARK, LocalDateTime.now())
        );
    }
}