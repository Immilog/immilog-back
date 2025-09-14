package com.backend.immilog.post.presentation.controller;

import com.backend.immilog.post.application.dto.out.EnrichedPopularPostMenuResponse;
import com.backend.immilog.post.application.dto.out.PostResult;
import com.backend.immilog.post.application.services.query.PostPopularService;
import com.backend.immilog.shared.annotation.CurrentUser;
import com.backend.immilog.shared.application.gateway.ApiGatewayService;
import com.backend.immilog.shared.application.gateway.DataEnrichmentRequest;
import com.backend.immilog.shared.application.gateway.EnrichedResult;
import com.backend.immilog.shared.enums.ContentType;
import com.backend.immilog.shared.model.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Tag(name = "Enriched Popular Post API", description = "API Gateway를 활용한 인기글 API")
@RequestMapping("/api/v2/posts")
@RestController
@RequiredArgsConstructor
public class PopularPostController {

    private final PostPopularService postPopularService;
    private final ApiGatewayService apiGatewayService;

    @GetMapping("/popular")
    @Operation(
            summary = "향상된 인기글 메뉴 조회",
            description = "HOT 게시물과 주간베스트 게시물을 사용자 정보, 상호작용 정보와 함께 조회합니다."
    )
    public ResponseEntity<EnrichedPopularPostMenuResponse> getEnrichedPopularPostMenu(
            @CurrentUser AuthenticatedUser currentUser,
            @RequestParam(defaultValue = "true") boolean includeUserData,
            @RequestParam(defaultValue = "true") boolean includeInteractionData
    ) {

        log.info("Getting enriched popular post menu for user: {}",
                currentUser != null ? currentUser.userId() : "anonymous");

        try {
            var baseResponse = postPopularService.getPopularPostMenu();

            var enrichmentRequest = DataEnrichmentRequest.builder()
                    .userId(currentUser != null ? currentUser.userId() : null)
                    .contentType(ContentType.POST);

            if (includeUserData) {
                enrichmentRequest.includeUserData();
            }
            if (includeInteractionData) {
                enrichmentRequest.includeInteractionData();
                enrichmentRequest.includeUserInteraction();
            }

            var request = enrichmentRequest.build();

            var hotPostsFuture = enrichPostResults(baseResponse.hot(), request);

            var weeklyBestFuture = enrichPostResults(baseResponse.weeklyBest(), request);

            var combinedFuture = hotPostsFuture.thenCombine(
                    weeklyBestFuture,
                    (enrichedHot, enrichedWeekly) -> {
                        log.debug("Data enrichment completed - Hot: {}, Weekly: {}", enrichedHot.size(), enrichedWeekly.size());
                        return EnrichedPopularPostMenuResponse.builder()
                                .hotPosts(enrichedHot)
                                .weeklyBest(enrichedWeekly)
                                .totalHotPosts(enrichedHot.size())
                                .totalWeeklyBest(enrichedWeekly.size())
                                .enrichmentIncluded(includeUserData || includeInteractionData)
                                .currentUserId(currentUser != null ? currentUser.userId() : null)
                                .build();
                    });

            var enrichedResponse = combinedFuture.get(5, java.util.concurrent.TimeUnit.SECONDS);

            return ResponseEntity.ok(enrichedResponse);

        } catch (Exception e) {
            log.error("Error getting enriched popular post menu", e);

            var baseResponse = postPopularService.getPopularPostMenu();
            var fallbackResponse = EnrichedPopularPostMenuResponse.fallback(
                    baseResponse.hot(),
                    baseResponse.weeklyBest(),
                    e.getMessage()
            );

            return ResponseEntity.ok(fallbackResponse);
        }
    }

    @GetMapping("/popular/hot")
    @Operation(
            summary = "HOT 게시물 조회",
            description = "HOT 게시물만을 향상된 데이터와 함께 조회합니다."
    )
    public ResponseEntity<List<EnrichedResult<PostResult>>> getEnrichedHotPosts(
            @CurrentUser AuthenticatedUser currentUser,
            @RequestParam(defaultValue = "true") boolean includeUserData,
            @RequestParam(defaultValue = "true") boolean includeInteractionData
    ) {

        try {
            var baseResponse = postPopularService.getPopularPostMenu();
            var request = buildEnrichmentRequest(currentUser, includeUserData, includeInteractionData);

            var enrichedPosts = enrichPostResults(baseResponse.hot(), request).get(3, TimeUnit.SECONDS);

            return ResponseEntity.ok(enrichedPosts);

        } catch (Exception e) {
            log.error("Error getting enriched hot posts", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/popular/weekly")
    @Operation(
            summary = "주간베스트 게시물 조회",
            description = "주간베스트 게시물만을 향상된 데이터와 함께 조회합니다."
    )
    public ResponseEntity<List<EnrichedResult<PostResult>>> getEnrichedWeeklyBestPosts(
            @CurrentUser AuthenticatedUser currentUser,
            @RequestParam(defaultValue = "true") boolean includeUserData,
            @RequestParam(defaultValue = "true") boolean includeInteractionData
    ) {

        try {
            var baseResponse = postPopularService.getPopularPostMenu();
            var request = buildEnrichmentRequest(currentUser, includeUserData, includeInteractionData);

            var enrichedPosts = enrichPostResults(baseResponse.weeklyBest(), request).get(3, TimeUnit.SECONDS);

            return ResponseEntity.ok(enrichedPosts);

        } catch (Exception e) {
            log.error("Error getting enriched weekly best posts", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Private helper methods
    private CompletableFuture<List<EnrichedResult<PostResult>>> enrichPostResults(
            List<PostResult> posts,
            DataEnrichmentRequest request
    ) {

        return apiGatewayService.enrichDataListAsync(posts, request);
    }

    private DataEnrichmentRequest buildEnrichmentRequest(
            AuthenticatedUser currentUser,
            boolean includeUserData,
            boolean includeInteractionData
    ) {

        var builder = DataEnrichmentRequest.builder()
                .userId(currentUser != null ? currentUser.userId() : null)
                .contentType(ContentType.POST);

        if (includeUserData) {
            builder.includeUserData();
        }
        if (includeInteractionData) {
            builder.includeInteractionData();
            builder.includeUserInteraction();
        }

        return builder.build();
    }
}