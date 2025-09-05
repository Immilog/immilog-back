package com.backend.immilog.post.presentation.controller;

import com.backend.immilog.post.application.dto.PopularPostMenuResponse;
import com.backend.immilog.post.application.usecase.PopularPostMenuUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Popular Post API", description = "인기글 관련 API")
@RequestMapping("/api/v1/posts")
@RestController
public class PopularPostController {
    
    private final PopularPostMenuUseCase popularPostMenuUseCase;

    public PopularPostController(PopularPostMenuUseCase popularPostMenuUseCase) {
        this.popularPostMenuUseCase = popularPostMenuUseCase;
    }

    @GetMapping("/popular")
    @Operation(
        summary = "인기글 메뉴 조회", 
        description = "HOT 게시물 5개와 주간베스트 게시물 5개를 통합 조회합니다."
    )
    public ResponseEntity<PopularPostMenuResponse> getPopularPostMenu() {
        var response = popularPostMenuUseCase.getPopularPostMenu();
        return ResponseEntity.ok(response);
    }
}