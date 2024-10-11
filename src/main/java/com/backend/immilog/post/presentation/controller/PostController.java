package com.backend.immilog.post.presentation.controller;

import com.backend.immilog.global.security.ExtractUserId;
import com.backend.immilog.post.application.result.PostResult;
import com.backend.immilog.post.application.services.PostDeleteService;
import com.backend.immilog.post.application.services.PostInquiryService;
import com.backend.immilog.post.application.services.PostUpdateService;
import com.backend.immilog.post.application.services.PostUploadService;
import com.backend.immilog.post.domain.model.enums.Categories;
import com.backend.immilog.post.domain.model.enums.Countries;
import com.backend.immilog.post.domain.model.enums.SortingMethods;
import com.backend.immilog.post.presentation.request.PostUpdateRequest;
import com.backend.immilog.post.presentation.request.PostUploadRequest;
import com.backend.immilog.post.presentation.response.PostApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@Tag(name = "PostEntity API", description = "게시물 관련 API")
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostUploadService postUploadService;
    private final PostUpdateService postUpdateService;
    private final PostDeleteService postDeleteService;
    private final PostInquiryService postInquiryService;

    @PostMapping
    @ExtractUserId
    @Operation(summary = "게시물 작성", description = "게시물을 작성합니다.")
    public ResponseEntity<PostApiResponse> createPost(
            HttpServletRequest request,
            @Valid @RequestBody PostUploadRequest postUploadRequest
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        postUploadService.uploadPost(userSeq, postUploadRequest.toCommand());
        return ResponseEntity.status(CREATED).build();
    }

    @PatchMapping("/{postSeq}")
    @ExtractUserId
    @Operation(summary = "게시물 수정", description = "게시물을 수정합니다.")
    public ResponseEntity<PostApiResponse> updatePost(
            @PathVariable Long postSeq,
            HttpServletRequest request,
            @Valid @RequestBody PostUpdateRequest postUpdateRequest
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        postUpdateService.updatePost(userSeq, postSeq, postUpdateRequest.toCommand());
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/{postSeq}/delete")
    @ExtractUserId
    @Operation(summary = "게시물 삭제", description = "게시물을 삭제합니다.")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postSeq,
            HttpServletRequest request
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        postDeleteService.deletePost(userSeq, postSeq);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/{postSeq}/view")
    @Operation(summary = "게시물 조회수 증가", description = "게시물 조회수를 증가시킵니다.")
    public ResponseEntity<PostApiResponse> increaseViewCount(
            @PathVariable Long postSeq
    ) {
        postUpdateService.increaseViewCount(postSeq);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @GetMapping
    @Operation(summary = "게시물 목록 조회", description = "게시물 목록을 조회합니다.")
    public ResponseEntity<PostApiResponse> getPosts(
            @RequestParam(required = false) Countries country,
            @RequestParam(required = false) SortingMethods sortingMethod,
            @RequestParam(required = false) String isPublic,
            @RequestParam(required = false) Categories category,
            @RequestParam(required = false) Integer page
    ) {
        Page<PostResult> posts = postInquiryService.getPosts(
                country,
                sortingMethod,
                isPublic,
                category,
                page == null ? 0 : page
        );
        return ResponseEntity.status(OK).body(PostApiResponse.of(posts));
    }

    @GetMapping("/{postSeq}")
    @Operation(summary = "게시물 상세 조회", description = "게시물 상세 정보를 조회합���다.")
    public ResponseEntity<PostApiResponse> getPost(
            @PathVariable("postSeq") Long postSeq
    ) {
        PostResult post = postInquiryService.getPost(postSeq);
        return ResponseEntity
                .status(OK)
                .body(PostApiResponse.of(post));
    }

    @GetMapping("/search")
    @Operation(summary = "게시물 검색", description = "게시물을 검색합니다.")
    public ResponseEntity<PostApiResponse> searchPosts(
            @RequestParam(required = true) String keyword,
            @RequestParam(required = true) Integer page
    ) {
        Page<PostResult> posts = postInquiryService.searchKeyword(keyword, page);
        return ResponseEntity.status(OK).body(PostApiResponse.of(posts));
    }

    @GetMapping("/users/{userSeq}/page/{page}")
    @Operation(summary = "사용자 게시물 목록 조회", description = "사용자 게시물 목록을 조회합니다.")
    public ResponseEntity<PostApiResponse> getUserPosts(
            @PathVariable Long userSeq,
            @PathVariable Integer page
    ) {
        Page<PostResult> posts = postInquiryService.getUserPosts(userSeq, page);
        return ResponseEntity.status(OK).body(PostApiResponse.of(posts));
    }

}
