package com.backend.immilog.interaction.domain.service;

import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.interaction.exception.InteractionErrorCode;
import com.backend.immilog.interaction.exception.InteractionException;
import com.backend.immilog.shared.enums.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InteractionDomainServiceTest {

    private final InteractionDomainService interactionDomainService = new InteractionDomainService.InteractionDomainServiceImpl();

    @Nested
    @DisplayName("상호작용 규칙 검증 테스트")
    class ValidateInteractionRulesTest {

        @Test
        @DisplayName("유효한 상호작용을 검증할 수 있다")
        void validateValidInteraction() {
            InteractionUser validInteraction = InteractionUser.builder()
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            interactionDomainService.validateInteractionRules(validInteraction);
        }

        @Test
        @DisplayName("null 상호작용 검증 시 예외가 발생한다")
        void validateNullInteractionThrowsException() {
            assertThatThrownBy(() -> interactionDomainService.validateInteractionRules(null))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }

        @Test
        @DisplayName("null userId를 가진 상호작용 검증 시 예외가 발생한다")
        void validateInteractionWithNullUserIdThrowsException() {
            InteractionUser invalidInteraction = InteractionUser.builder()
                    .userId(null)
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            assertThatThrownBy(() -> interactionDomainService.validateInteractionRules(invalidInteraction))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }

        @Test
        @DisplayName("빈 userId를 가진 상호작용 검증 시 예외가 발생한다")
        void validateInteractionWithEmptyUserIdThrowsException() {
            InteractionUser invalidInteraction = InteractionUser.builder()
                    .userId("")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            assertThatThrownBy(() -> interactionDomainService.validateInteractionRules(invalidInteraction))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }

        @Test
        @DisplayName("공백 userId를 가진 상호작용 검증 시 예외가 발생한다")
        void validateInteractionWithBlankUserIdThrowsException() {
            InteractionUser invalidInteraction = InteractionUser.builder()
                    .userId("   ")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            assertThatThrownBy(() -> interactionDomainService.validateInteractionRules(invalidInteraction))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }

        @Test
        @DisplayName("null postId를 가진 상호작용 검증 시 예외가 발생한다")
        void validateInteractionWithNullPostIdThrowsException() {
            InteractionUser invalidInteraction = InteractionUser.builder()
                    .userId("user123")
                    .postId(null)
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            assertThatThrownBy(() -> interactionDomainService.validateInteractionRules(invalidInteraction))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }

        @Test
        @DisplayName("빈 postId를 가진 상호작용 검증 시 예외가 발생한다")
        void validateInteractionWithEmptyPostIdThrowsException() {
            InteractionUser invalidInteraction = InteractionUser.builder()
                    .userId("user123")
                    .postId("")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            assertThatThrownBy(() -> interactionDomainService.validateInteractionRules(invalidInteraction))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }

        @Test
        @DisplayName("null contentType을 가진 상호작용 검증 시 예외가 발생한다")
        void validateInteractionWithNullContentTypeThrowsException() {
            InteractionUser invalidInteraction = InteractionUser.builder()
                    .userId("user123")
                    .postId("post456")
                    .contentType(null)
                    .interactionType(InteractionType.LIKE)
                    .build();

            assertThatThrownBy(() -> interactionDomainService.validateInteractionRules(invalidInteraction))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }

        @Test
        @DisplayName("null interactionType을 가진 상호작용 검증 시 예외가 발생한다")
        void validateInteractionWithNullInteractionTypeThrowsException() {
            InteractionUser invalidInteraction = InteractionUser.builder()
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(null)
                    .build();

            assertThatThrownBy(() -> interactionDomainService.validateInteractionRules(invalidInteraction))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }
    }

    @Nested
    @DisplayName("사용자 상호작용 가능 여부 테스트")
    class CanUserInteractTest {

        @Test
        @DisplayName("유효한 파라미터로 사용자 상호작용이 가능하다")
        void canUserInteractWithValidParameters() {
            boolean result = interactionDomainService.canUserInteract(
                    "user123", 
                    "post456", 
                    InteractionType.LIKE, 
                    ContentType.POST
            );

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("null userId로 사용자 상호작용이 불가능하다")
        void cannotUserInteractWithNullUserId() {
            boolean result = interactionDomainService.canUserInteract(
                    null, 
                    "post456", 
                    InteractionType.LIKE, 
                    ContentType.POST
            );

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("빈 userId로 사용자 상호작용이 불가능하다")
        void cannotUserInteractWithEmptyUserId() {
            boolean result = interactionDomainService.canUserInteract(
                    "", 
                    "post456", 
                    InteractionType.LIKE, 
                    ContentType.POST
            );

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("공백 userId로 사용자 상호작용이 불가능하다")
        void cannotUserInteractWithBlankUserId() {
            boolean result = interactionDomainService.canUserInteract(
                    "   ", 
                    "post456", 
                    InteractionType.LIKE, 
                    ContentType.POST
            );

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("null postId로 사용자 상호작용이 불가능하다")
        void cannotUserInteractWithNullPostId() {
            boolean result = interactionDomainService.canUserInteract(
                    "user123", 
                    null, 
                    InteractionType.LIKE, 
                    ContentType.POST
            );

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("빈 postId로 사용자 상호작용이 불가능하다")
        void cannotUserInteractWithEmptyPostId() {
            boolean result = interactionDomainService.canUserInteract(
                    "user123", 
                    "", 
                    InteractionType.LIKE, 
                    ContentType.POST
            );

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("null interactionType으로 사용자 상호작용이 불가능하다")
        void cannotUserInteractWithNullInteractionType() {
            boolean result = interactionDomainService.canUserInteract(
                    "user123", 
                    "post456", 
                    null, 
                    ContentType.POST
            );

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("null contentType으로 사용자 상호작용이 불가능하다")
        void cannotUserInteractWithNullContentType() {
            boolean result = interactionDomainService.canUserInteract(
                    "user123", 
                    "post456", 
                    InteractionType.LIKE, 
                    null
            );

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("사용자 권한 검증 테스트")
    class ValidateUserPermissionsTest {

        @Test
        @DisplayName("유효한 userId와 postId로 권한 검증이 성공한다")
        void validateUserPermissionsSuccess() {
            interactionDomainService.validateUserPermissions("user123", "post456");
        }

        @Test
        @DisplayName("null userId로 권한 검증 시 예외가 발생한다")
        void validateUserPermissionsWithNullUserIdThrowsException() {
            assertThatThrownBy(() -> interactionDomainService.validateUserPermissions(null, "post456"))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }

        @Test
        @DisplayName("빈 userId로 권한 검증 시 예외가 발생한다")
        void validateUserPermissionsWithEmptyUserIdThrowsException() {
            assertThatThrownBy(() -> interactionDomainService.validateUserPermissions("", "post456"))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }

        @Test
        @DisplayName("공백 userId로 권한 검증 시 예외가 발생한다")
        void validateUserPermissionsWithBlankUserIdThrowsException() {
            assertThatThrownBy(() -> interactionDomainService.validateUserPermissions("   ", "post456"))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }
    }

    @Nested
    @DisplayName("게시물 존재 검증 테스트")
    class ValidatePostExistsTest {

        @Test
        @DisplayName("유효한 postId와 contentType으로 게시물 존재 검증이 성공한다")
        void validatePostExistsSuccess() {
            interactionDomainService.validatePostExists("post456", ContentType.POST);
        }

        @Test
        @DisplayName("null postId로 게시물 존재 검증 시 예외가 발생한다")
        void validatePostExistsWithNullPostIdThrowsException() {
            assertThatThrownBy(() -> interactionDomainService.validatePostExists(null, ContentType.POST))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }

        @Test
        @DisplayName("빈 postId로 게시물 존재 검증 시 예외가 발생한다")
        void validatePostExistsWithEmptyPostIdThrowsException() {
            assertThatThrownBy(() -> interactionDomainService.validatePostExists("", ContentType.POST))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }

        @Test
        @DisplayName("공백 postId로 게시물 존재 검증 시 예외가 발생한다")
        void validatePostExistsWithBlankPostIdThrowsException() {
            assertThatThrownBy(() -> interactionDomainService.validatePostExists("   ", ContentType.POST))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }

        @Test
        @DisplayName("null contentType으로 게시물 존재 검증 시 예외가 발생한다")
        void validatePostExistsWithNullContentTypeThrowsException() {
            assertThatThrownBy(() -> interactionDomainService.validatePostExists("post456", null))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }
    }

    @Nested
    @DisplayName("상호작용 제한 검증 테스트")
    class ValidateInteractionLimitsTest {

        @Test
        @DisplayName("유효한 userId와 interactionType으로 제한 검증이 성공한다")
        void validateInteractionLimitsSuccess() {
            interactionDomainService.validateInteractionLimits("user123", InteractionType.LIKE);
        }

        @Test
        @DisplayName("null userId로 제한 검증 시 예외가 발생한다")
        void validateInteractionLimitsWithNullUserIdThrowsException() {
            assertThatThrownBy(() -> interactionDomainService.validateInteractionLimits(null, InteractionType.LIKE))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }

        @Test
        @DisplayName("빈 userId로 제한 검증 시 예외가 발생한다")
        void validateInteractionLimitsWithEmptyUserIdThrowsException() {
            assertThatThrownBy(() -> interactionDomainService.validateInteractionLimits("", InteractionType.LIKE))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }

        @Test
        @DisplayName("공백 userId로 제한 검증 시 예외가 발생한다")
        void validateInteractionLimitsWithBlankUserIdThrowsException() {
            assertThatThrownBy(() -> interactionDomainService.validateInteractionLimits("   ", InteractionType.LIKE))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }

        @Test
        @DisplayName("null interactionType으로 제한 검증 시 예외가 발생한다")
        void validateInteractionLimitsWithNullInteractionTypeThrowsException() {
            assertThatThrownBy(() -> interactionDomainService.validateInteractionLimits("user123", null))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());
        }
    }

    @Nested
    @DisplayName("도메인 서비스 통합 테스트")
    class DomainServiceIntegrationTest {

        @Test
        @DisplayName("모든 검증 메서드가 유효한 데이터에 대해 성공한다")
        void allValidationMethodsSucceedWithValidData() {
            InteractionUser validInteraction = InteractionUser.builder()
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            interactionDomainService.validateInteractionRules(validInteraction);
            interactionDomainService.validateUserPermissions(validInteraction.userId(), validInteraction.postId());
            interactionDomainService.validatePostExists(validInteraction.postId(), validInteraction.contentType());
            interactionDomainService.validateInteractionLimits(validInteraction.userId(), validInteraction.interactionType());

            boolean canInteract = interactionDomainService.canUserInteract(
                    validInteraction.userId(),
                    validInteraction.postId(),
                    validInteraction.interactionType(),
                    validInteraction.contentType()
            );

            assertThat(canInteract).isTrue();
        }

        @Test
        @DisplayName("다양한 ContentType에 대해 검증이 성공한다")
        void validationSucceedsForVariousContentTypes() {
            ContentType[] contentTypes = {ContentType.POST, ContentType.COMMENT};

            for (ContentType contentType : contentTypes) {
                InteractionUser interaction = InteractionUser.builder()
                        .userId("user123")
                        .postId("post456")
                        .contentType(contentType)
                        .interactionType(InteractionType.LIKE)
                        .build();

                interactionDomainService.validateInteractionRules(interaction);
                interactionDomainService.validatePostExists(interaction.postId(), contentType);

                boolean canInteract = interactionDomainService.canUserInteract(
                        interaction.userId(),
                        interaction.postId(),
                        interaction.interactionType(),
                        contentType
                );

                assertThat(canInteract).isTrue();
            }
        }

        @Test
        @DisplayName("다양한 InteractionType에 대해 검증이 성공한다")
        void validationSucceedsForVariousInteractionTypes() {
            InteractionType[] interactionTypes = {InteractionType.LIKE, InteractionType.BOOKMARK};

            for (InteractionType interactionType : interactionTypes) {
                InteractionUser interaction = InteractionUser.builder()
                        .userId("user123")
                        .postId("post456")
                        .contentType(ContentType.POST)
                        .interactionType(interactionType)
                        .build();

                interactionDomainService.validateInteractionRules(interaction);
                interactionDomainService.validateInteractionLimits(interaction.userId(), interactionType);

                boolean canInteract = interactionDomainService.canUserInteract(
                        interaction.userId(),
                        interaction.postId(),
                        interactionType,
                        interaction.contentType()
                );

                assertThat(canInteract).isTrue();
            }
        }
    }
}