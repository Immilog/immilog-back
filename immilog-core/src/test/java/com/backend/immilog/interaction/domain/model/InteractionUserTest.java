package com.backend.immilog.interaction.domain.model;

import com.backend.immilog.shared.enums.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class InteractionUserTest {

    @Nested
    @DisplayName("InteractionUser 생성 테스트")
    class InteractionUserCreationTest {

        @Test
        @DisplayName("of 메서드로 InteractionUser를 생성할 수 있다")
        void createInteractionUserWithOfMethod() {
            String userId = "user123";
            String postId = "post456";
            ContentType contentType = ContentType.POST;
            InteractionType interactionType = InteractionType.LIKE;

            InteractionUser result = InteractionUser.of(userId, postId, contentType, interactionType);

            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.postId()).isEqualTo(postId);
            assertThat(result.contentType()).isEqualTo(contentType);
            assertThat(result.interactionType()).isEqualTo(interactionType);
            assertThat(result.interactionStatus()).isEqualTo(InteractionStatus.ACTIVE);
            assertThat(result.createdAt()).isNotNull();
            assertThat(result.id()).isNull();
        }

        @Test
        @DisplayName("createBookmark 메서드로 북마크 InteractionUser를 생성할 수 있다")
        void createBookmarkInteractionUser() {
            String userId = "user123";
            String postId = "post456";
            ContentType contentType = ContentType.POST;

            InteractionUser result = InteractionUser.createBookmark(userId, postId, contentType);

            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.postId()).isEqualTo(postId);
            assertThat(result.contentType()).isEqualTo(contentType);
            assertThat(result.interactionType()).isEqualTo(InteractionType.BOOKMARK);
            assertThat(result.interactionStatus()).isEqualTo(InteractionStatus.ACTIVE);
            assertThat(result.createdAt()).isNotNull();
        }

        @Test
        @DisplayName("createLike 메서드로 좋아요 InteractionUser를 생성할 수 있다")
        void createLikeInteractionUser() {
            String userId = "user123";
            String postId = "post456";
            ContentType contentType = ContentType.POST;

            InteractionUser result = InteractionUser.createLike(userId, postId, contentType);

            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.postId()).isEqualTo(postId);
            assertThat(result.contentType()).isEqualTo(contentType);
            assertThat(result.interactionType()).isEqualTo(InteractionType.LIKE);
            assertThat(result.interactionStatus()).isEqualTo(InteractionStatus.ACTIVE);
            assertThat(result.createdAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("InteractionUser 상태 변경 테스트")
    class InteractionUserToggleTest {

        @Test
        @DisplayName("ACTIVE 상태를 INACTIVE로 토글할 수 있다")
        void toggleActiveToInactive() {
            InteractionUser activeInteraction = InteractionUser.builder()
                    .id("id123")
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            InteractionUser result = activeInteraction.toggleStatus();

            assertThat(result.interactionStatus()).isEqualTo(InteractionStatus.INACTIVE);
            assertThat(result.id()).isEqualTo(activeInteraction.id());
            assertThat(result.userId()).isEqualTo(activeInteraction.userId());
            assertThat(result.postId()).isEqualTo(activeInteraction.postId());
            assertThat(result.contentType()).isEqualTo(activeInteraction.contentType());
            assertThat(result.interactionType()).isEqualTo(activeInteraction.interactionType());
            assertThat(result.createdAt()).isAfter(activeInteraction.createdAt());
        }

        @Test
        @DisplayName("INACTIVE 상태를 ACTIVE로 토글할 수 있다")
        void toggleInactiveToActive() {
            InteractionUser inactiveInteraction = InteractionUser.builder()
                    .id("id123")
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.BOOKMARK)
                    .interactionStatus(InteractionStatus.INACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            InteractionUser result = inactiveInteraction.toggleStatus();

            assertThat(result.interactionStatus()).isEqualTo(InteractionStatus.ACTIVE);
            assertThat(result.id()).isEqualTo(inactiveInteraction.id());
            assertThat(result.userId()).isEqualTo(inactiveInteraction.userId());
            assertThat(result.postId()).isEqualTo(inactiveInteraction.postId());
            assertThat(result.contentType()).isEqualTo(inactiveInteraction.contentType());
            assertThat(result.interactionType()).isEqualTo(inactiveInteraction.interactionType());
        }
    }

    @Nested
    @DisplayName("InteractionUser Builder 테스트")
    class InteractionUserBuilderTest {

        @Test
        @DisplayName("Builder로 완전한 InteractionUser를 생성할 수 있다")
        void createCompleteInteractionUserWithBuilder() {
            String id = "id123";
            String userId = "user123";
            String postId = "post456";
            ContentType contentType = ContentType.COMMENT;
            InteractionType interactionType = InteractionType.LIKE;
            InteractionStatus interactionStatus = InteractionStatus.ACTIVE;
            LocalDateTime createdAt = LocalDateTime.now();

            InteractionUser result = InteractionUser.builder()
                    .id(id)
                    .userId(userId)
                    .postId(postId)
                    .contentType(contentType)
                    .interactionType(interactionType)
                    .interactionStatus(interactionStatus)
                    .createdAt(createdAt)
                    .build();

            assertThat(result.id()).isEqualTo(id);
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.postId()).isEqualTo(postId);
            assertThat(result.contentType()).isEqualTo(contentType);
            assertThat(result.interactionType()).isEqualTo(interactionType);
            assertThat(result.interactionStatus()).isEqualTo(interactionStatus);
            assertThat(result.createdAt()).isEqualTo(createdAt);
        }

        @Test
        @DisplayName("Builder로 부분적인 InteractionUser를 생성할 수 있다")
        void createPartialInteractionUserWithBuilder() {
            String userId = "user123";
            String postId = "post456";

            InteractionUser result = InteractionUser.builder()
                    .userId(userId)
                    .postId(postId)
                    .build();

            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.postId()).isEqualTo(postId);
            assertThat(result.id()).isNull();
            assertThat(result.contentType()).isNull();
            assertThat(result.interactionType()).isNull();
            assertThat(result.interactionStatus()).isNull();
            assertThat(result.createdAt()).isNull();
        }
    }

    @Nested
    @DisplayName("InteractionUser 동등성 테스트")
    class InteractionUserEqualityTest {

        @Test
        @DisplayName("같은 값을 가진 InteractionUser는 동등하다")
        void sameValueInteractionUsersAreEqual() {
            LocalDateTime now = LocalDateTime.now();
            InteractionUser interaction1 = InteractionUser.builder()
                    .id("id123")
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            InteractionUser interaction2 = InteractionUser.builder()
                    .id("id123")
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            assertThat(interaction1).isEqualTo(interaction2);
            assertThat(interaction1.hashCode()).isEqualTo(interaction2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 InteractionUser는 동등하지 않다")
        void differentValueInteractionUsersAreNotEqual() {
            InteractionUser interaction1 = InteractionUser.builder()
                    .id("id123")
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            InteractionUser interaction2 = InteractionUser.builder()
                    .id("id456")
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            assertThat(interaction1).isNotEqualTo(interaction2);
        }
    }

    @Nested
    @DisplayName("InteractionUser toString 테스트")
    class InteractionUserToStringTest {

        @Test
        @DisplayName("toString 메서드가 올바르게 동작한다")
        void toStringWorksCorrectly() {
            InteractionUser interaction = InteractionUser.builder()
                    .id("id123")
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            String result = interaction.toString();

            assertThat(result).contains("InteractionUser");
            assertThat(result).contains("id123");
            assertThat(result).contains("user123");
            assertThat(result).contains("post456");
            assertThat(result).contains("POST");
            assertThat(result).contains("LIKE");
            assertThat(result).contains("ACTIVE");
        }
    }
}