package com.backend.immilog.interaction.application.dto.in;

import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.shared.enums.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InteractionCreateCommandTest {

    @Nested
    @DisplayName("InteractionCreateCommand 생성 테스트")
    class InteractionCreateCommandCreationTest {

        @Test
        @DisplayName("Builder로 InteractionCreateCommand를 생성할 수 있다")
        void createInteractionCreateCommandWithBuilder() {
            String userId = "user123";
            String postId = "post456";
            ContentType contentType = ContentType.POST;
            InteractionType interactionType = InteractionType.LIKE;

            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId(userId)
                    .postId(postId)
                    .contentType(contentType)
                    .interactionType(interactionType)
                    .build();

            assertThat(command.userId()).isEqualTo(userId);
            assertThat(command.postId()).isEqualTo(postId);
            assertThat(command.contentType()).isEqualTo(contentType);
            assertThat(command.interactionType()).isEqualTo(interactionType);
        }

        @Test
        @DisplayName("좋아요 타입으로 InteractionCreateCommand를 생성할 수 있다")
        void createLikeInteractionCreateCommand() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId("likeUser")
                    .postId("likePost")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            assertThat(command.interactionType()).isEqualTo(InteractionType.LIKE);
            assertThat(command.contentType()).isEqualTo(ContentType.POST);
        }

        @Test
        @DisplayName("북마크 타입으로 InteractionCreateCommand를 생성할 수 있다")
        void createBookmarkInteractionCreateCommand() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId("bookmarkUser")
                    .postId("bookmarkPost")
                    .contentType(ContentType.COMMENT)
                    .interactionType(InteractionType.BOOKMARK)
                    .build();

            assertThat(command.interactionType()).isEqualTo(InteractionType.BOOKMARK);
            assertThat(command.contentType()).isEqualTo(ContentType.COMMENT);
        }

        @Test
        @DisplayName("댓글 컨텐츠 타입으로 InteractionCreateCommand를 생성할 수 있다")
        void createCommentContentTypeInteractionCreateCommand() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId("commentUser")
                    .postId("comment789")
                    .contentType(ContentType.COMMENT)
                    .interactionType(InteractionType.LIKE)
                    .build();

            assertThat(command.contentType()).isEqualTo(ContentType.COMMENT);
            assertThat(command.postId()).isEqualTo("comment789");
        }
    }

    @Nested
    @DisplayName("InteractionCreateCommand 속성 접근 테스트")
    class InteractionCreateCommandAccessTest {

        @Test
        @DisplayName("모든 속성에 접근할 수 있다")
        void accessAllProperties() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId("accessUser")
                    .postId("accessPost")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            assertThat(command.userId()).isNotNull();
            assertThat(command.postId()).isNotNull();
            assertThat(command.contentType()).isNotNull();
            assertThat(command.interactionType()).isNotNull();
        }

        @Test
        @DisplayName("null 값으로 속성을 설정할 수 있다")
        void setPropertiesWithNullValues() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId(null)
                    .postId(null)
                    .contentType(null)
                    .interactionType(null)
                    .build();

            assertThat(command.userId()).isNull();
            assertThat(command.postId()).isNull();
            assertThat(command.contentType()).isNull();
            assertThat(command.interactionType()).isNull();
        }

        @Test
        @DisplayName("부분적으로만 속성을 설정할 수 있다")
        void setPropertiesPartially() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId("partialUser")
                    .interactionType(InteractionType.BOOKMARK)
                    .build();

            assertThat(command.userId()).isEqualTo("partialUser");
            assertThat(command.interactionType()).isEqualTo(InteractionType.BOOKMARK);
            assertThat(command.postId()).isNull();
            assertThat(command.contentType()).isNull();
        }
    }

    @Nested
    @DisplayName("InteractionCreateCommand 동등성 테스트")
    class InteractionCreateCommandEqualityTest {

        @Test
        @DisplayName("같은 값을 가진 InteractionCreateCommand는 동등하다")
        void sameValueInteractionCreateCommandsAreEqual() {
            InteractionCreateCommand command1 = InteractionCreateCommand.builder()
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            InteractionCreateCommand command2 = InteractionCreateCommand.builder()
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            assertThat(command1).isEqualTo(command2);
            assertThat(command1.hashCode()).isEqualTo(command2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 InteractionCreateCommand는 동등하지 않다")
        void differentValueInteractionCreateCommandsAreNotEqual() {
            InteractionCreateCommand command1 = InteractionCreateCommand.builder()
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            InteractionCreateCommand command2 = InteractionCreateCommand.builder()
                    .userId("user456")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            assertThat(command1).isNotEqualTo(command2);
        }

        @Test
        @DisplayName("userId가 다를 때 동등하지 않다")
        void notEqualWhenUserIdDiffers() {
            InteractionCreateCommand command1 = InteractionCreateCommand.builder()
                    .userId("user1")
                    .postId("post123")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            InteractionCreateCommand command2 = InteractionCreateCommand.builder()
                    .userId("user2")
                    .postId("post123")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            assertThat(command1).isNotEqualTo(command2);
        }

        @Test
        @DisplayName("postId가 다를 때 동등하지 않다")
        void notEqualWhenPostIdDiffers() {
            InteractionCreateCommand command1 = InteractionCreateCommand.builder()
                    .userId("user123")
                    .postId("post1")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            InteractionCreateCommand command2 = InteractionCreateCommand.builder()
                    .userId("user123")
                    .postId("post2")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            assertThat(command1).isNotEqualTo(command2);
        }

        @Test
        @DisplayName("contentType이 다를 때 동등하지 않다")
        void notEqualWhenContentTypeDiffers() {
            InteractionCreateCommand command1 = InteractionCreateCommand.builder()
                    .userId("user123")
                    .postId("post123")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            InteractionCreateCommand command2 = InteractionCreateCommand.builder()
                    .userId("user123")
                    .postId("post123")
                    .contentType(ContentType.COMMENT)
                    .interactionType(InteractionType.LIKE)
                    .build();

            assertThat(command1).isNotEqualTo(command2);
        }

        @Test
        @DisplayName("interactionType이 다를 때 동등하지 않다")
        void notEqualWhenInteractionTypeDiffers() {
            InteractionCreateCommand command1 = InteractionCreateCommand.builder()
                    .userId("user123")
                    .postId("post123")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            InteractionCreateCommand command2 = InteractionCreateCommand.builder()
                    .userId("user123")
                    .postId("post123")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.BOOKMARK)
                    .build();

            assertThat(command1).isNotEqualTo(command2);
        }
    }

    @Nested
    @DisplayName("InteractionCreateCommand toString 테스트")
    class InteractionCreateCommandToStringTest {

        @Test
        @DisplayName("toString 메서드가 올바르게 동작한다")
        void toStringWorksCorrectly() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            String result = command.toString();

            assertThat(result).contains("InteractionCreateCommand");
            assertThat(result).contains("user123");
            assertThat(result).contains("post456");
            assertThat(result).contains("POST");
            assertThat(result).contains("LIKE");
        }

        @Test
        @DisplayName("null 값이 포함된 toString이 올바르게 동작한다")
        void toStringWorksCorrectlyWithNullValues() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId(null)
                    .postId("post456")
                    .contentType(null)
                    .interactionType(InteractionType.BOOKMARK)
                    .build();

            String result = command.toString();

            assertThat(result).contains("InteractionCreateCommand");
            assertThat(result).contains("post456");
            assertThat(result).contains("BOOKMARK");
            assertThat(result).contains("null");
        }
    }

    @Nested
    @DisplayName("InteractionCreateCommand 비즈니스 시나리오 테스트")
    class InteractionCreateCommandBusinessScenarioTest {

        @Test
        @DisplayName("게시물 좋아요 명령을 생성할 수 있다")
        void createPostLikeCommand() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId("blogger123")
                    .postId("techPost456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            assertThat(command.userId()).isEqualTo("blogger123");
            assertThat(command.postId()).isEqualTo("techPost456");
            assertThat(command.contentType()).isEqualTo(ContentType.POST);
            assertThat(command.interactionType()).isEqualTo(InteractionType.LIKE);
        }

        @Test
        @DisplayName("댓글 북마크 명령을 생성할 수 있다")
        void createCommentBookmarkCommand() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId("reader789")
                    .postId("insightfulComment")
                    .contentType(ContentType.COMMENT)
                    .interactionType(InteractionType.BOOKMARK)
                    .build();

            assertThat(command.userId()).isEqualTo("reader789");
            assertThat(command.postId()).isEqualTo("insightfulComment");
            assertThat(command.contentType()).isEqualTo(ContentType.COMMENT);
            assertThat(command.interactionType()).isEqualTo(InteractionType.BOOKMARK);
        }

        @Test
        @DisplayName("다양한 사용자 ID 형식을 지원한다")
        void supportVariousUserIdFormats() {
            String[] userIds = {"user123", "user-456", "USER_789", "email@domain.com", "uuid-12345"};

            for (String userId : userIds) {
                InteractionCreateCommand command = InteractionCreateCommand.builder()
                        .userId(userId)
                        .postId("post123")
                        .contentType(ContentType.POST)
                        .interactionType(InteractionType.LIKE)
                        .build();

                assertThat(command.userId()).isEqualTo(userId);
            }
        }

        @Test
        @DisplayName("다양한 포스트 ID 형식을 지원한다")
        void supportVariousPostIdFormats() {
            String[] postIds = {"post123", "article-456", "CONTENT_789", "12345", "uuid-abcdef"};

            for (String postId : postIds) {
                InteractionCreateCommand command = InteractionCreateCommand.builder()
                        .userId("user123")
                        .postId(postId)
                        .contentType(ContentType.POST)
                        .interactionType(InteractionType.LIKE)
                        .build();

                assertThat(command.postId()).isEqualTo(postId);
            }
        }
    }

    @Nested
    @DisplayName("InteractionCreateCommand Builder 테스트")
    class InteractionCreateCommandBuilderTest {

        @Test
        @DisplayName("Builder는 체이닝 방식으로 동작한다")
        void builderWorksWithMethodChaining() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId("chainUser")
                    .postId("chainPost")
                    .contentType(ContentType.COMMENT)
                    .interactionType(InteractionType.BOOKMARK)
                    .build();

            assertThat(command.userId()).isEqualTo("chainUser");
            assertThat(command.postId()).isEqualTo("chainPost");
            assertThat(command.contentType()).isEqualTo(ContentType.COMMENT);
            assertThat(command.interactionType()).isEqualTo(InteractionType.BOOKMARK);
        }

        @Test
        @DisplayName("Builder로 여러 인스턴스를 독립적으로 생성할 수 있다")
        void builderCreatesIndependentInstances() {
            InteractionCreateCommand.InteractionCreateCommandBuilder builder = InteractionCreateCommand.builder();

            InteractionCreateCommand command1 = builder
                    .userId("user1")
                    .postId("post1")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            InteractionCreateCommand command2 = InteractionCreateCommand.builder()
                    .userId("user2")
                    .postId("post2")
                    .contentType(ContentType.COMMENT)
                    .interactionType(InteractionType.BOOKMARK)
                    .build();

            assertThat(command1.userId()).isEqualTo("user1");
            assertThat(command2.userId()).isEqualTo("user2");
            assertThat(command1).isNotEqualTo(command2);
        }
    }
}