package com.backend.immilog.post.presentation.controller;

import com.backend.immilog.comment.application.services.CommentQueryService;
import com.backend.immilog.post.application.dto.PostResult;
import com.backend.immilog.post.application.usecase.DeletePostUseCase;
import com.backend.immilog.post.application.usecase.FetchPostUseCase;
import com.backend.immilog.post.application.usecase.UpdatePostUseCase;
import com.backend.immilog.post.application.usecase.UploadPostUseCase;
import com.backend.immilog.post.domain.model.post.Categories;
import com.backend.immilog.post.domain.model.post.SortingMethods;
import com.backend.immilog.post.presentation.payload.*;
import com.backend.immilog.shared.annotation.CurrentUser;
import com.backend.immilog.shared.enums.ContentType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Tag(name = "Post API", description = "게시물 관련 API")
@RequestMapping("/api/v1/posts")
@RestController
public class PostController {
    private final UploadPostUseCase uploadPostUseCase;
    private final UpdatePostUseCase updatePostUseCase;
    private final DeletePostUseCase deletePostUseCase;
    private final FetchPostUseCase fetchPostUseCase;
    private final CommentQueryService commentQueryService;

    public PostController(
            UploadPostUseCase uploadPostUseCase,
            UpdatePostUseCase updatePostUseCase,
            DeletePostUseCase deletePostUseCase,
            FetchPostUseCase fetchPostUseCase,
            CommentQueryService commentQueryService
    ) {
        this.uploadPostUseCase = uploadPostUseCase;
        this.updatePostUseCase = updatePostUseCase;
        this.deletePostUseCase = deletePostUseCase;
        this.fetchPostUseCase = fetchPostUseCase;
        this.commentQueryService = commentQueryService;
    }

    @PostMapping
    @Operation(summary = "게시물 작성", description = "게시물을 작성합니다.")
    public ResponseEntity<Void> createPost(
            @CurrentUser String userId,
            @Valid @RequestBody PostUploadRequest postUploadRequest
    ) {
        uploadPostUseCase.uploadPost(userId, postUploadRequest.toCommand());
        return ResponseEntity.status(CREATED).build();
    }

    @PutMapping("/{postId}")
    @Operation(summary = "게시물 수정", description = "게시물을 수정합니다.")
    public ResponseEntity<Void> updatePost(
            @Parameter(description = "게시물 고유번호") @PathVariable("postId") String postId,
            @CurrentUser String userId,
            @Valid @RequestBody PostUpdateRequest postUpdateRequest
    ) {
        updatePostUseCase.updatePost(userId, postId, postUpdateRequest.toCommand());
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "게시물 삭제", description = "게시물을 삭제합니다.")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "게시물 고유번호") @PathVariable("postId") String postId,
            @CurrentUser String userId
    ) {
        deletePostUseCase.deletePost(userId, postId);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PostMapping("/{postId}/views")
    @Operation(summary = "게시물 조회수 증가", description = "게시물 조회수를 증가시킵니다.")
    public ResponseEntity<Void> increaseViewCount(
            @Parameter(description = "게시물 고유번호") @PathVariable("postId") String postId
    ) {
        updatePostUseCase.increaseViewCount(postId);
        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping
    @Operation(summary = "게시물 목록 조회", description = "게시물 목록을 조회합니다.")
    public ResponseEntity<PostPageResponse> getPosts(
            @Parameter(description = "국가") @RequestParam(value = "country", required = false) String countryId,
            @Parameter(description = "정렬 방식") @RequestParam(value = "sort", required = false) SortingMethods sort,
            @Parameter(description = "공개 여부") @RequestParam(value = "isPublic", required = false) String isPublic,
            @Parameter(description = "카테고리") @RequestParam(value = "category", required = false) Categories category,
            @Parameter(description = "검색어") @RequestParam(value = "keyword", required = false) String keyword,
            @Parameter(description = "페이지") @RequestParam(value = "page", defaultValue = "0") Integer page
    ) {
        Page<PostResult> posts;
        if (keyword != null) {
            posts = fetchPostUseCase.searchKeyword(keyword, page);
        } else {
            posts = fetchPostUseCase.getPosts(countryId, sort, isPublic, category, page);
        }
        var pagedPosts = posts.map(PostResult::toInfraDTO);
        return ResponseEntity.ok(PostPageResponse.of(pagedPosts));
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시물 상세 조회", description = "게시물 상세 정보를 조회합니다.")
    public ResponseEntity<PostDetailResponse> getPost(
            @Parameter(description = "게시물 고유번호") @PathVariable("postId") String postId
    ) {
        var post = fetchPostUseCase.getPostDetail(postId);
        var comments = commentQueryService.getHierarchicalCommentsByPostId(postId);
        return ResponseEntity.ok(PostDetailResponse.successWithHierarchicalComments(post, comments));
    }

    @GetMapping("/bookmarked")
    @Operation(summary = "북마크한 게시물 조회", description = "인증된 사용자의 북마크한 게시물을 조회합니다.")
    public ResponseEntity<PostListResponse> getBookmarkedPosts(
            @CurrentUser String userId,
            @Parameter(description = "포스팅 타입") @RequestParam(value = "contentType", defaultValue = "POST") ContentType contentType
    ) {
        var postResults = fetchPostUseCase.getBookmarkedPosts(userId, contentType);
        var postList = postResults.stream().map(PostResult::toInfraDTO).toList();
        return ResponseEntity.ok(PostListResponse.of(postList));
    }


    @GetMapping("/users/{userId}")
    @Operation(summary = "특정 사용자 게시물 목록 조회", description = "특정 사용자의 게시물 목록을 조회합니다.")
    public ResponseEntity<PostPageResponse> getPostsByUserId(
            @Parameter(description = "사용자 고유번호") @PathVariable("userId") String userId,
            @Parameter(description = "페이지") @RequestParam(value = "page", defaultValue = "0") Integer page
    ) {
        var postResults = fetchPostUseCase.getUserPosts(userId, page);
        var pagedPosts = postResults.map(PostResult::toInfraDTO);
        return ResponseEntity.ok(PostPageResponse.of(pagedPosts));
    }
}
