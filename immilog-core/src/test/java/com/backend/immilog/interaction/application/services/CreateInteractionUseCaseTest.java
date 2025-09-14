package com.backend.immilog.interaction.application.services;

import com.backend.immilog.interaction.application.dto.in.InteractionCreateCommand;
import com.backend.immilog.interaction.application.dto.out.InteractionResult;
import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.interaction.domain.service.InteractionDomainService;
import com.backend.immilog.shared.enums.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateInteractionUseCaseTest {

    private final InteractionUserCommandService mockInteractionUserCommandService = mock(InteractionUserCommandService.class);
    private final InteractionDomainService mockInteractionDomainService = mock(InteractionDomainService.class);
    private final CreateInteractionUseCase.CreatorInteraction createInteractionUseCase = new CreateInteractionUseCase.CreatorInteraction(
            mockInteractionUserCommandService,
            mockInteractionDomainService
    );

    @Nested
    @DisplayName("InteractionCreateUseCase toggleInteraction 메서드 테스트")
    class ToggleInteractionTest {

        @Test
        @DisplayName("유효한 명령으로 인터랙션을 토글할 수 있다")
        void toggleInteractionWithValidCommand() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            InteractionUser savedInteraction = InteractionUser.builder()
                    .id("interaction123")
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionDomainService.canUserInteract(
                    eq("user123"),
                    eq("post456"),
                    eq(InteractionType.LIKE),
                    eq(ContentType.POST)
            )).thenReturn(true);

            when(mockInteractionUserCommandService.toggleInteraction(any(InteractionUser.class)))
                    .thenReturn(savedInteraction);

            InteractionResult result = createInteractionUseCase.toggleInteraction(command);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo("interaction123");
            assertThat(result.userId()).isEqualTo("user123");
            assertThat(result.postId()).isEqualTo("post456");
            assertThat(result.contentType()).isEqualTo(ContentType.POST);
            assertThat(result.interactionType()).isEqualTo(InteractionType.LIKE);
            assertThat(result.interactionStatus()).isEqualTo(InteractionStatus.ACTIVE);

            verify(mockInteractionDomainService).canUserInteract(
                    "user123",
                    "post456",
                    InteractionType.LIKE,
                    ContentType.POST
            );
            verify(mockInteractionUserCommandService).toggleInteraction(any(InteractionUser.class));
        }

        @Test
        @DisplayName("북마크 인터랙션을 토글할 수 있다")
        void toggleBookmarkInteraction() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId("user456")
                    .postId("post789")
                    .contentType(ContentType.COMMENT)
                    .interactionType(InteractionType.BOOKMARK)
                    .build();

            InteractionUser savedInteraction = InteractionUser.builder()
                    .id("bookmark456")
                    .userId("user456")
                    .postId("post789")
                    .contentType(ContentType.COMMENT)
                    .interactionType(InteractionType.BOOKMARK)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionDomainService.canUserInteract(
                    eq("user456"),
                    eq("post789"),
                    eq(InteractionType.BOOKMARK),
                    eq(ContentType.COMMENT)
            )).thenReturn(true);

            when(mockInteractionUserCommandService.toggleInteraction(any(InteractionUser.class)))
                    .thenReturn(savedInteraction);

            InteractionResult result = createInteractionUseCase.toggleInteraction(command);

            assertThat(result.interactionType()).isEqualTo(InteractionType.BOOKMARK);
            assertThat(result.contentType()).isEqualTo(ContentType.COMMENT);

            verify(mockInteractionDomainService).canUserInteract(
                    "user456",
                    "post789",
                    InteractionType.BOOKMARK,
                    ContentType.COMMENT
            );
        }

        @Test
        @DisplayName("사용자가 인터랙션할 수 없는 경우 예외가 발생한다")
        void throwsExceptionWhenUserCannotInteract() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            when(mockInteractionDomainService.canUserInteract(
                    eq("user123"),
                    eq("post456"),
                    eq(InteractionType.LIKE),
                    eq(ContentType.POST)
            )).thenReturn(false);

            assertThatThrownBy(() -> createInteractionUseCase.toggleInteraction(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User cannot interact with this content");

            verify(mockInteractionDomainService).canUserInteract(
                    "user123",
                    "post456",
                    InteractionType.LIKE,
                    ContentType.POST
            );
        }

        @Test
        @DisplayName("도메인 서비스 검증 실패 시 인터랙션 명령 서비스가 호출되지 않는다")
        void doesNotCallCommandServiceWhenValidationFails() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            when(mockInteractionDomainService.canUserInteract(
                    eq("user123"),
                    eq("post456"),
                    eq(InteractionType.LIKE),
                    eq(ContentType.POST)
            )).thenReturn(false);

            assertThatThrownBy(() -> createInteractionUseCase.toggleInteraction(command))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(mockInteractionUserCommandService, org.mockito.Mockito.never())
                    .toggleInteraction(any(InteractionUser.class));
        }
    }

    @Nested
    @DisplayName("InteractionCreateUseCase 통합 시나리오 테스트")
    class IntegrationScenarioTest {

        @Test
        @DisplayName("새로운 좋아요 인터랙션 생성 시나리오")
        void createNewLikeInteractionScenario() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId("newUser")
                    .postId("newPost")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            InteractionUser createdInteraction = InteractionUser.builder()
                    .id("newLike123")
                    .userId("newUser")
                    .postId("newPost")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionDomainService.canUserInteract(
                    "newUser", "newPost", InteractionType.LIKE, ContentType.POST
            )).thenReturn(true);

            when(mockInteractionUserCommandService.toggleInteraction(any(InteractionUser.class)))
                    .thenReturn(createdInteraction);

            InteractionResult result = createInteractionUseCase.toggleInteraction(command);

            assertThat(result.id()).isEqualTo("newLike123");
            assertThat(result.userId()).isEqualTo("newUser");
            assertThat(result.postId()).isEqualTo("newPost");
            assertThat(result.interactionStatus()).isEqualTo(InteractionStatus.ACTIVE);
        }

        @Test
        @DisplayName("기존 인터랙션 토글 시나리오")
        void toggleExistingInteractionScenario() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId("existingUser")
                    .postId("existingPost")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            InteractionUser toggledInteraction = InteractionUser.builder()
                    .id("existing123")
                    .userId("existingUser")
                    .postId("existingPost")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .interactionStatus(InteractionStatus.INACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mockInteractionDomainService.canUserInteract(
                    "existingUser", "existingPost", InteractionType.LIKE, ContentType.POST
            )).thenReturn(true);

            when(mockInteractionUserCommandService.toggleInteraction(any(InteractionUser.class)))
                    .thenReturn(toggledInteraction);

            InteractionResult result = createInteractionUseCase.toggleInteraction(command);

            assertThat(result.interactionStatus()).isEqualTo(InteractionStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("InteractionCreateUseCase 에러 처리 테스트")
    class ErrorHandlingTest {

        @Test
        @DisplayName("null 명령으로 호출 시 NullPointerException 발생")
        void throwsNullPointerExceptionWithNullCommand() {
            assertThatThrownBy(() -> createInteractionUseCase.toggleInteraction(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("도메인 서비스에서 예외 발생 시 전파된다")
        void propagatesExceptionFromDomainService() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            when(mockInteractionDomainService.canUserInteract(
                    eq("user123"),
                    eq("post456"),
                    eq(InteractionType.LIKE),
                    eq(ContentType.POST)
            )).thenThrow(new RuntimeException("Domain service error"));

            assertThatThrownBy(() -> createInteractionUseCase.toggleInteraction(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Domain service error");
        }

        @Test
        @DisplayName("명령 서비스에서 예외 발생 시 전파된다")
        void propagatesExceptionFromCommandService() {
            InteractionCreateCommand command = InteractionCreateCommand.builder()
                    .userId("user123")
                    .postId("post456")
                    .contentType(ContentType.POST)
                    .interactionType(InteractionType.LIKE)
                    .build();

            when(mockInteractionDomainService.canUserInteract(
                    eq("user123"),
                    eq("post456"),
                    eq(InteractionType.LIKE),
                    eq(ContentType.POST)
            )).thenReturn(true);

            when(mockInteractionUserCommandService.toggleInteraction(any(InteractionUser.class)))
                    .thenThrow(new RuntimeException("Command service error"));

            assertThatThrownBy(() -> createInteractionUseCase.toggleInteraction(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Command service error");
        }
    }
}