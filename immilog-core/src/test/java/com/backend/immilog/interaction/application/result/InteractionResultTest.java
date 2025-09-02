package com.backend.immilog.interaction.application.result;

import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.shared.enums.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InteractionResultTest {

    @Test
    @DisplayName("InteractionResult 생성 - 정상 케이스")
    void createInteractionResultSuccessfully() {
        //given
        String id = "interactionId";
        String userId = "userId";
        String postId = "postId";
        ContentType contentType = ContentType.POST;
        InteractionType interactionType = InteractionType.LIKE;
        LocalDateTime createdAt = LocalDateTime.now();

        //when
        InteractionResult result = new InteractionResult(id, userId, postId, contentType, interactionType, createdAt);

        //then
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.postId()).isEqualTo(postId);
        assertThat(result.contentType()).isEqualTo(contentType);
        assertThat(result.interactionType()).isEqualTo(interactionType);
        assertThat(result.createdAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("InteractionUser로부터 InteractionResult 생성")
    void createInteractionResultFromInteractionUser() {
        //given
        InteractionUser interactionUser = new InteractionUser(
                "interactionId",
                "userId",
                "postId",
                ContentType.POST,
                InteractionType.LIKE,
                InteractionStatus.ACTIVE,
                LocalDateTime.now()
        );

        //when
        InteractionResult result = InteractionResult.from(interactionUser);

        //then
        assertThat(result.id()).isEqualTo(interactionUser.id());
        assertThat(result.userId()).isEqualTo(interactionUser.userId());
        assertThat(result.postId()).isEqualTo(interactionUser.postId());
        assertThat(result.contentType()).isEqualTo(interactionUser.contentType());
        assertThat(result.interactionType()).isEqualTo(interactionUser.interactionType());
        assertThat(result.createdAt()).isEqualTo(interactionUser.createdAt());
    }

    @Test
    @DisplayName("좋아요 InteractionResult 생성")
    void createLikeInteractionResult() {
        //given
        InteractionUser likeInteraction = new InteractionUser(
                "likeId",
                "userId",
                "postId",
                ContentType.POST,
                InteractionType.LIKE,
                InteractionStatus.ACTIVE,
                LocalDateTime.now()
        );

        //when
        InteractionResult result = InteractionResult.from(likeInteraction);

        //then
        assertThat(result.interactionType()).isEqualTo(InteractionType.LIKE);
        assertThat(result.contentType()).isEqualTo(ContentType.POST);
        assertThat(result.id()).isEqualTo("likeId");
    }

    @Test
    @DisplayName("북마크 InteractionResult 생성")
    void createBookmarkInteractionResult() {
        //given
        InteractionUser bookmarkInteraction = new InteractionUser(
                "bookmarkId",
                "userId",
                "postId",
                ContentType.POST,
                InteractionType.BOOKMARK,
                InteractionStatus.ACTIVE,
                LocalDateTime.now()
        );

        //when
        InteractionResult result = InteractionResult.from(bookmarkInteraction);

        //then
        assertThat(result.interactionType()).isEqualTo(InteractionType.BOOKMARK);
        assertThat(result.contentType()).isEqualTo(ContentType.POST);
        assertThat(result.id()).isEqualTo("bookmarkId");
    }

    @Test
    @DisplayName("COMMENT 타입 InteractionResult 생성")
    void createCommentInteractionResult() {
        //given
        InteractionUser commentInteraction = new InteractionUser(
                "commentId",
                "userId",
                "commentPostId",
                ContentType.COMMENT,
                InteractionType.LIKE,
                InteractionStatus.ACTIVE,
                LocalDateTime.now()
        );

        //when
        InteractionResult result = InteractionResult.from(commentInteraction);

        //then
        assertThat(result.contentType()).isEqualTo(ContentType.COMMENT);
        assertThat(result.interactionType()).isEqualTo(InteractionType.LIKE);
        assertThat(result.postId()).isEqualTo("commentPostId");
    }

    @Test
    @DisplayName("POST 타입 InteractionResult 생성")
    void createPostInteractionResult() {
        //given
        InteractionUser postInteraction = new InteractionUser(
                "postInteractionId",
                "userId",
                "postId",
                ContentType.POST,
                InteractionType.BOOKMARK,
                InteractionStatus.ACTIVE,
                LocalDateTime.now()
        );

        //when
        InteractionResult result = InteractionResult.from(postInteraction);

        //then
        assertThat(result.contentType()).isEqualTo(ContentType.POST);
        assertThat(result.interactionType()).isEqualTo(InteractionType.BOOKMARK);
        assertThat(result.postId()).isEqualTo("postId");
    }

    @Test
    @DisplayName("null 값으로 InteractionResult 생성")
    void createInteractionResultWithNullValues() {
        //given
        String id = null;
        String userId = null;
        String postId = null;
        ContentType contentType = null;
        InteractionType interactionType = null;
        LocalDateTime createdAt = null;

        //when
        InteractionResult result = new InteractionResult(id, userId, postId, contentType, interactionType, createdAt);

        //then
        assertThat(result.id()).isNull();
        assertThat(result.userId()).isNull();
        assertThat(result.postId()).isNull();
        assertThat(result.contentType()).isNull();
        assertThat(result.interactionType()).isNull();
        assertThat(result.createdAt()).isNull();
    }

    @Test
    @DisplayName("빈 문자열로 InteractionResult 생성")
    void createInteractionResultWithEmptyStrings() {
        //given
        String id = "";
        String userId = "";
        String postId = "";
        ContentType contentType = ContentType.POST;
        InteractionType interactionType = InteractionType.LIKE;
        LocalDateTime createdAt = LocalDateTime.now();

        //when
        InteractionResult result = new InteractionResult(id, userId, postId, contentType, interactionType, createdAt);

        //then
        assertThat(result.id()).isEmpty();
        assertThat(result.userId()).isEmpty();
        assertThat(result.postId()).isEmpty();
        assertThat(result.contentType()).isEqualTo(ContentType.POST);
        assertThat(result.interactionType()).isEqualTo(InteractionType.LIKE);
        assertThat(result.createdAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("null InteractionUser로부터 InteractionResult 생성 시 예외 발생")
    void throwExceptionWhenCreatingFromNullInteractionUser() {
        //given
        InteractionUser nullInteractionUser = null;

        //when & then
        assertThatThrownBy(() -> InteractionResult.from(nullInteractionUser))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("모든 InteractionType으로 InteractionResult 생성")
    void createInteractionResultWithAllInteractionTypes() {
        //given
        LocalDateTime createdAt = LocalDateTime.now();

        //when & then
        for (InteractionType type : InteractionType.values()) {
            InteractionUser interaction = new InteractionUser(
                    "id", "userId", "postId", ContentType.POST, type, InteractionStatus.ACTIVE, createdAt);
            InteractionResult result = InteractionResult.from(interaction);
            
            assertThat(result.interactionType()).isEqualTo(type);
            assertThat(result.contentType()).isEqualTo(ContentType.POST);
        }
    }

    @Test
    @DisplayName("모든 PostType으로 InteractionResult 생성")
    void createInteractionResultWithAllPostTypes() {
        //given
        LocalDateTime createdAt = LocalDateTime.now();

        //when & then
        for (ContentType type : ContentType.values()) {
            InteractionUser interaction = new InteractionUser(
                    "id", "userId", "postId", type, InteractionType.LIKE, InteractionStatus.ACTIVE, createdAt);
            InteractionResult result = InteractionResult.from(interaction);
            
            assertThat(result.contentType()).isEqualTo(type);
            assertThat(result.interactionType()).isEqualTo(InteractionType.LIKE);
        }
    }

    @Test
    @DisplayName("레코드 불변성 검증")
    void verifyRecordImmutability() {
        //given
        LocalDateTime createdAt = LocalDateTime.now();
        InteractionUser interaction = new InteractionUser(
                "id", "userId", "postId", ContentType.POST, InteractionType.LIKE, InteractionStatus.ACTIVE, createdAt);

        //when
        InteractionResult result1 = InteractionResult.from(interaction);
        InteractionResult result2 = InteractionResult.from(interaction);

        //then
        assertThat(result1.id()).isEqualTo(result2.id());
        assertThat(result1.userId()).isEqualTo(result2.userId());
        assertThat(result1.postId()).isEqualTo(result2.postId());
        assertThat(result1.contentType()).isEqualTo(result2.contentType());
        assertThat(result1.interactionType()).isEqualTo(result2.interactionType());
        assertThat(result1.createdAt()).isEqualTo(result2.createdAt());
        assertThat(result1).isNotSameAs(result2);
    }

    @Test
    @DisplayName("동등성 검증")
    void verifyEquality() {
        //given
        LocalDateTime createdAt = LocalDateTime.now();
        InteractionUser interaction1 = new InteractionUser(
                "id", "userId", "postId", ContentType.POST, InteractionType.LIKE, InteractionStatus.ACTIVE, createdAt);
        InteractionUser interaction2 = new InteractionUser(
                "id", "userId", "postId", ContentType.POST, InteractionType.LIKE, InteractionStatus.ACTIVE, createdAt);
        InteractionUser interaction3 = new InteractionUser(
                "differentId", "userId", "postId", ContentType.POST, InteractionType.LIKE, InteractionStatus.ACTIVE, createdAt);

        //when
        InteractionResult result1 = InteractionResult.from(interaction1);
        InteractionResult result2 = InteractionResult.from(interaction2);
        InteractionResult result3 = InteractionResult.from(interaction3);

        //then
        assertThat(result1).isEqualTo(result2);
        assertThat(result1).isNotEqualTo(result3);
        assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
        assertThat(result1.hashCode()).isNotEqualTo(result3.hashCode());
    }

    @Test
    @DisplayName("toString 검증")
    void verifyToString() {
        //given
        InteractionUser interaction = new InteractionUser(
                "interactionId", "userId", "postId", ContentType.POST, InteractionType.LIKE, InteractionStatus.ACTIVE, LocalDateTime.now());

        //when
        InteractionResult result = InteractionResult.from(interaction);
        String toStringResult = result.toString();

        //then
        assertThat(toStringResult).contains("interactionId");
        assertThat(toStringResult).contains("userId");
        assertThat(toStringResult).contains("postId");
        assertThat(toStringResult).contains("POST");
        assertThat(toStringResult).contains("LIKE");
    }

    @Test
    @DisplayName("시간 정보 정확성 검증")
    void verifyTimeAccuracy() {
        //given
        LocalDateTime specificTime = LocalDateTime.of(2023, 10, 15, 14, 30, 45);
        InteractionUser interaction = new InteractionUser(
                "id", "userId", "postId", ContentType.POST, InteractionType.BOOKMARK, InteractionStatus.ACTIVE, specificTime);

        //when
        InteractionResult result = InteractionResult.from(interaction);

        //then
        assertThat(result.createdAt()).isEqualTo(specificTime);
        assertThat(result.createdAt().getYear()).isEqualTo(2023);
        assertThat(result.createdAt().getMonthValue()).isEqualTo(10);
        assertThat(result.createdAt().getDayOfMonth()).isEqualTo(15);
        assertThat(result.createdAt().getHour()).isEqualTo(14);
        assertThat(result.createdAt().getMinute()).isEqualTo(30);
        assertThat(result.createdAt().getSecond()).isEqualTo(45);
    }
}