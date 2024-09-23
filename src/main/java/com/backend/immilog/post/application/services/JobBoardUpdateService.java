package com.backend.immilog.post.application.services;

import com.backend.immilog.post.application.command.JobBoardUpdateCommand;
import com.backend.immilog.post.application.result.JobBoardResult;
import com.backend.immilog.post.domain.model.JobBoard;
import com.backend.immilog.post.domain.model.enums.PostType;
import com.backend.immilog.post.domain.model.enums.ResourceType;
import com.backend.immilog.post.domain.repositories.BulkInsertRepository;
import com.backend.immilog.post.domain.repositories.JobBoardRepository;
import com.backend.immilog.post.domain.repositories.PostResourceRepository;
import com.backend.immilog.post.domain.vo.CompanyMetaData;
import com.backend.immilog.post.domain.vo.PostMetaData;
import com.backend.immilog.post.exception.PostException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static com.backend.immilog.post.exception.PostErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobBoardUpdateService {
    private final JobBoardRepository jobBoardRepository;
    private final PostResourceRepository postResourceRepository;
    private final BulkInsertRepository bulkInsertRepository;

    @Transactional
    public void updateJobBoard(
            Long userSeq,
            Long jobBoardSeq,
            JobBoardUpdateCommand command
    ) {
        JobBoardResult jobBoard = getJobBoard(jobBoardSeq);
        verifyIfUserIsOwner(userSeq, jobBoard);
        JobBoard updatedJobBoard = createUpdatedJobBoard(userSeq, command, jobBoard);
        jobBoardRepository.saveEntity(updatedJobBoard);
    }

    @Transactional
    public void deactivateJobBoard(
            Long userSeq,
            Long jobBoardSeq
    ) {
        JobBoardResult jobBoard = getJobBoard(jobBoardSeq);
        verifyIfUserIsOwner(userSeq, jobBoard);
        JobBoard deletedJobBoard = createDeletedJobBoard(jobBoard);
        jobBoardRepository.saveEntity(deletedJobBoard);
    }

    private JobBoard createDeletedJobBoard(
            JobBoardResult jobBoard
    ) {
        return jobBoard.toDomain().toDeleteDomain();
    }

    private JobBoard createUpdatedJobBoard(
            Long userSeq,
            JobBoardUpdateCommand command,
            JobBoardResult jobBoard
    ) {
        PostMetaData postMetaData = PostMetaData.builder()
                .title(command.title() != null ? command.title() : jobBoard.title())
                .content(command.content() != null ? command.content() : jobBoard.content())
                .viewCount(jobBoard.viewCount())
                .likeCount(jobBoard.likeCount())
                .status(jobBoard.status())
                .country(jobBoard.country())
                .region(jobBoard.region())
                .build();

        updateTags(command, jobBoard);
        updateAttachments(command, jobBoard);

        return JobBoard.builder()
                .seq(jobBoard.seq())
                .userSeq(userSeq)
                .postMetaData(postMetaData)
                .companyMetaData(
                        CompanyMetaData.of(
                                jobBoard.companySeq(),
                                jobBoard.industry(),
                                command.experience() != null ? command.experience() : jobBoard.experience(),
                                command.deadline() != null ? command.deadline() : jobBoard.deadline(),
                                command.salary() != null ? command.salary() : jobBoard.salary(),
                                jobBoard.companyName(),
                                jobBoard.companyEmail(),
                                jobBoard.companyPhone(),
                                jobBoard.companyAddress(),
                                jobBoard.companyHomepage(),
                                jobBoard.companyLogo()
                        )
                )
                .build();
    }

    private void updateAttachments(
            JobBoardUpdateCommand command,
            JobBoardResult jobBoard
    ) {
        List<String> attachmentToDelete = jobBoard.attachments()
                .stream()
                .filter(attachment -> command.deleteAttachments().contains(attachment))
                .toList();

        postResourceRepository.deleteAllEntities(
                jobBoard.seq(),
                PostType.JOB_BOARD,
                ResourceType.ATTACHMENT,
                attachmentToDelete
        );

        saveAllPostResources(
                jobBoard.seq(),
                PostType.JOB_BOARD,
                ResourceType.ATTACHMENT,
                command.addAttachments()
        );
    }

    private void updateTags(
            JobBoardUpdateCommand command,
            JobBoardResult jobBoard
    ) {
        List<String> tagToDelete = jobBoard.tags()
                .stream()
                .filter(tag -> command.deleteAttachments().contains(tag))
                .toList();

        postResourceRepository.deleteAllEntities(
                jobBoard.seq(),
                PostType.JOB_BOARD,
                ResourceType.TAG,
                tagToDelete
        );

        saveAllPostResources(
                jobBoard.seq(),
                PostType.JOB_BOARD,
                ResourceType.TAG,
                command.addTags()
        );
    }

    private void saveAllPostResources(
            Long postSeq,
            PostType postType,
            ResourceType resourceType,
            List<String> postResources
    ) {
        bulkInsertRepository.saveAll(
                postResources,
                """
                INSERT INTO post_resource (
                    post_seq,
                    post_type,
                    resource_type,
                    content
                ) VALUES (?, ?, ?, ?)
                """,
                (ps, postResource) -> {
                    try {
                        ps.setLong(1, postSeq);
                        ps.setString(2, postType.name());
                        ps.setString(3, resourceType.name());
                        ps.setString(4, postResource);
                    } catch (SQLException e) {
                        log.error("Failed to save post resource: {}", e.getMessage());
                        throw new PostException(FAILED_TO_SAVE_POST);
                    }
                }
        );
    }

    private void verifyIfUserIsOwner(
            Long userSeq,
            JobBoardResult jobBoard
    ) {
        if (!Objects.equals(jobBoard.companyManagerUserSeq(), userSeq)) {
            throw new PostException(NO_AUTHORITY);
        }
    }

    private JobBoardResult getJobBoard(
            Long jobBoardSeq
    ) {
        return jobBoardRepository
                .getJobBoardBySeq(jobBoardSeq)
                .orElseThrow(() -> new PostException(JOB_BOARD_NOT_FOUND));
    }
}
