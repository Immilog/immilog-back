package com.backend.immilog.comment.application.usecase;

import com.backend.immilog.comment.application.dto.CommentCreateCommand;
import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.comment.application.services.CommentCommandService;
import com.backend.immilog.comment.domain.model.Comment;
import org.springframework.stereotype.Service;

public interface CommentCreateUseCase {
    CommentResult createComment(CommentCreateCommand command);

    @Service
    class CommentCreator implements CommentCreateUseCase {
        private final CommentCommandService commentCommandService;

        public CommentCreator(CommentCommandService commentCommandService) {
            this.commentCommandService = commentCommandService;
        }

        @Override
        public CommentResult createComment(CommentCreateCommand command) {
            var comment = Comment.of(
                    command.userId(),
                    command.postId(),
                    command.content(),
                    command.referenceType()
            );
            var savedComment = commentCommandService.createComment(comment);
            return CommentResult.from(savedComment);
        }
    }
}