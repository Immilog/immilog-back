package com.backend.immilog.comment.presentation.controller;

import com.backend.immilog.comment.application.dto.CommentCreateCommand;
import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.comment.application.services.CommentCommandService;
import com.backend.immilog.comment.application.services.CommentQueryService;
import com.backend.immilog.comment.application.usecase.CommentCreateUseCase;
import com.backend.immilog.comment.presentation.request.CommentCreateRequest;
import com.backend.immilog.comment.presentation.response.CommentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentCreateUseCase commentCreateUseCase;
    private final CommentQueryService commentQueryService;
    private final CommentCommandService commentCommandService;

    public CommentController(
            CommentCreateUseCase commentCreateUseCase,
            CommentQueryService commentQueryService,
            CommentCommandService commentCommandService
    ) {
        this.commentCreateUseCase = commentCreateUseCase;
        this.commentQueryService = commentQueryService;
        this.commentCommandService = commentCommandService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @RequestHeader("User-Id") String userId,
            @RequestBody CommentCreateRequest request
    ) {
        var command = new CommentCreateCommand(
                userId,
                request.postId(),
                request.content(),
                request.referenceType()
        );
        var result = commentCreateUseCase.createComment(command);
        return ResponseEntity.ok(CommentResponse.success(result));
    }

    @GetMapping
    public ResponseEntity<CommentResponse> getComments(@RequestParam String postId) {
        var comments = commentQueryService.getComments(postId);
        return ResponseEntity.ok(CommentResponse.success(comments));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @RequestHeader("User-Id") String userId,
            @PathVariable String commentId,
            @RequestBody CommentCreateRequest request
    ) {
        var updatedComment = commentCommandService.updateComment(commentId, request.content());
        return ResponseEntity.ok(CommentResponse.success(CommentResult.from(updatedComment)));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentResponse> deleteComment(
            @RequestHeader("User-Id") String userId,
            @PathVariable String commentId
    ) {
        commentCommandService.deleteComment(commentId);
        return ResponseEntity.ok(CommentResponse.success("Comment deleted successfully"));
    }
}