package com.backend.immilog.post.application.services.command;

import com.backend.immilog.post.domain.model.resource.ContentResource;
import com.backend.immilog.post.domain.repositories.BulkInsertRepository;
import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.shared.enums.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import static com.backend.immilog.post.domain.model.resource.ResourceType.ATTACHMENT;
import static com.backend.immilog.post.domain.model.resource.ResourceType.TAG;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("BulkCommandService")
class BulkCommandServiceTest {
    private final BulkInsertRepository bulkInsertRepository = mock(BulkInsertRepository.class);
    private final BulkCommandService bulkCommandService = new BulkCommandService(bulkInsertRepository);

    private List<ContentResource> testResources;

    @BeforeEach
    void setUp() {
        testResources = createTestResources();
    }

    @Nested
    @DisplayName("일반 벌크 저장")
    class GeneralBulkSave {

        @Test
        @DisplayName("벌크 저장 성공")
        void saveAllSuccess() {
            String testCommand = "INSERT INTO test (id) VALUES (?)";
            BiConsumer<PreparedStatement, ContentResource> setter = mock(BiConsumer.class);

            bulkCommandService.saveAll(testResources, testCommand, setter);

            verify(bulkInsertRepository).saveAll(testResources, testCommand, setter);
        }

        @Test
        @DisplayName("빈 리스트 저장 시도 시 로그만 출력하고 종료")
        void saveAllWithEmptyList() {
            List<ContentResource> emptyList = Collections.emptyList();
            String testCommand = "INSERT INTO test (id) VALUES (?)";
            BiConsumer<PreparedStatement, ContentResource> setter = mock(BiConsumer.class);

            bulkCommandService.saveAll(emptyList, testCommand, setter);

            verify(bulkInsertRepository, never()).saveAll(any(), any(), any());
        }

        @Test
        @DisplayName("null 리스트 저장 시도 시 로그만 출력하고 종료")
        void saveAllWithNullList() {
            String testCommand = "INSERT INTO test (id) VALUES (?)";
            BiConsumer<PreparedStatement, ContentResource> setter = mock(BiConsumer.class);

            bulkCommandService.saveAll(null, testCommand, setter);

            verify(bulkInsertRepository, never()).saveAll(any(), any(), any());
        }

