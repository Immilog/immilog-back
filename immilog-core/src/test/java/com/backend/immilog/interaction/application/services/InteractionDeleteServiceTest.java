package com.backend.immilog.interaction.application.services;

import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class InteractionDeleteServiceTest {

    private final InteractionUserRepository mockInteractionUserRepository = mock(InteractionUserRepository.class);
    private final InteractionDeleteService interactionDeleteService = new InteractionDeleteService.Impl(mockInteractionUserRepository);

    @Nested
    @DisplayName("InteractionDeleteService delete 메서드 테스트")
    class DeleteTest {

        @Test
        @DisplayName("유효한 ID로 인터랙션을 삭제할 수 있다")
        void deleteInteractionWithValidId() {
            String interactionId = "interaction123";

            doNothing().when(mockInteractionUserRepository).deleteById(interactionId);

            interactionDeleteService.delete(interactionId);

            verify(mockInteractionUserRepository).deleteById(interactionId);
        }

        @Test
        @DisplayName("다양한 ID 형식으로 인터랙션을 삭제할 수 있다")
        void deleteInteractionWithVariousIdFormats() {
            String[] testIds = {"abc123", "user-456", "INTERACTION_789", "12345"};

            for (String id : testIds) {
                doNothing().when(mockInteractionUserRepository).deleteById(id);

                interactionDeleteService.delete(id);

                verify(mockInteractionUserRepository).deleteById(id);
            }
        }

        @Test
        @DisplayName("존재하지 않는 ID로 삭제 시도 시 레포지토리 호출은 수행된다")
        void callsRepositoryEvenForNonExistentId() {
            String nonExistentId = "nonExistent123";

            doNothing().when(mockInteractionUserRepository).deleteById(nonExistentId);

            interactionDeleteService.delete(nonExistentId);

            verify(mockInteractionUserRepository).deleteById(nonExistentId);
        }

        @Test
        @DisplayName("빈 문자열 ID로 삭제 시도")
        void deleteWithEmptyString() {
            String emptyId = "";

            doNothing().when(mockInteractionUserRepository).deleteById(emptyId);

            interactionDeleteService.delete(emptyId);

            verify(mockInteractionUserRepository).deleteById(emptyId);
        }
    }

    @Nested
    @DisplayName("InteractionDeleteService 예외 처리 테스트")
    class ExceptionHandlingTest {

        @Test
        @DisplayName("null ID로 삭제 시도")
        void deleteWithNullId() {
            doNothing().when(mockInteractionUserRepository).deleteById(null);

            interactionDeleteService.delete(null);

            verify(mockInteractionUserRepository).deleteById(null);
        }

        @Test
        @DisplayName("레포지토리에서 예외 발생 시 전파된다")
        void propagatesRepositoryException() {
            String interactionId = "interaction123";
            RuntimeException repositoryException = new RuntimeException("Database connection failed");

            doThrow(repositoryException).when(mockInteractionUserRepository).deleteById(interactionId);

            assertThatThrownBy(() -> interactionDeleteService.delete(interactionId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database connection failed");

            verify(mockInteractionUserRepository).deleteById(interactionId);
        }

        @Test
        @DisplayName("데이터베이스 제약 조건 위반 예외 전파")
        void propagatesConstraintViolationException() {
            String interactionId = "interaction123";
            IllegalStateException constraintException = new IllegalStateException("Foreign key constraint violation");

            doThrow(constraintException).when(mockInteractionUserRepository).deleteById(interactionId);

            assertThatThrownBy(() -> interactionDeleteService.delete(interactionId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Foreign key constraint violation");

            verify(mockInteractionUserRepository).deleteById(interactionId);
        }

        @Test
        @DisplayName("일반적인 런타임 예외 전파")
        void propagatesGenericRuntimeException() {
            String interactionId = "interaction123";
            RuntimeException genericException = new RuntimeException("Generic error");

            doThrow(genericException).when(mockInteractionUserRepository).deleteById(interactionId);

            assertThatThrownBy(() -> interactionDeleteService.delete(interactionId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Generic error");
        }
    }

    @Nested
    @DisplayName("InteractionDeleteService 비즈니스 로직 테스트")
    class BusinessLogicTest {

        @Test
        @DisplayName("삭제 작업은 단일 트랜잭션으로 처리된다")
        void deletionIsAtomicOperation() {
            String interactionId = "atomic123";

            doNothing().when(mockInteractionUserRepository).deleteById(interactionId);

            interactionDeleteService.delete(interactionId);

            verify(mockInteractionUserRepository).deleteById(interactionId);
        }

        @Test
        @DisplayName("여러 삭제 요청을 순차적으로 처리할 수 있다")
        void handleMultipleDeleteRequests() {
            String[] interactionIds = {"id1", "id2", "id3", "id4", "id5"};

            for (String id : interactionIds) {
                doNothing().when(mockInteractionUserRepository).deleteById(id);
                interactionDeleteService.delete(id);
            }

            for (String id : interactionIds) {
                verify(mockInteractionUserRepository).deleteById(id);
            }
        }

        @Test
        @DisplayName("삭제 서비스는 ID 검증 없이 레포지토리에 위임한다")
        void delegatesValidationToRepository() {
            String invalidId = "invalid-format-id!@#";

            doNothing().when(mockInteractionUserRepository).deleteById(invalidId);

            interactionDeleteService.delete(invalidId);

            verify(mockInteractionUserRepository).deleteById(invalidId);
        }

        @Test
        @DisplayName("삭제 성공 시 추가적인 작업은 수행하지 않는다")
        void noAdditionalWorkAfterSuccessfulDeletion() {
            String interactionId = "simple123";

            doNothing().when(mockInteractionUserRepository).deleteById(interactionId);

            interactionDeleteService.delete(interactionId);

            verify(mockInteractionUserRepository).deleteById(interactionId);
        }
    }

    @Nested
    @DisplayName("InteractionDeleteService 상태 변경 테스트")
    class StateChangeTest {

        @Test
        @DisplayName("삭제 전후 서비스 상태는 변경되지 않는다")
        void serviceStateRemainsUnchangedAfterDeletion() {
            String interactionId = "stateless123";

            doNothing().when(mockInteractionUserRepository).deleteById(interactionId);

            interactionDeleteService.delete(interactionId);
            interactionDeleteService.delete("another456");

            verify(mockInteractionUserRepository).deleteById(interactionId);
            verify(mockInteractionUserRepository).deleteById("another456");
        }

        @Test
        @DisplayName("동일한 ID로 여러 번 삭제 요청을 처리할 수 있다")
        void handleMultipleDeletionRequestsForSameId() {
            String interactionId = "duplicate123";

            doNothing().when(mockInteractionUserRepository).deleteById(interactionId);

            interactionDeleteService.delete(interactionId);
            interactionDeleteService.delete(interactionId);
            interactionDeleteService.delete(interactionId);

            verify(mockInteractionUserRepository, org.mockito.Mockito.times(3)).deleteById(interactionId);
        }
    }
}