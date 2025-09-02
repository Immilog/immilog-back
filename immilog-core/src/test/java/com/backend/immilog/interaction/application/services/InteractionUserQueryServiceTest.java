package com.backend.immilog.interaction.application.services;

import com.backend.immilog.interaction.domain.model.InteractionStatus;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class InteractionUserQueryServiceTest {

    private final InteractionUserRepository mockInteractionUserRepository = mock(InteractionUserRepository.class);

    private InteractionUserQueryService interactionUserQueryService;

    @BeforeEach
    void setUp() {
        interactionUserQueryService = new InteractionUserQueryService(mockInteractionUserRepository);
    }

    @Test
    @DisplayName("게시물 ID 목록으로 인터랙션 조회 - 정상 케이스")
    void getInteractionUsersByPostIdListAndActiveSuccessfully() {
        //given
        List<String> postIdList = Arrays.asList("post1", "post2", "post3");
        ContentType contentType = ContentType.POST;
        InteractionStatus interactionStatus = InteractionStatus.ACTIVE;
        List<InteractionUser> expectedInteractions = createTestInteractionList();

        when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(postIdList, contentType, interactionStatus))
                .thenReturn(expectedInteractions);

        //when
        List<InteractionUser> result = interactionUserQueryService.getInteractionUsersByPostIdListAndActive(postIdList, contentType, interactionStatus);

        //then
        assertThat(result).isEqualTo(expectedInteractions);
        assertThat(result).hasSize(3);
        verify(mockInteractionUserRepository).findByPostIdListAndContentTypeAndInteractionStatus(postIdList, contentType, interactionStatus);
    }

    @Test
    @DisplayName("빈 게시물 ID 목록으로 인터랙션 조회")
    void getInteractionUsersWithEmptyPostIdList() {
        //given
        List<String> emptyPostIdList = Collections.emptyList();
        ContentType contentType = ContentType.POST;
        InteractionStatus interactionStatus = InteractionStatus.ACTIVE;
        List<InteractionUser> expectedInteractions = Collections.emptyList();

        when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(emptyPostIdList, contentType, interactionStatus))
                .thenReturn(expectedInteractions);

        //when
        List<InteractionUser> result = interactionUserQueryService.getInteractionUsersByPostIdListAndActive(emptyPostIdList, contentType, interactionStatus);

        //then
        assertThat(result).isEmpty();
        verify(mockInteractionUserRepository).findByPostIdListAndContentTypeAndInteractionStatus(emptyPostIdList, contentType, interactionStatus);
    }

    @Test
    @DisplayName("COMMENT 타입으로 인터랙션 조회")
    void getInteractionUsersForCommentType() {
        //given
        List<String> postIdList = Arrays.asList("comment1", "comment2");
        ContentType contentType = ContentType.COMMENT;
        InteractionStatus interactionStatus = InteractionStatus.ACTIVE;
        List<InteractionUser> expectedInteractions = createCommentInteractionList();

        when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(postIdList, contentType, interactionStatus))
                .thenReturn(expectedInteractions);

        //when
        List<InteractionUser> result = interactionUserQueryService.getInteractionUsersByPostIdListAndActive(postIdList, contentType, interactionStatus);

        //then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).contentType()).isEqualTo(ContentType.COMMENT);
        verify(mockInteractionUserRepository).findByPostIdListAndContentTypeAndInteractionStatus(postIdList, contentType, interactionStatus);
    }

    @Test
    @DisplayName("단일 게시물 ID로 인터랙션 조회")
    void getInteractionUsersForSinglePost() {
        //given
        List<String> singlePostIdList = Collections.singletonList("singlePost");
        ContentType contentType = ContentType.POST;
        InteractionStatus interactionStatus = InteractionStatus.ACTIVE;
        List<InteractionUser> expectedInteractions = Collections.singletonList(createTestInteraction());

        when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(singlePostIdList, contentType, interactionStatus))
                .thenReturn(expectedInteractions);

        //when
        List<InteractionUser> result = interactionUserQueryService.getInteractionUsersByPostIdListAndActive(singlePostIdList, contentType, interactionStatus);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).postId()).isEqualTo("singlePost");
        verify(mockInteractionUserRepository).findByPostIdListAndContentTypeAndInteractionStatus(singlePostIdList, contentType, interactionStatus);
    }

    @Test
    @DisplayName("북마크 인터랙션 조회 - 정상 케이스")
    void getBookmarkInteractionsSuccessfully() {
        //given
        String userId = "userId";
        ContentType contentType = ContentType.POST;
        InteractionStatus interactionStatus = InteractionStatus.ACTIVE;
        List<InteractionUser> expectedBookmarks = createBookmarkInteractionList();

        when(mockInteractionUserRepository.findBookmarksByUserIdAndContentTypeAndInteractionStatus(userId, contentType, interactionStatus))
                .thenReturn(expectedBookmarks);

        //when
        List<InteractionUser> result = interactionUserQueryService.getBookmarkInteractions(userId, contentType, interactionStatus);

        //then
        assertThat(result).isEqualTo(expectedBookmarks);
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(interaction -> interaction.interactionType() == InteractionType.BOOKMARK);
        verify(mockInteractionUserRepository).findBookmarksByUserIdAndContentTypeAndInteractionStatus(userId, contentType, interactionStatus);
    }

    @Test
    @DisplayName("빈 북마크 목록 조회")
    void getEmptyBookmarkInteractions() {
        //given
        String userId = "userWithNoBookmarks";
        ContentType contentType = ContentType.POST;
        InteractionStatus interactionStatus = InteractionStatus.ACTIVE;
        List<InteractionUser> emptyBookmarks = Collections.emptyList();

        when(mockInteractionUserRepository.findBookmarksByUserIdAndContentTypeAndInteractionStatus(userId, contentType, interactionStatus))
                .thenReturn(emptyBookmarks);

        //when
        List<InteractionUser> result = interactionUserQueryService.getBookmarkInteractions(userId, contentType, interactionStatus);

        //then
        assertThat(result).isEmpty();
        verify(mockInteractionUserRepository).findBookmarksByUserIdAndContentTypeAndInteractionStatus(userId, contentType, interactionStatus);
    }

    @Test
    @DisplayName("COMMENT 타입 북마크 조회")
    void getCommentBookmarkInteractions() {
        //given
        String userId = "userId";
        ContentType contentType = ContentType.COMMENT;
        InteractionStatus interactionStatus = InteractionStatus.ACTIVE;
        List<InteractionUser> expectedBookmarks = createCommentBookmarkList();

        when(mockInteractionUserRepository.findBookmarksByUserIdAndContentTypeAndInteractionStatus(userId, contentType, interactionStatus))
                .thenReturn(expectedBookmarks);

        //when
        List<InteractionUser> result = interactionUserQueryService.getBookmarkInteractions(userId, contentType, interactionStatus);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).contentType()).isEqualTo(ContentType.COMMENT);
        assertThat(result.get(0).interactionType()).isEqualTo(InteractionType.BOOKMARK);
        verify(mockInteractionUserRepository).findBookmarksByUserIdAndContentTypeAndInteractionStatus(userId, contentType, interactionStatus);
    }

    @Test
    @DisplayName("null 사용자 ID로 북마크 조회")
    void getBookmarkInteractionsWithNullUserId() {
        //given
        String userId = null;
        ContentType contentType = ContentType.POST;
        InteractionStatus interactionStatus = InteractionStatus.ACTIVE;
        List<InteractionUser> emptyBookmarks = Collections.emptyList();

        when(mockInteractionUserRepository.findBookmarksByUserIdAndContentTypeAndInteractionStatus(userId, contentType, interactionStatus))
                .thenReturn(emptyBookmarks);

        //when
        List<InteractionUser> result = interactionUserQueryService.getBookmarkInteractions(userId, contentType, interactionStatus);

        //then
        assertThat(result).isEmpty();
        verify(mockInteractionUserRepository).findBookmarksByUserIdAndContentTypeAndInteractionStatus(userId, contentType, interactionStatus);
    }

    @Test
    @DisplayName("빈 문자열 사용자 ID로 북마크 조회")
    void getBookmarkInteractionsWithEmptyUserId() {
        //given
        String userId = "";
        ContentType contentType = ContentType.POST;
        InteractionStatus interactionStatus = InteractionStatus.ACTIVE;
        List<InteractionUser> emptyBookmarks = Collections.emptyList();

        when(mockInteractionUserRepository.findBookmarksByUserIdAndContentTypeAndInteractionStatus(userId, contentType, interactionStatus))
                .thenReturn(emptyBookmarks);

        //when
        List<InteractionUser> result = interactionUserQueryService.getBookmarkInteractions(userId, contentType, interactionStatus);

        //then
        assertThat(result).isEmpty();
        verify(mockInteractionUserRepository).findBookmarksByUserIdAndContentTypeAndInteractionStatus(userId, contentType, interactionStatus);
    }

    @Test
    @DisplayName("다양한 PostType으로 조회")
    void getInteractionsWithDifferentPostTypes() {
        //given
        InteractionStatus interactionStatus = InteractionStatus.ACTIVE;
        List<String> postIdList = Arrays.asList("post1", "post2");
        List<InteractionUser> postInteractions = createTestInteractionList();
        List<InteractionUser> commentInteractions = createCommentInteractionList();

        when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(postIdList, ContentType.POST, interactionStatus))
                .thenReturn(postInteractions);
        when(mockInteractionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(postIdList, ContentType.COMMENT, interactionStatus))
                .thenReturn(commentInteractions);

        //when
        List<InteractionUser> postResults = interactionUserQueryService.getInteractionUsersByPostIdListAndActive(postIdList, ContentType.POST, interactionStatus);
        List<InteractionUser> commentResults = interactionUserQueryService.getInteractionUsersByPostIdListAndActive(postIdList, ContentType.COMMENT, interactionStatus);

        //then
        assertThat(postResults).allMatch(interaction -> interaction.contentType() == ContentType.POST);
        assertThat(commentResults).allMatch(interaction -> interaction.contentType() == ContentType.COMMENT);
        verify(mockInteractionUserRepository).findByPostIdListAndContentTypeAndInteractionStatus(postIdList, ContentType.POST, interactionStatus);
        verify(mockInteractionUserRepository).findByPostIdListAndContentTypeAndInteractionStatus(postIdList, ContentType.COMMENT, interactionStatus);
    }

    private List<InteractionUser> createTestInteractionList() {
        return Arrays.asList(
                new InteractionUser("id1", "user1", "post1", ContentType.POST, InteractionType.LIKE, InteractionStatus.ACTIVE, LocalDateTime.now()),
                new InteractionUser("id2", "user2", "post2", ContentType.POST, InteractionType.BOOKMARK, InteractionStatus.ACTIVE, LocalDateTime.now()),
                new InteractionUser("id3", "user3", "post3", ContentType.POST, InteractionType.LIKE, InteractionStatus.ACTIVE, LocalDateTime.now())
        );
    }

    private InteractionUser createTestInteraction() {
        return new InteractionUser(
                "interactionId",
                "userId",
                "singlePost",
                ContentType.POST,
                InteractionType.LIKE,
                InteractionStatus.ACTIVE,
                LocalDateTime.now()
        );
    }

    private List<InteractionUser> createCommentInteractionList() {
        return Arrays.asList(
                new InteractionUser("id1", "user1", "comment1", ContentType.COMMENT, InteractionType.LIKE, InteractionStatus.ACTIVE, LocalDateTime.now()),
                new InteractionUser("id2", "user2", "comment2", ContentType.COMMENT, InteractionType.BOOKMARK, InteractionStatus.ACTIVE, LocalDateTime.now())
        );
    }

    private List<InteractionUser> createBookmarkInteractionList() {
        return Arrays.asList(
                new InteractionUser("id1", "userId", "post1", ContentType.POST, InteractionType.BOOKMARK, InteractionStatus.ACTIVE, LocalDateTime.now()),
                new InteractionUser("id2", "userId", "post2", ContentType.POST, InteractionType.BOOKMARK, InteractionStatus.ACTIVE, LocalDateTime.now())
        );
    }

    private List<InteractionUser> createCommentBookmarkList() {
        return Collections.singletonList(
                new InteractionUser("id1", "userId", "comment1", ContentType.COMMENT, InteractionType.BOOKMARK, InteractionStatus.ACTIVE, LocalDateTime.now())
        );
    }
}