        @Test
        @DisplayName("Repository에서 예외 발생 시 PostException으로 변환")
        void saveAllWithRepositoryException() {
            String testCommand = "INSERT INTO test (id) VALUES (?)";
            BiConsumer<PreparedStatement, ContentResource> setter = mock(BiConsumer.class);
            RuntimeException repositoryException = new RuntimeException("Database error");

            doThrow(repositoryException).when(bulkInsertRepository)
                    .saveAll(testResources, testCommand, setter);

            assertThatThrownBy(() -> bulkCommandService.saveAll(testResources, testCommand, setter))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.FAILED_TO_SAVE_POST);
        }
    }

    @Nested
    @DisplayName("ContentResource 저장")
    class ContentResourceSave {

        @Test
        @DisplayName("ContentResource 저장 성공")
        void saveContentResourcesSuccess() {
            bulkCommandService.saveContentResources(testResources);

            verify(bulkInsertRepository).saveAll(
                    eq(testResources),
                    contains("INSERT INTO content_resource"),
                    any()
            );
        }

        @Test
        @DisplayName("빈 ContentResource 리스트는 저장하지 않음")
        void saveContentResourcesWithEmptyList() {
            List<ContentResource> emptyList = Collections.emptyList();

            bulkCommandService.saveContentResources(emptyList);

            verify(bulkInsertRepository, never()).saveAll(any(), any(), any());
        }

        @Test
        @DisplayName("null ContentResource 리스트는 저장하지 않음")
        void saveContentResourcesWithNullList() {
            bulkCommandService.saveContentResources(null);

            verify(bulkInsertRepository, never()).saveAll(any(), any(), any());
        }

        @Test
        @DisplayName("단일 ContentResource 저장")
        void saveSingleContentResource() {
            List<ContentResource> singleResource = List.of(
                    ContentResource.of(ContentType.POST, TAG, "single-tag", "post1")
            );

            bulkCommandService.saveContentResources(singleResource);

            verify(bulkInsertRepository).saveAll(
                    eq(singleResource),
                    anyString(),
                    any()
            );
        }

        @Test
        @DisplayName("대량의 ContentResource 저장")
        void saveManyContentResources() {
            List<ContentResource> manyResources = createManyTestResources(100);

            bulkCommandService.saveContentResources(manyResources);

            verify(bulkInsertRepository).saveAll(
                    eq(manyResources),
                    anyString(),
                    any()
            );
        }

        @Test
        @DisplayName("다양한 타입의 ContentResource 저장")
        void saveVariousContentResources() {
            List<ContentResource> variousResources = List.of(
                    ContentResource.of(ContentType.POST, TAG, "java", "post1"),
                    ContentResource.of(ContentType.POST, TAG, "spring", "post1"),
                    ContentResource.of(ContentType.POST, ATTACHMENT, "file1.jpg", "post1"),
                    ContentResource.of(ContentType.POST, ATTACHMENT, "file2.pdf", "post1"),
                    ContentResource.of(ContentType.COMMENT, TAG, "reply", "comment1")
            );

            bulkCommandService.saveContentResources(variousResources);

            verify(bulkInsertRepository).saveAll(
                    eq(variousResources),
                    anyString(),
                    any()
            );
        }
    }

    @Nested
    @DisplayName("SQL 문 검증")
    class SqlValidation {

        @Test
        @DisplayName("ContentResource 저장 시 올바른 SQL 문 사용")
        void saveContentResourcesWithCorrectSql() {
            bulkCommandService.saveContentResources(testResources);

            verify(bulkInsertRepository).saveAll(
                    eq(testResources),
                    argThat(sql ->
                            sql.contains("INSERT INTO content_resource") &&
                                    sql.contains("content_resource_id") &&
                                    sql.contains("content_id") &&
                                    sql.contains("content_type") &&
                                    sql.contains("resource_type") &&
                                    sql.contains("content")
                    ),
                    any()
            );
        }
    }

    @Nested
    @DisplayName("예외 처리")
    class ExceptionHandling {

        @Test
        @DisplayName("ContentResource 저장 중 예외 발생 시 PostException으로 래핑")
        void saveContentResourcesWithException() {
            RuntimeException repositoryException = new RuntimeException("SQL error");
            doThrow(repositoryException).when(bulkInsertRepository)
                    .saveAll(any(), any(), any());

            assertThatThrownBy(() -> bulkCommandService.saveContentResources(testResources))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.FAILED_TO_SAVE_POST);
        }

        @Test
        @DisplayName("SQL 예외 발생 시에도 PostException으로 변환")
        void saveContentResourcesWithSqlException() {
            RuntimeException sqlException = new RuntimeException("SQL constraint violation");
            doThrow(sqlException).when(bulkInsertRepository)
                    .saveAll(any(), any(), any());

            assertThatThrownBy(() -> bulkCommandService.saveContentResources(testResources))
                    .isInstanceOf(PostException.class)
                    .extracting("errorCode")
                    .isEqualTo(PostErrorCode.FAILED_TO_SAVE_POST);
        }
    }

    private List<ContentResource> createTestResources() {
        return List.of(
                ContentResource.of(ContentType.POST, TAG, "java", "post1"),
                ContentResource.of(ContentType.POST, TAG, "spring", "post1"),
                ContentResource.of(ContentType.POST, ATTACHMENT, "file.jpg", "post1")
        );
    }

    private List<ContentResource> createManyTestResources(int count) {
        return java.util.stream.IntStream.range(1, count + 1)
                .mapToObj(i -> ContentResource.of(
                        ContentType.POST,
                        i % 2 == 0 ? TAG : ATTACHMENT,
                        "content" + i,
                        "post" + i
                ))
                .toList();
    }
}