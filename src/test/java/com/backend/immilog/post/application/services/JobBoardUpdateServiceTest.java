package com.backend.immilog.post.application.services;

import com.backend.immilog.post.application.command.JobBoardUpdateCommand;
import com.backend.immilog.post.application.result.JobBoardResult;
import com.backend.immilog.post.domain.model.PostResource;
import com.backend.immilog.post.domain.model.enums.Experience;
import com.backend.immilog.post.domain.model.enums.PostType;
import com.backend.immilog.post.domain.model.enums.ResourceType;
import com.backend.immilog.post.domain.repositories.BulkInsertRepository;
import com.backend.immilog.post.domain.repositories.JobBoardRepository;
import com.backend.immilog.post.domain.repositories.PostResourceRepository;
import com.backend.immilog.post.exception.PostException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static com.backend.immilog.post.exception.PostErrorCode.NO_AUTHORITY;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("JobBoardUpdateService 테스트")
class JobBoardUpdateServiceTest {

    @Mock
    private JobBoardRepository jobBoardRepository;

    @Mock
    private PostResourceRepository postResourceRepository;

    @Mock
    private BulkInsertRepository bulkInsertRepository;

    private JobBoardUpdateService jobBoardUpdateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jobBoardUpdateService = new JobBoardUpdateService(
                jobBoardRepository,
                postResourceRepository,
                bulkInsertRepository
        );
    }

    @Test
    @DisplayName("구인구직 업데이트 - 성공")
    void updateJobBoard_updatesTagsAndAttachments() {
        LocalDateTime now = LocalDateTime.now();
        ArgumentCaptor<BiConsumer<PreparedStatement, PostResource>> captor =
                ArgumentCaptor.forClass(BiConsumer.class);
        JobBoardUpdateCommand command = JobBoardUpdateCommand.builder()
                .title("New Title")
                .content("New Content")
                .addTags(List.of("tag1", "tag2"))
                .addAttachments(List.of("att1", "att2"))
                .deleteTags(List.of("tag3"))
                .deleteAttachments(List.of("att3"))
                .deadline(now)
                .experience(Experience.JUNIOR)
                .salary("1000")
                .build();
        JobBoardResult jobBoard = JobBoardResult.builder()
                .seq(1L)
                .title("Title")
                .content("Content")
                .viewCount(0L)
                .likeCount(0L)
                .tags(List.of("tag1", "tag3"))
                .attachments(List.of("att1", "att3"))
                .likeUsers(List.of())
                .bookmarkUsers(List.of())
                .country(null)
                .region(null)
                .industry(null)
                .deadline(null)
                .experience(null)
                .salary(null)
                .companySeq(1L)
                .companyName(null)
                .companyEmail(null)
                .companyPhone(null)
                .companyAddress(null)
                .companyHomepage(null)
                .companyLogo(null)
                .companyManagerUserSeq(1L)
                .status(null)
                .createdAt(null)
                .build();
        when(jobBoardRepository.getJobBoardBySeq(anyLong())).thenReturn(Optional.of(jobBoard));
        jobBoardUpdateService.updateJobBoard(1L, 1L, command);
        verify(postResourceRepository).deleteAllEntities(anyLong(), eq(PostType.JOB_BOARD), eq(ResourceType.TAG), anyList());
        verify(postResourceRepository).deleteAllEntities(anyLong(), eq(PostType.JOB_BOARD), eq(ResourceType.ATTACHMENT), anyList());
        verify(bulkInsertRepository, times(2)).saveAll(anyList(), anyString(), any());
        verify(bulkInsertRepository, times(2)).saveAll(
                anyList(),
                anyString(),
                captor.capture()
        );
    }

    @Test
    @DisplayName("구인구직 업데이트 - 실패 : 매니저 아닌 경우")
    void updateJobBoard_throwsExceptionIfUserIsNotOwner() {
        LocalDateTime now = LocalDateTime.now();
        JobBoardUpdateCommand command = JobBoardUpdateCommand.builder()
                .title("New Title")
                .content("New Content")
                .addTags(List.of("tag1", "tag2"))
                .addAttachments(List.of("att1", "att2"))
                .deleteTags(List.of("tag3"))
                .deleteAttachments(List.of("att3"))
                .deadline(now)
                .experience(Experience.JUNIOR)
                .salary("1000")
                .build();
        JobBoardResult jobBoard = JobBoardResult.builder()
                .seq(1L)
                .title("Title")
                .content("Content")
                .viewCount(0L)
                .likeCount(0L)
                .tags(List.of("tag1", "tag3"))
                .attachments(List.of("att1", "att3"))
                .likeUsers(List.of())
                .bookmarkUsers(List.of())
                .country(null)
                .region(null)
                .industry(null)
                .deadline(null)
                .experience(null)
                .salary(null)
                .companySeq(1L)
                .companyName(null)
                .companyEmail(null)
                .companyPhone(null)
                .companyAddress(null)
                .companyHomepage(null)
                .companyLogo(null)
                .companyManagerUserSeq(3L)
                .status(null)
                .createdAt(null)
                .build();
        when(jobBoardRepository.getJobBoardBySeq(anyLong())).thenReturn(Optional.of(jobBoard));

        assertThatThrownBy(() -> jobBoardUpdateService.updateJobBoard(1L, 1L, command))
                .isInstanceOf(PostException.class)
                .hasMessage(NO_AUTHORITY.getMessage());
    }
}