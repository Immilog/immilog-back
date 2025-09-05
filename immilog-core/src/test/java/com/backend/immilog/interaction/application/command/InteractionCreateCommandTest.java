package com.backend.immilog.interaction.application.command;

import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.shared.enums.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InteractionCreateCommandTest {

    @Test
    @DisplayName("InteractionCreateCommand 생성 - 정상 케이스")
    void createInteractionCreateCommandSuccessfully() {
        //given
        String userId = "userId";
        String postId = "postId";
        ContentType contentType = ContentType.POST;
        InteractionType interactionType = InteractionType.LIKE;

        //when
        InteractionCreateCommand command = new InteractionCreateCommand(userId, postId, contentType, interactionType);

        //then
        assertThat(command.userId()).isEqualTo(userId);
        assertThat(command.postId()).isEqualTo(postId);
        assertThat(command.contentType()).isEqualTo(contentType);
        assertThat(command.interactionType()).isEqualTo(interactionType);
    }

    @Test
    @DisplayName("좋아요 InteractionCreateCommand 생성")
    void createLikeInteractionCreateCommand() {
        //given
        String userId = "userId";
        String postId = "postId";
        ContentType contentType = ContentType.POST;
        InteractionType interactionType = InteractionType.LIKE;

        //when
        InteractionCreateCommand command = new InteractionCreateCommand(userId, postId, contentType, interactionType);

        //then
        assertThat(command.interactionType()).isEqualTo(InteractionType.LIKE);
        assertThat(command.contentType()).isEqualTo(ContentType.POST);
        assertThat(command.userId()).isEqualTo(userId);
        assertThat(command.postId()).isEqualTo(postId);
    }

    @Test
    @DisplayName("북마크 InteractionCreateCommand 생성")
    void createBookmarkInteractionCreateCommand() {
        //given
        String userId = "userId";
        String postId = "postId";
        ContentType contentType = ContentType.POST;
        InteractionType interactionType = InteractionType.BOOKMARK;

        //when
        InteractionCreateCommand command = new InteractionCreateCommand(userId, postId, contentType, interactionType);

        //then
        assertThat(command.interactionType()).isEqualTo(InteractionType.BOOKMARK);
        assertThat(command.contentType()).isEqualTo(ContentType.POST);
        assertThat(command.userId()).isEqualTo(userId);
        assertThat(command.postId()).isEqualTo(postId);
    }

    @Test
    @DisplayName("COMMENT 타입 InteractionCreateCommand 생성")
    void createCommentInteractionCreateCommand() {
        //given
        String userId = "userId";
        String postId = "commentId";
        ContentType contentType = ContentType.COMMENT;
        InteractionType interactionType = InteractionType.LIKE;

        //when
        InteractionCreateCommand command = new InteractionCreateCommand(userId, postId, contentType, interactionType);

        //then
        assertThat(command.contentType()).isEqualTo(ContentType.COMMENT);
        assertThat(command.interactionType()).isEqualTo(InteractionType.LIKE);
        assertThat(command.postId()).isEqualTo(postId);
        assertThat(command.userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("POST 타입 InteractionCreateCommand 생성")
    void createPostInteractionCreateCommand() {
        //given
        String userId = "userId";
        String postId = "postId";
        ContentType contentType = ContentType.POST;
        InteractionType interactionType = InteractionType.BOOKMARK;

        //when
        InteractionCreateCommand command = new InteractionCreateCommand(userId, postId, contentType, interactionType);

        //then
        assertThat(command.contentType()).isEqualTo(ContentType.POST);
        assertThat(command.interactionType()).isEqualTo(InteractionType.BOOKMARK);
        assertThat(command.userId()).isEqualTo(userId);
        assertThat(command.postId()).isEqualTo(postId);
    }

    @Test
    @DisplayName("null 값으로 InteractionCreateCommand 생성")
    void createInteractionCreateCommandWithNullValues() {
        //given
        String userId = null;
        String postId = null;
        ContentType contentType = null;
        InteractionType interactionType = null;

        //when
        InteractionCreateCommand command = new InteractionCreateCommand(userId, postId, contentType, interactionType);

        //then
        assertThat(command.userId()).isNull();
        assertThat(command.postId()).isNull();
        assertThat(command.contentType()).isNull();
        assertThat(command.interactionType()).isNull();
    }

    @Test
    @DisplayName("빈 문자열로 InteractionCreateCommand 생성")
    void createInteractionCreateCommandWithEmptyStrings() {
        //given
        String userId = "";
        String postId = "";
        ContentType contentType = ContentType.POST;
        InteractionType interactionType = InteractionType.LIKE;

        //when
        InteractionCreateCommand command = new InteractionCreateCommand(userId, postId, contentType, interactionType);

        //then
        assertThat(command.userId()).isEmpty();
        assertThat(command.postId()).isEmpty();
        assertThat(command.contentType()).isEqualTo(ContentType.POST);
        assertThat(command.interactionType()).isEqualTo(InteractionType.LIKE);
    }

    @Test
    @DisplayName("모든 InteractionType으로 InteractionCreateCommand 생성")
    void createInteractionCreateCommandWithAllInteractionTypes() {
        //given
        String userId = "userId";
        String postId = "postId";
        ContentType contentType = ContentType.POST;

        //when & then
        for (InteractionType type : InteractionType.values()) {
            InteractionCreateCommand command = new InteractionCreateCommand(userId, postId, contentType, type);
            
            assertThat(command.interactionType()).isEqualTo(type);
            assertThat(command.userId()).isEqualTo(userId);
            assertThat(command.postId()).isEqualTo(postId);
            assertThat(command.contentType()).isEqualTo(contentType);
        }
    }

    @Test
    @DisplayName("모든 PostType으로 InteractionCreateCommand 생성")
    void createInteractionCreateCommandWithAllPostTypes() {
        //given
        String userId = "userId";
        String postId = "postId";
        InteractionType interactionType = InteractionType.LIKE;

        //when & then
        for (ContentType type : ContentType.values()) {
            InteractionCreateCommand command = new InteractionCreateCommand(userId, postId, type, interactionType);
            
            assertThat(command.contentType()).isEqualTo(type);
            assertThat(command.userId()).isEqualTo(userId);
            assertThat(command.postId()).isEqualTo(postId);
            assertThat(command.interactionType()).isEqualTo(interactionType);
        }
    }

    @Test
    @DisplayName("레코드 불변성 검증")
    void verifyRecordImmutability() {
        //given
        String userId = "userId";
        String postId = "postId";
        ContentType contentType = ContentType.POST;
        InteractionType interactionType = InteractionType.LIKE;

        //when
        InteractionCreateCommand command1 = new InteractionCreateCommand(userId, postId, contentType, interactionType);
        InteractionCreateCommand command2 = new InteractionCreateCommand(userId, postId, contentType, interactionType);

        //then
        assertThat(command1.userId()).isEqualTo(command2.userId());
        assertThat(command1.postId()).isEqualTo(command2.postId());
        assertThat(command1.contentType()).isEqualTo(command2.contentType());
        assertThat(command1.interactionType()).isEqualTo(command2.interactionType());
        assertThat(command1).isNotSameAs(command2);
    }

    @Test
    @DisplayName("동등성 검증")
    void verifyEquality() {
        //given
        String userId = "userId";
        String postId = "postId";
        ContentType contentType = ContentType.POST;
        InteractionType interactionType = InteractionType.LIKE;

        //when
        InteractionCreateCommand command1 = new InteractionCreateCommand(userId, postId, contentType, interactionType);
        InteractionCreateCommand command2 = new InteractionCreateCommand(userId, postId, contentType, interactionType);
        InteractionCreateCommand command3 = new InteractionCreateCommand("differentUserId", postId, contentType, interactionType);

        //then
        assertThat(command1).isEqualTo(command2);
        assertThat(command1).isNotEqualTo(command3);
        assertThat(command1.hashCode()).isEqualTo(command2.hashCode());
        assertThat(command1.hashCode()).isNotEqualTo(command3.hashCode());
    }

    @Test
    @DisplayName("toString 검증")
    void verifyToString() {
        //given
        String userId = "userId";
        String postId = "postId";
        ContentType contentType = ContentType.POST;
        InteractionType interactionType = InteractionType.LIKE;

        //when
        InteractionCreateCommand command = new InteractionCreateCommand(userId, postId, contentType, interactionType);
        String toStringResult = command.toString();

        //then
        assertThat(toStringResult).contains("userId");
        assertThat(toStringResult).contains("postId");
        assertThat(toStringResult).contains("POST");
        assertThat(toStringResult).contains("LIKE");
    }

    @Test
    @DisplayName("다양한 조합으로 InteractionCreateCommand 생성")
    void createInteractionCreateCommandWithVariousCombinations() {
        //given & when & then
        InteractionCreateCommand likePostCommand = new InteractionCreateCommand(
                "user1", "post1", ContentType.POST, InteractionType.LIKE);
        InteractionCreateCommand bookmarkPostCommand = new InteractionCreateCommand(
                "user2", "post2", ContentType.POST, InteractionType.BOOKMARK);
        InteractionCreateCommand likeCommentCommand = new InteractionCreateCommand(
                "user3", "comment1", ContentType.COMMENT, InteractionType.LIKE);
        InteractionCreateCommand bookmarkCommentCommand = new InteractionCreateCommand(
                "user4", "comment2", ContentType.COMMENT, InteractionType.BOOKMARK);

        assertThat(likePostCommand.interactionType()).isEqualTo(InteractionType.LIKE);
        assertThat(likePostCommand.contentType()).isEqualTo(ContentType.POST);
        
        assertThat(bookmarkPostCommand.interactionType()).isEqualTo(InteractionType.BOOKMARK);
        assertThat(bookmarkPostCommand.contentType()).isEqualTo(ContentType.POST);
        
        assertThat(likeCommentCommand.interactionType()).isEqualTo(InteractionType.LIKE);
        assertThat(likeCommentCommand.contentType()).isEqualTo(ContentType.COMMENT);
        
        assertThat(bookmarkCommentCommand.interactionType()).isEqualTo(InteractionType.BOOKMARK);
        assertThat(bookmarkCommentCommand.contentType()).isEqualTo(ContentType.COMMENT);
    }
}