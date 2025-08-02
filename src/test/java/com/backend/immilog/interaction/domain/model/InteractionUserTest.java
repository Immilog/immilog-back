package com.backend.immilog.interaction.domain.model;

import com.backend.immilog.post.domain.model.post.PostType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class InteractionUserTest {

    @Test
    @DisplayName("인터랙션 생성 - 정상 케이스")
    void createInteractionSuccessfully() {
        //given
        String userId = "userId";
        String postId = "postId";
        PostType postType = PostType.POST;
        InteractionType interactionType = InteractionType.LIKE;

        //when
        InteractionUser interaction = InteractionUser.of(userId, postId, postType, interactionType);

        //then
        assertThat(interaction.userId()).isEqualTo(userId);
        assertThat(interaction.postId()).isEqualTo(postId);
        assertThat(interaction.postType()).isEqualTo(postType);
        assertThat(interaction.interactionType()).isEqualTo(interactionType);
        assertThat(interaction.id()).isNull();
        assertThat(interaction.createdAt()).isNotNull();
    }

    @Test
    @DisplayName("좋아요 인터랙션 생성")
    void createLikeInteraction() {
        //given
        String userId = "userId";
        String postId = "postId";
        PostType postType = PostType.POST;
        InteractionType interactionType = InteractionType.LIKE;

        //when
        InteractionUser interaction = InteractionUser.of(userId, postId, postType, interactionType);

        //then
        assertThat(interaction.interactionType()).isEqualTo(InteractionType.LIKE);
        assertThat(interaction.userId()).isEqualTo(userId);
        assertThat(interaction.postId()).isEqualTo(postId);
    }

    @Test
    @DisplayName("북마크 인터랙션 생성")
    void createBookmarkInteraction() {
        //given
        String userId = "userId";
        String postId = "postId";
        PostType postType = PostType.POST;
        InteractionType interactionType = InteractionType.BOOKMARK;

        //when
        InteractionUser interaction = InteractionUser.of(userId, postId, postType, interactionType);

        //then
        assertThat(interaction.interactionType()).isEqualTo(InteractionType.BOOKMARK);
        assertThat(interaction.userId()).isEqualTo(userId);
        assertThat(interaction.postId()).isEqualTo(postId);
    }

    @Test
    @DisplayName("JOB_BOARD 타입 인터랙션 생성")
    void createJobBoardInteraction() {
        //given
        String userId = "userId";
        String postId = "jobBoardId";
        PostType postType = PostType.JOB_BOARD;
        InteractionType interactionType = InteractionType.LIKE;

        //when
        InteractionUser interaction = InteractionUser.of(userId, postId, postType, interactionType);

        //then
        assertThat(interaction.postType()).isEqualTo(PostType.JOB_BOARD);
        assertThat(interaction.interactionType()).isEqualTo(InteractionType.LIKE);
        assertThat(interaction.postId()).isEqualTo(postId);
    }

    @Test
    @DisplayName("POST 타입 인터랙션 생성")
    void createPostInteraction() {
        //given
        String userId = "userId";
        String postId = "postId";
        PostType postType = PostType.POST;
        InteractionType interactionType = InteractionType.BOOKMARK;

        //when
        InteractionUser interaction = InteractionUser.of(userId, postId, postType, interactionType);

        //then
        assertThat(interaction.postType()).isEqualTo(PostType.POST);
        assertThat(interaction.interactionType()).isEqualTo(InteractionType.BOOKMARK);
        assertThat(interaction.userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("인터랙션 생성 시 생성 시간 설정")
    void createInteractionWithCreatedAt() {
        //given
        String userId = "userId";
        String postId = "postId";
        PostType postType = PostType.POST;
        InteractionType interactionType = InteractionType.LIKE;
        LocalDateTime beforeCreation = LocalDateTime.now();

        //when
        InteractionUser interaction = InteractionUser.of(userId, postId, postType, interactionType);

        //then
        assertThat(interaction.createdAt()).isAfter(beforeCreation.minusSeconds(1));
        assertThat(interaction.createdAt()).isBefore(LocalDateTime.now().plusSeconds(1));
    }

    @Test
    @DisplayName("null 사용자 ID로 인터랙션 생성")
    void createInteractionWithNullUserId() {
        //given
        String userId = null;
        String postId = "postId";
        PostType postType = PostType.POST;
        InteractionType interactionType = InteractionType.LIKE;

        //when
        InteractionUser interaction = InteractionUser.of(userId, postId, postType, interactionType);

        //then
        assertThat(interaction.userId()).isNull();
        assertThat(interaction.postId()).isEqualTo(postId);
        assertThat(interaction.postType()).isEqualTo(postType);
        assertThat(interaction.interactionType()).isEqualTo(interactionType);
    }

    @Test
    @DisplayName("null 게시물 ID로 인터랙션 생성")
    void createInteractionWithNullPostId() {
        //given
        String userId = "userId";
        String postId = null;
        PostType postType = PostType.POST;
        InteractionType interactionType = InteractionType.LIKE;

        //when
        InteractionUser interaction = InteractionUser.of(userId, postId, postType, interactionType);

        //then
        assertThat(interaction.userId()).isEqualTo(userId);
        assertThat(interaction.postId()).isNull();
        assertThat(interaction.postType()).isEqualTo(postType);
        assertThat(interaction.interactionType()).isEqualTo(interactionType);
    }

    @Test
    @DisplayName("빈 문자열 사용자 ID로 인터랙션 생성")
    void createInteractionWithEmptyUserId() {
        //given
        String userId = "";
        String postId = "postId";
        PostType postType = PostType.POST;
        InteractionType interactionType = InteractionType.BOOKMARK;

        //when
        InteractionUser interaction = InteractionUser.of(userId, postId, postType, interactionType);

        //then
        assertThat(interaction.userId()).isEmpty();
        assertThat(interaction.postId()).isEqualTo(postId);
        assertThat(interaction.interactionType()).isEqualTo(InteractionType.BOOKMARK);
    }

    @Test
    @DisplayName("빈 문자열 게시물 ID로 인터랙션 생성")
    void createInteractionWithEmptyPostId() {
        //given
        String userId = "userId";
        String postId = "";
        PostType postType = PostType.JOB_BOARD;
        InteractionType interactionType = InteractionType.LIKE;

        //when
        InteractionUser interaction = InteractionUser.of(userId, postId, postType, interactionType);

        //then
        assertThat(interaction.userId()).isEqualTo(userId);
        assertThat(interaction.postId()).isEmpty();
        assertThat(interaction.postType()).isEqualTo(PostType.JOB_BOARD);
        assertThat(interaction.interactionType()).isEqualTo(InteractionType.LIKE);
    }

    @Test
    @DisplayName("다양한 사용자와 게시물로 인터랙션 생성")
    void createInteractionsWithDifferentUsersAndPosts() {
        //given
        String[] userIds = {"user1", "user2", "user3"};
        String[] postIds = {"post1", "post2", "post3"};

        //when & then
        for (int i = 0; i < userIds.length; i++) {
            InteractionUser interaction = InteractionUser.of(
                    userIds[i], 
                    postIds[i], 
                    PostType.POST, 
                    InteractionType.LIKE
            );
            
            assertThat(interaction.userId()).isEqualTo(userIds[i]);
            assertThat(interaction.postId()).isEqualTo(postIds[i]);
            assertThat(interaction.interactionType()).isEqualTo(InteractionType.LIKE);
        }
    }

    @Test
    @DisplayName("레코드 불변성 검증")
    void verifyRecordImmutability() {
        //given
        String userId = "userId";
        String postId = "postId";
        PostType postType = PostType.POST;
        InteractionType interactionType = InteractionType.LIKE;

        //when
        InteractionUser interaction1 = InteractionUser.of(userId, postId, postType, interactionType);
        InteractionUser interaction2 = InteractionUser.of(userId, postId, postType, interactionType);

        //then
        assertThat(interaction1.userId()).isEqualTo(interaction2.userId());
        assertThat(interaction1.postId()).isEqualTo(interaction2.postId());
        assertThat(interaction1.postType()).isEqualTo(interaction2.postType());
        assertThat(interaction1.interactionType()).isEqualTo(interaction2.interactionType());
        assertThat(interaction1).isNotSameAs(interaction2);
    }

    @Test
    @DisplayName("모든 InteractionType으로 인터랙션 생성")
    void createInteractionsWithAllTypes() {
        //given
        String userId = "userId";
        String postId = "postId";
        PostType postType = PostType.POST;

        //when & then
        for (InteractionType type : InteractionType.values()) {
            InteractionUser interaction = InteractionUser.of(userId, postId, postType, type);
            
            assertThat(interaction.interactionType()).isEqualTo(type);
            assertThat(interaction.userId()).isEqualTo(userId);
            assertThat(interaction.postId()).isEqualTo(postId);
        }
    }
}