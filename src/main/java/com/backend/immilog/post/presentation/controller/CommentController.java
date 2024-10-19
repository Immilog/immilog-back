package com.backend.immilog.post.presentation.controller;

import com.backend.immilog.global.security.ExtractUserId;
import com.backend.immilog.post.application.services.CommentUploadService;
import com.backend.immilog.post.presentation.request.CommentUploadRequest;
import com.backend.immilog.post.presentation.response.PostApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "CommentEntity API", description = "댓글 관련 API")
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentUploadService commentUploadService;

    @PostMapping("/{referenceType}/{postSeq}")
    @ExtractUserId
    @Operation(summary = "댓글 작성", description = "댓글을 작성합니다.")
    public ResponseEntity<PostApiResponse> createComment(
            @PathVariable("referenceType") String referenceType,
            @PathVariable("postSeq") Long postSeq,
            HttpServletRequest request,
            @Valid @RequestBody CommentUploadRequest commentUploadRequest
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        commentUploadService.uploadComment(
                userSeq,
                postSeq,
                referenceType,
                commentUploadRequest.content()
        );
        return ResponseEntity.status(CREATED).build();
    }
}
