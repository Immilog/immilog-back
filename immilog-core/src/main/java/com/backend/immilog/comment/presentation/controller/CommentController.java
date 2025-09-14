package com.backend.immilog.comment.presentation.controller;

import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.comment.application.services.CommentCommandService;
import com.backend.immilog.comment.application.services.CommentQueryService;
import com.backend.immilog.comment.application.usecase.CommentCreateUseCase;
import com.backend.immilog.comment.presentation.payload.CommentCreateRequest;
import com.backend.immilog.comment.presentation.payload.CommentResponse;
import com.backend.immilog.shared.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentCreateUseCase commentCreateUseCase;
    private final CommentQueryService commentQueryService;
    private final CommentCommandService commentCommandService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @CurrentUser String userId,
            @RequestBody CommentCreateRequest request
    ) {
        var command = request.toCommand(userId);
        var result = commentCreateUseCase.createComment(command);
        return ResponseEntity.ok(CommentResponse.success(result.toInfraDTO()));
    }

    @GetMapping
    public ResponseEntity<CommentResponse> getComments(@RequestParam("postId") String postId) {
        var comments = commentQueryService.getCommentsByPostId(postId);
        var commentInformationList = comments.stream().map(CommentResult::toInfraDTO).toList();
        return ResponseEntity.ok(CommentResponse.success(commentInformationList));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable("commentId") String commentId,
            @RequestBody CommentCreateRequest request
    ) {
        var updatedComment = commentCommandService.updateComment(commentId, request.content());
        var commentByCommentId = commentQueryService.getCommentByCommentId(updatedComment.id());
        return ResponseEntity.ok(CommentResponse.success(commentByCommentId.toInfraDTO()));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentResponse> deleteComment(
            @PathVariable("commentId") String commentId
    ) {
        commentCommandService.deleteComment(commentId);
        return ResponseEntity.ok(CommentResponse.success("Comment deleted successfully"));
    }
}