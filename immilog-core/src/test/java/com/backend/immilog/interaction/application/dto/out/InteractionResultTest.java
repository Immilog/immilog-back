package com.backend.immilog.interaction.application.dto.out;

import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.interaction.presentation.payload.InteractionResponse;
import com.backend.immilog.shared.enums.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class InteractionResultTest {

    @Nested
    @DisplayName("InteractionResult 생성 테스트")
    class InteractionResultCreationTest {

        @Test
        @DisplayName("생성자로 InteractionResult를 생성할 수 있다")
        void createInteractionResultWithConstructor() {
            String id = "result123";
            String userId = "user456";
            String postId = "post789";
            ContentType contentType = ContentType.POST;
            InteractionType interactionType = InteractionType.LIKE;
            InteractionStatus interactionStatus = InteractionStatus.ACTIVE;
            LocalDateTime createdAt = LocalDateTime.now();

            InteractionResult result = new InteractionResult(
                    id, userId, postId, contentType, interactionType, interactionStatus, createdAt
            );

            assertThat(result.id()).isEqualTo(id);
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.postId()).isEqualTo(postId);
            assertThat(result.contentType()).isEqualTo(contentType);
            assertThat(result.interactionType()).isEqualTo(interactionType);
            assertThat(result.interactionStatus()).isEqualTo(interactionStatus);
            assertThat(result.createdAt()).isEqualTo(createdAt);
        }

        @Test
        @DisplayName("from 메서드로 InteractionUser에서 InteractionResult를 생성할 수 있다")
        void createInteractionResultFromInteractionUser() {
            LocalDateTime now = LocalDateTime.now();
            InteractionUser interactionUser = InteractionUser.builder()
                    .id("user123")
                    .userId("user456")
                    .postId("post789")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            InteractionResult result = InteractionResult.from(interactionUser);

            assertThat(result.id()).isEqualTo("user123");
            assertThat(result.userId()).isEqualTo("user456");
            assertThat(result.postId()).isEqualTo("post789");
            assertThat(result.contentType()).isEqualTo(ContentType.POST);
            assertThat(result.interactionType()).isEqualTo(InteractionType.LIKE);
            assertThat(result.interactionStatus()).isEqualTo(InteractionStatus.ACTIVE);
            assertThat(result.createdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("북마크 타입 InteractionResult를 생성할 수 있다")
        void createBookmarkInteractionResult() {
            LocalDateTime now = LocalDateTime.now();
            InteractionUser bookmarkUser = InteractionUser.builder()
                    .id("bookmark123")
                    .userId("user456")
                    .postId("post789")
                    .contentType(ContentType.COMMENT)
                    .interactionType(InteractionType.BOOKMARK)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            InteractionResult result = InteractionResult.from(bookmarkUser);

            assertThat(result.interactionType()).isEqualTo(InteractionType.BOOKMARK);
            assertThat(result.contentType()).isEqualTo(ContentType.COMMENT);
        }

        @Test
        @DisplayName("INACTIVE 상태의 InteractionResult를 생성할 수 있다")
        void createInactiveInteractionResult() {
            LocalDateTime now = LocalDateTime.now();
            InteractionUser inactiveUser = InteractionUser.builder()
                    .id("inactive123")
                    .userId("user456")
                    .postId("post789")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.INACTIVE)
                    .createdAt(now)
                    .build();

            InteractionResult result = InteractionResult.from(inactiveUser);

            assertThat(result.interactionStatus()).isEqualTo(InteractionStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("InteractionResult 속성 접근 테스트")
    class InteractionResultAccessTest {

        @Test
        @DisplayName("모든 속성에 접근할 수 있다")
        void accessAllProperties() {
            LocalDateTime now = LocalDateTime.now();
            InteractionResult result = new InteractionResult(
                    "id123", "user456", "post789", ContentType.POST,
                    InteractionType.LIKE, InteractionStatus.ACTIVE, now
            );

            assertThat(result.id()).isNotNull();
            assertThat(result.userId()).isNotNull();
            assertThat(result.postId()).isNotNull();
            assertThat(result.contentType()).isNotNull();
            assertThat(result.interactionType()).isNotNull();
            assertThat(result.interactionStatus()).isNotNull();
            assertThat(result.createdAt()).isNotNull();
        }

        @Test
        @DisplayName("null 값으로 속성을 설정할 수 있다")
        void accessPropertiesWithNullValues() {
            InteractionResult result = new InteractionResult(
                    null, null, null, null, null, null, null
            );

            assertThat(result.id()).isNull();
            assertThat(result.userId()).isNull();
            assertThat(result.postId()).isNull();
            assertThat(result.contentType()).isNull();
            assertThat(result.interactionType()).isNull();
            assertThat(result.interactionStatus()).isNull();
            assertThat(result.createdAt()).isNull();
        }
    }

    @Nested
    @DisplayName("InteractionResult toInfraDTO 메서드 테스트")
    class InteractionResultToInfraDTOTest {

        @Test
        @DisplayName("toInfraDTO로 InteractionResponse.InteractionInformation을 생성할 수 있다")
        void convertToInfraDTO() {
            LocalDateTime now = LocalDateTime.now();
            InteractionResult result = new InteractionResult(
                    "id123", "user456", "post789", ContentType.POST,
                    InteractionType.LIKE, InteractionStatus.ACTIVE, now
            );

            InteractionResponse.InteractionInformation infraDTO = result.toInfraDTO();

            assertThat(infraDTO.interactionId()).isEqualTo("id123");
            assertThat(infraDTO.userId()).isEqualTo("user456");
            assertThat(infraDTO.postId()).isEqualTo("post789");
            assertThat(infraDTO.contentType()).isEqualTo(ContentType.POST);
            assertThat(infraDTO.interactionType()).isEqualTo(InteractionType.LIKE);
            assertThat(infraDTO.createdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("북마크 타입의 InteractionResult를 InfraDTO로 변환할 수 있다")
        void convertBookmarkToInfraDTO() {
            LocalDateTime now = LocalDateTime.now();
            InteractionResult result = new InteractionResult(
                    "bookmark456", "user789", "comment123", ContentType.COMMENT,
                    InteractionType.BOOKMARK, InteractionStatus.ACTIVE, now
            );

            InteractionResponse.InteractionInformation infraDTO = result.toInfraDTO();

            assertThat(infraDTO.interactionType()).isEqualTo(InteractionType.BOOKMARK);
            assertThat(infraDTO.contentType()).isEqualTo(ContentType.COMMENT);
        }

        @Test
        @DisplayName("INACTIVE 상태의 InteractionResult를 InfraDTO로 변환할 수 있다")
        void convertInactiveToInfraDTO() {
            LocalDateTime now = LocalDateTime.now();
            InteractionResult result = new InteractionResult(
                    "inactive789", "user123", "post456", ContentType.POST,
                    InteractionType.LIKE, InteractionStatus.INACTIVE, now
            );

            InteractionResponse.InteractionInformation infraDTO = result.toInfraDTO();

        }

        @Test
        @DisplayName("null 값이 포함된 InteractionResult를 InfraDTO로 변환할 수 있다")
        void convertNullValuesToInfraDTO() {
            InteractionResult result = new InteractionResult(
                    null, null, null, null, null, null, null
            );

            InteractionResponse.InteractionInformation infraDTO = result.toInfraDTO();

            assertThat(infraDTO.interactionId()).isNull();
            assertThat(infraDTO.userId()).isNull();
            assertThat(infraDTO.postId()).isNull();
            assertThat(infraDTO.contentType()).isNull();
            assertThat(infraDTO.interactionType()).isNull();
            assertThat(infraDTO.createdAt()).isNull();
        }
    }

    @Nested
    @DisplayName("InteractionResult 동등성 테스트")
    class InteractionResultEqualityTest {

        @Test
        @DisplayName("같은 값을 가진 InteractionResult는 동등하다")
        void sameValueInteractionResultsAreEqual() {
            LocalDateTime now = LocalDateTime.now();
            InteractionResult result1 = new InteractionResult(
                    "id123", "user456", "post789", ContentType.POST,
                    InteractionType.LIKE, InteractionStatus.ACTIVE, now
            );

            InteractionResult result2 = new InteractionResult(
                    "id123", "user456", "post789", ContentType.POST,
                    InteractionType.LIKE, InteractionStatus.ACTIVE, now
            );

            assertThat(result1).isEqualTo(result2);
            assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 InteractionResult는 동등하지 않다")
        void differentValueInteractionResultsAreNotEqual() {
            LocalDateTime now = LocalDateTime.now();
            InteractionResult result1 = new InteractionResult(
                    "id123", "user456", "post789", ContentType.POST,
                    InteractionType.LIKE, InteractionStatus.ACTIVE, now
            );

            InteractionResult result2 = new InteractionResult(
                    "id456", "user456", "post789", ContentType.POST,
                    InteractionType.LIKE, InteractionStatus.ACTIVE, now
            );

            assertThat(result1).isNotEqualTo(result2);
        }

        @Test
        @DisplayName("ID가 다를 때 동등하지 않다")
        void notEqualWhenIdDiffers() {
            LocalDateTime now = LocalDateTime.now();
            InteractionResult result1 = new InteractionResult(
                    "id1", "user456", "post789", ContentType.POST,
                    InteractionType.LIKE, InteractionStatus.ACTIVE, now
            );

            InteractionResult result2 = new InteractionResult(
                    "id2", "user456", "post789", ContentType.POST,
                    InteractionType.LIKE, InteractionStatus.ACTIVE, now
            );

            assertThat(result1).isNotEqualTo(result2);
        }

        @Test
        @DisplayName("InteractionType이 다를 때 동등하지 않다")
        void notEqualWhenInteractionTypeDiffers() {
            LocalDateTime now = LocalDateTime.now();
            InteractionResult result1 = new InteractionResult(
                    "id123", "user456", "post789", ContentType.POST,
                    InteractionType.LIKE, InteractionStatus.ACTIVE, now
            );

            InteractionResult result2 = new InteractionResult(
                    "id123", "user456", "post789", ContentType.POST,
                    InteractionType.BOOKMARK, InteractionStatus.ACTIVE, now
            );

            assertThat(result1).isNotEqualTo(result2);
        }

        @Test
        @DisplayName("InteractionStatus가 다를 때 동등하지 않다")
        void notEqualWhenInteractionStatusDiffers() {
            LocalDateTime now = LocalDateTime.now();
            InteractionResult result1 = new InteractionResult(
                    "id123", "user456", "post789", ContentType.POST,
                    InteractionType.LIKE, InteractionStatus.ACTIVE, now
            );

            InteractionResult result2 = new InteractionResult(
                    "id123", "user456", "post789", ContentType.POST,
                    InteractionType.LIKE, InteractionStatus.INACTIVE, now
            );

            assertThat(result1).isNotEqualTo(result2);
        }
    }

    @Nested
    @DisplayName("InteractionResult toString 테스트")
    class InteractionResultToStringTest {

        @Test
        @DisplayName("toString 메서드가 올바르게 동작한다")
        void toStringWorksCorrectly() {
            LocalDateTime now = LocalDateTime.now();
            InteractionResult result = new InteractionResult(
                    "id123", "user456", "post789", ContentType.POST,
                    InteractionType.LIKE, InteractionStatus.ACTIVE, now
            );

            String resultString = result.toString();

            assertThat(resultString).contains("InteractionResult");
            assertThat(resultString).contains("id123");
            assertThat(resultString).contains("user456");
            assertThat(resultString).contains("post789");
            assertThat(resultString).contains("POST");
            assertThat(resultString).contains("LIKE");
            assertThat(resultString).contains("ACTIVE");
        }

        @Test
        @DisplayName("null 값이 포함된 toString이 올바르게 동작한다")
        void toStringWorksCorrectlyWithNullValues() {
            InteractionResult result = new InteractionResult(
                    null, "user456", null, ContentType.POST,
                    null, InteractionStatus.ACTIVE, null
            );

            String resultString = result.toString();

            assertThat(resultString).contains("InteractionResult");
            assertThat(resultString).contains("user456");
            assertThat(resultString).contains("POST");
            assertThat(resultString).contains("ACTIVE");
            assertThat(resultString).contains("null");
        }
    }

    @Nested
    @DisplayName("InteractionResult 변환 일관성 테스트")
    class InteractionResultConversionConsistencyTest {

        @Test
        @DisplayName("InteractionUser에서 변환한 후 toInfraDTO 호출이 일관되게 동작한다")
        void conversionConsistencyFromUserToInfraDTO() {
            LocalDateTime now = LocalDateTime.now();
            InteractionUser originalUser = InteractionUser.builder()
                    .id("consistency123")
                    .userId("user789")
                    .postId("post456")
                    .contentType(ContentType.COMMENT)
                    .interactionType(InteractionType.BOOKMARK)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            InteractionResult result = InteractionResult.from(originalUser);
            InteractionResponse.InteractionInformation infraDTO = result.toInfraDTO();

            assertThat(infraDTO.interactionId()).isEqualTo(originalUser.id());
            assertThat(infraDTO.userId()).isEqualTo(originalUser.userId());
            assertThat(infraDTO.postId()).isEqualTo(originalUser.postId());
            assertThat(infraDTO.contentType()).isEqualTo(originalUser.contentType());
            assertThat(infraDTO.interactionType()).isEqualTo(originalUser.interactionType());
            assertThat(infraDTO.createdAt()).isEqualTo(originalUser.createdAt());
        }

        @Test
        @DisplayName("여러 타입의 InteractionUser 변환이 모두 올바르게 동작한다")
        void multipleInteractionTypeConversionsWork() {
            LocalDateTime now = LocalDateTime.now();

            InteractionUser likeUser = InteractionUser.createLike("user1", "post1", ContentType.POST);
            InteractionUser bookmarkUser = InteractionUser.createBookmark("user2", "post2", ContentType.COMMENT);

            InteractionResult likeResult = InteractionResult.from(likeUser);
            InteractionResult bookmarkResult = InteractionResult.from(bookmarkUser);

            assertThat(likeResult.interactionType()).isEqualTo(InteractionType.LIKE);
            assertThat(bookmarkResult.interactionType()).isEqualTo(InteractionType.BOOKMARK);

            InteractionResponse.InteractionInformation likeInfraDTO = likeResult.toInfraDTO();
            InteractionResponse.InteractionInformation bookmarkInfraDTO = bookmarkResult.toInfraDTO();

            assertThat(likeInfraDTO.interactionType()).isEqualTo(InteractionType.LIKE);
            assertThat(bookmarkInfraDTO.interactionType()).isEqualTo(InteractionType.BOOKMARK);
        }
    }

    @Nested
    @DisplayName("InteractionResult 비즈니스 시나리오 테스트")
    class InteractionResultBusinessScenarioTest {

        @Test
        @DisplayName("게시물 좋아요 결과를 처리할 수 있다")
        void handlePostLikeResult() {
            LocalDateTime now = LocalDateTime.now();
            InteractionUser postLike = InteractionUser.builder()
                    .id("postLike123")
                    .userId("blogger456")
                    .postId("techArticle789")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            InteractionResult result = InteractionResult.from(postLike);

            assertThat(result.userId()).isEqualTo("blogger456");
            assertThat(result.postId()).isEqualTo("techArticle789");
            assertThat(result.contentType()).isEqualTo(ContentType.POST);
            assertThat(result.interactionType()).isEqualTo(InteractionType.LIKE);
            assertThat(result.interactionStatus()).isEqualTo(InteractionStatus.ACTIVE);
        }

        @Test
        @DisplayName("댓글 북마크 결과를 처리할 수 있다")
        void handleCommentBookmarkResult() {
            LocalDateTime now = LocalDateTime.now();
            InteractionUser commentBookmark = InteractionUser.builder()
                    .id("commentBookmark456")
                    .userId("reader789")
                    .postId("insightfulComment123")
                    .contentType(ContentType.COMMENT)
                    .interactionType(InteractionType.BOOKMARK)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            InteractionResult result = InteractionResult.from(commentBookmark);

            assertThat(result.userId()).isEqualTo("reader789");
            assertThat(result.postId()).isEqualTo("insightfulComment123");
            assertThat(result.contentType()).isEqualTo(ContentType.COMMENT);
            assertThat(result.interactionType()).isEqualTo(InteractionType.BOOKMARK);
            assertThat(result.interactionStatus()).isEqualTo(InteractionStatus.ACTIVE);
        }

        @Test
        @DisplayName("비활성화된 인터랙션 결과를 처리할 수 있다")
        void handleDeactivatedInteractionResult() {
            LocalDateTime now = LocalDateTime.now();
            InteractionUser deactivatedInteraction = InteractionUser.builder()
                    .id("deactivated123")
                    .userId("user456")
                    .postId("post789")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.INACTIVE)
                    .createdAt(now)
                    .build();

            InteractionResult result = InteractionResult.from(deactivatedInteraction);
            InteractionResponse.InteractionInformation infraDTO = result.toInfraDTO();

            assertThat(result.interactionStatus()).isEqualTo(InteractionStatus.INACTIVE);
        }
    }
}