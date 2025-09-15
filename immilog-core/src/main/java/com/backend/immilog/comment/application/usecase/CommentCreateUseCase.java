package com.backend.immilog.comment.application.usecase;

import com.backend.immilog.comment.application.dto.CommentCreateCommand;
import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.comment.application.services.CommentCommandService;
import com.backend.immilog.comment.application.services.CommentQueryService;
import com.backend.immilog.comment.domain.service.CommentDomainService;
import com.backend.immilog.shared.application.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

public interface CommentCreateUseCase {
    CommentResult createComment(CommentCreateCommand command);

    @Service
    @RequiredArgsConstructor
    class CommentCreator implements CommentCreateUseCase {
        private final CommentCommandService commentCommandService;
        private final CommentQueryService commentQueryService;
        private final CommentDomainService commentDomainService;
        private final DomainEventPublisher domainEventPublisher;

        @Override
        public CommentResult createComment(CommentCreateCommand command) {
            var comment = commentDomainService.createComment(
                    command.userId(),
                    command.postId(),
                    command.content(),
                    command.parentId(),
                    command.referenceType()
            );
            var savedComment = commentCommandService.createComment(comment);
            
            commentDomainService.publishCommentCreatedEvent(savedComment);
            
            domainEventPublisher.publishEvents();

            return commentQueryService.getCommentByCommentId(savedComment.id());
        }
    }
}