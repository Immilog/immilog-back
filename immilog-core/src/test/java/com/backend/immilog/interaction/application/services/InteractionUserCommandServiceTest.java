package com.backend.immilog.interaction.application.services;

import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import com.backend.immilog.interaction.domain.service.InteractionDomainService;
import com.backend.immilog.interaction.exception.InteractionErrorCode;
import com.backend.immilog.interaction.exception.InteractionException;
import com.backend.immilog.shared.enums.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InteractionUserCommandServiceTest {

    private final InteractionUserRepository mockInteractionUserRepository = mock(InteractionUserRepository.class);
    private final InteractionDomainService mockInteractionDomainService = mock(InteractionDomainService.class);
    private final InteractionUserCommandService interactionUserCommandService = new InteractionUserCommandService(
            mockInteractionUserRepository,
            mockInteractionDomainService
    );

    @Nested
    @DisplayName("상호작용 토글 테스트")
    class ToggleInteractionTest {

        @Test
        @DisplayName("기존 상호작용이 없을 때 새로 생성한다")
        void createNewInteractionWhenNotExists() {
            InteractionUser newInteraction = InteractionUser.createLike("user123", "post456", ContentType.POST);
            InteractionUser savedInteraction = InteractionUser.builder()
                    .id("interaction123")
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionUserRepository.findByUserIdAndInteractionTypeAndContentTypeAndPostId(
                    "user123", InteractionType.LIKE, ContentType.POST, "post456"
            )).thenReturn(Optional.empty());
            when(mockInteractionUserRepository.save(newInteraction)).thenReturn(savedInteraction);

            InteractionUser result = interactionUserCommandService.toggleInteraction(newInteraction);

            assertThat(result).isEqualTo(savedInteraction);
            verify(mockInteractionDomainService).validateInteractionRules(newInteraction);
            verify(mockInteractionDomainService).validateUserPermissions("user123", "post456");
            verify(mockInteractionDomainService).validatePostExists("post456", ContentType.POST);
            verify(mockInteractionUserRepository).findByUserIdAndInteractionTypeAndContentTypeAndPostId(
                    "user123", InteractionType.LIKE, ContentType.POST, "post456"
            );
            verify(mockInteractionUserRepository).save(newInteraction);
        }

        @Test
        @DisplayName("기존 상호작용이 있을 때 상태를 토글한다")
        void toggleExistingInteractionStatus() {
            InteractionUser newInteraction = InteractionUser.createLike("user123", "post456", ContentType.POST);
            InteractionUser existingInteraction = InteractionUser.builder()
                    .id("interaction123")
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();
            InteractionUser toggledInteraction = existingInteraction.toggleStatus();
            InteractionUser savedInteraction = InteractionUser.builder()
                    .id("interaction123")
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.INACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionUserRepository.findByUserIdAndInteractionTypeAndContentTypeAndPostId(
                    "user123", InteractionType.LIKE, ContentType.POST, "post456"
            )).thenReturn(Optional.of(existingInteraction));
            when(mockInteractionUserRepository.save(any(InteractionUser.class))).thenReturn(savedInteraction);

            InteractionUser result = interactionUserCommandService.toggleInteraction(newInteraction);

            assertThat(result).isEqualTo(savedInteraction);
            verify(mockInteractionDomainService).validateInteractionRules(newInteraction);
            verify(mockInteractionDomainService).validateUserPermissions("user123", "post456");
            verify(mockInteractionDomainService).validatePostExists("post456", ContentType.POST);
            verify(mockInteractionUserRepository).findByUserIdAndInteractionTypeAndContentTypeAndPostId(
                    "user123", InteractionType.LIKE, ContentType.POST, "post456"
            );
            verify(mockInteractionUserRepository).save(any(InteractionUser.class));
        }

        @Test
        @DisplayName("도메인 서비스 검증 실패 시 예외가 발생한다")
        void throwsExceptionWhenDomainValidationFails() {
            InteractionUser invalidInteraction = InteractionUser.createLike("", "post456", ContentType.POST);

            doThrow(new InteractionException(InteractionErrorCode.INTERACTION_CREATE_FAILED))
                    .when(mockInteractionDomainService).validateInteractionRules(invalidInteraction);

            assertThatThrownBy(() -> interactionUserCommandService.toggleInteraction(invalidInteraction))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());

            verify(mockInteractionDomainService).validateInteractionRules(invalidInteraction);
            verifyNoInteractions(mockInteractionUserRepository);
        }
    }

    @Nested
    @DisplayName("상호작용 삭제 테스트")
    class DeleteInteractionTest {

        @Test
        @DisplayName("상호작용을 삭제할 수 있다")
        void deleteInteractionSuccessfully() {
            String interactionId = "interaction123";

            interactionUserCommandService.deleteInteraction(interactionId);

            verify(mockInteractionUserRepository).deleteById(interactionId);
        }

        @Test
        @DisplayName("null ID로 상호작용 삭제를 시도할 수 있다")
        void deleteInteractionWithNullId() {
            String interactionId = null;

            interactionUserCommandService.deleteInteraction(interactionId);

            verify(mockInteractionUserRepository).deleteById(interactionId);
        }
    }

    @Nested
    @DisplayName("상호작용 생성 테스트")
    class CreateInteractionTest {

        @Test
        @DisplayName("새로운 상호작용을 생성할 수 있다")
        void createInteractionSuccessfully() {
            InteractionUser newInteraction = InteractionUser.createBookmark("user123", "post456", ContentType.POST);
            InteractionUser savedInteraction = InteractionUser.builder()
                    .id("interaction123")
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.BOOKMARK)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionUserRepository.save(newInteraction)).thenReturn(savedInteraction);

            InteractionUser result = interactionUserCommandService.createInteraction(newInteraction);

            assertThat(result).isEqualTo(savedInteraction);
            verify(mockInteractionDomainService).validateInteractionRules(newInteraction);
            verify(mockInteractionDomainService).validateUserPermissions("user123", "post456");
            verify(mockInteractionDomainService).validatePostExists("post456", ContentType.POST);
            verify(mockInteractionDomainService).validateInteractionLimits("user123", InteractionType.BOOKMARK);
            verify(mockInteractionUserRepository).save(newInteraction);
        }

        @Test
        @DisplayName("도메인 서비스 검증 실패 시 예외가 발생한다")
        void throwsExceptionWhenCreateValidationFails() {
            InteractionUser invalidInteraction = InteractionUser.createBookmark(null, "post456", ContentType.POST);

            doThrow(new InteractionException(InteractionErrorCode.INTERACTION_CREATE_FAILED))
                    .when(mockInteractionDomainService).validateInteractionRules(invalidInteraction);

            assertThatThrownBy(() -> interactionUserCommandService.createInteraction(invalidInteraction))
                    .isInstanceOf(InteractionException.class)
                    .hasMessage(InteractionErrorCode.INTERACTION_CREATE_FAILED.getMessage());

            verify(mockInteractionDomainService).validateInteractionRules(invalidInteraction);
            verifyNoInteractions(mockInteractionUserRepository);
        }
    }

    @Nested
    @DisplayName("상호작용 활성화 테스트")
    class ActivateInteractionTest {

        @Test
        @DisplayName("비활성 상호작용을 활성화할 수 있다")
        void activateInactiveInteraction() {
            String interactionId = "interaction123";
            InteractionUser inactiveInteraction = InteractionUser.builder()
                    .id(interactionId)
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.INACTIVE)
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .build();

            InteractionUser activatedInteraction = InteractionUser.builder()
                    .id(interactionId)
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionUserRepository.findById(interactionId)).thenReturn(Optional.of(inactiveInteraction));
            when(mockInteractionUserRepository.save(any(InteractionUser.class))).thenReturn(activatedInteraction);

            InteractionUser result = interactionUserCommandService.activateInteraction(interactionId);

            assertThat(result).isEqualTo(activatedInteraction);
            verify(mockInteractionUserRepository).findById(interactionId);
            verify(mockInteractionUserRepository).save(any(InteractionUser.class));
        }

        @Test
        @DisplayName("이미 활성화된 상호작용은 그대로 반환한다")
        void returnActiveInteractionAsIs() {
            String interactionId = "interaction123";
            InteractionUser activeInteraction = InteractionUser.builder()
                    .id(interactionId)
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionUserRepository.findById(interactionId)).thenReturn(Optional.of(activeInteraction));

            InteractionUser result = interactionUserCommandService.activateInteraction(interactionId);

            assertThat(result).isEqualTo(activeInteraction);
            verify(mockInteractionUserRepository).findById(interactionId);
            verify(mockInteractionUserRepository, never()).save(any(InteractionUser.class));
        }

        @Test
        @DisplayName("존재하지 않는 상호작용 활성화 시 예외가 발생한다")
        void throwsExceptionWhenActivatingNonExistentInteraction() {
            String interactionId = "nonexistent";

            when(mockInteractionUserRepository.findById(interactionId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> interactionUserCommandService.activateInteraction(interactionId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Interaction not found: " + interactionId);

            verify(mockInteractionUserRepository).findById(interactionId);
            verify(mockInteractionUserRepository, never()).save(any(InteractionUser.class));
        }
    }

    @Nested
    @DisplayName("상호작용 비활성화 테스트")
    class DeactivateInteractionTest {

        @Test
        @DisplayName("활성 상호작용을 비활성화할 수 있다")
        void deactivateActiveInteraction() {
            String interactionId = "interaction123";
            InteractionUser activeInteraction = InteractionUser.builder()
                    .id(interactionId)
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .build();

            InteractionUser deactivatedInteraction = InteractionUser.builder()
                    .id(interactionId)
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.INACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionUserRepository.findById(interactionId)).thenReturn(Optional.of(activeInteraction));
            when(mockInteractionUserRepository.save(any(InteractionUser.class))).thenReturn(deactivatedInteraction);

            InteractionUser result = interactionUserCommandService.deactivateInteraction(interactionId);

            assertThat(result).isEqualTo(deactivatedInteraction);
            verify(mockInteractionUserRepository).findById(interactionId);
            verify(mockInteractionUserRepository).save(any(InteractionUser.class));
        }

        @Test
        @DisplayName("이미 비활성화된 상호작용은 그대로 반환한다")
        void returnInactiveInteractionAsIs() {
            String interactionId = "interaction123";
            InteractionUser inactiveInteraction = InteractionUser.builder()
                    .id(interactionId)
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.INACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionUserRepository.findById(interactionId)).thenReturn(Optional.of(inactiveInteraction));

            InteractionUser result = interactionUserCommandService.deactivateInteraction(interactionId);

            assertThat(result).isEqualTo(inactiveInteraction);
            verify(mockInteractionUserRepository).findById(interactionId);
            verify(mockInteractionUserRepository, never()).save(any(InteractionUser.class));
        }

        @Test
        @DisplayName("존재하지 않는 상호작용 비활성화 시 예외가 발생한다")
        void throwsExceptionWhenDeactivatingNonExistentInteraction() {
            String interactionId = "nonexistent";

            when(mockInteractionUserRepository.findById(interactionId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> interactionUserCommandService.deactivateInteraction(interactionId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Interaction not found: " + interactionId);

            verify(mockInteractionUserRepository).findById(interactionId);
            verify(mockInteractionUserRepository, never()).save(any(InteractionUser.class));
        }
    }

    @Nested
    @DisplayName("서비스 통합 테스트")
    class ServiceIntegrationTest {

        @Test
        @DisplayName("다양한 타입의 상호작용을 처리할 수 있다")
        void handleVariousInteractionTypes() {
            InteractionType[] types = {InteractionType.LIKE, InteractionType.BOOKMARK};
            
            for (InteractionType type : types) {
                InteractionUser interaction = InteractionUser.of("user123", "post456", ContentType.POST, type);
                InteractionUser savedInteraction = InteractionUser.builder()
                        .id("interaction123")
                        .userId("user123")
                        .postId("post456")
                        .contentType(ContentType.POST)
                        .interactionType(type)
                        .interactionStatus(InteractionStatus.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .build();

                when(mockInteractionUserRepository.save(interaction)).thenReturn(savedInteraction);

                InteractionUser result = interactionUserCommandService.createInteraction(interaction);

                assertThat(result.interactionType()).isEqualTo(type);
                verify(mockInteractionDomainService).validateInteractionRules(interaction);
                verify(mockInteractionUserRepository).save(interaction);
            }
        }

        @Test
        @DisplayName("다양한 콘텐츠 타입의 상호작용을 처리할 수 있다")
        void handleVariousContentTypes() {
            ContentType[] contentTypes = {ContentType.POST, ContentType.COMMENT};
            
            for (ContentType contentType : contentTypes) {
                InteractionUser interaction = InteractionUser.of("user123", "post456", contentType, InteractionType.LIKE);
                InteractionUser savedInteraction = InteractionUser.builder()
                        .id("interaction123")
                        .userId("user123")
                        .postId("post456")
                        .contentType(contentType)
                        .interactionType(InteractionType.LIKE)
                        .interactionStatus(InteractionStatus.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .build();

                when(mockInteractionUserRepository.save(interaction)).thenReturn(savedInteraction);

                InteractionUser result = interactionUserCommandService.createInteraction(interaction);

                assertThat(result.contentType()).isEqualTo(contentType);
                verify(mockInteractionDomainService).validatePostExists("post456", contentType);
                verify(mockInteractionUserRepository).save(interaction);
            }
        }
    }
}