package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.shared.annotation.CurrentUser;
import com.backend.immilog.user.application.usecase.FetchLocationUseCase;
import com.backend.immilog.user.application.usecase.LoginUserUseCase;
import com.backend.immilog.user.presentation.payload.UserSignInPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth API", description = "인증 관련 API")
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {
    private final FetchLocationUseCase locationFetcher;
    private final LoginUserUseCase userLoginProcessor;

    public AuthController(
            FetchLocationUseCase locationFetcher,
            LoginUserUseCase userLoginProcessor
    ) {
        this.locationFetcher = locationFetcher;
        this.userLoginProcessor = userLoginProcessor;
    }

    @PostMapping("/signin")
    @Operation(summary = "사용자 로그인", description = "사용자 로그인을 진행합니다.")
    public ResponseEntity<UserSignInPayload.UserSignInResponse> signIn(
            @Valid @RequestBody UserSignInPayload.UserSignInRequest request
    ) {
        var country = locationFetcher.getCountry(request.latitude(), request.longitude());
        var userSignInResult = userLoginProcessor.signIn(request.toCommand(), country);
        var userSignInInformation = userSignInResult.toInfraDTO();
        return ResponseEntity.ok(UserSignInPayload.UserSignInResponse.success(userSignInInformation));
    }

    @GetMapping("/me")
    @Operation(summary = "인증된 사용자 정보 조회", description = "현재 인증된 사용자의 정보를 조회합니다.")
    public ResponseEntity<UserSignInPayload.UserSignInResponse> getCurrentUser(
            @CurrentUser String userId,
            @Parameter(description = "위도") @RequestParam("latitude") Double latitude,
            @Parameter(description = "경도") @RequestParam("longitude") Double longitude
    ) {
        var countryFuture = locationFetcher.getCountry(latitude, longitude);
        var country = locationFetcher.joinCompletableFutureLocation(countryFuture);
        var userSignInResult = userLoginProcessor.getUserSignInDTO(userId, country);
        var userSignInInformation = userSignInResult.toInfraDTO();
        return ResponseEntity.ok(UserSignInPayload.UserSignInResponse.success(userSignInInformation));
    }

    @GetMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.")
    public ResponseEntity<UserSignInPayload.RefreshTokenResponse> refreshToken(
            @Parameter(description = "리프레시 토큰") @RequestParam("token") String refreshToken
    ) {
        try {
            var userSignInResult = userLoginProcessor.refreshToken(refreshToken);
            var userSignInInformation = userSignInResult.toInfraDTO();
            return ResponseEntity.ok(UserSignInPayload.RefreshTokenResponse.success(
                    userSignInInformation.userId(),
                    userSignInInformation.accessToken(), 
                    userSignInInformation.refreshToken()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(UserSignInPayload.RefreshTokenResponse.failure("유효하지 않은 리프레시 토큰입니다."));
        }
    }

}

