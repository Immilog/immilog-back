package com.backend.immilog.user.presentation.payload;

import com.backend.immilog.user.application.command.UserSignInCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserSignInPayload() {
    @Schema(description = "사용자 로그인 요청 DTO")
    public record UserSignInRequest(
            @Schema(description = "이메일", example = "email@email.com")
            @NotBlank(message = "이메일을 입력해주세요.")
            @Email(message = "이메일 형식에 맞게 입력해주세요.")
            String email,

            @Schema(description = "비밀번호", example = "password")
            @Size(min = 8, max = 15, message = "비밀번호는 8자에서 15자여야 합니다.")
            @NotBlank(message = "비밀번호를 입력해주세요.")
            String password,

            @Schema(description = "위도", example = "37.123456")
            Double latitude,

            @Schema(description = "경도", example = "127.123456")
            Double longitude
    ) {
        public UserSignInCommand toCommand() {
            return new UserSignInCommand(
                    this.email,
                    this.password,
                    this.latitude,
                    this.longitude
            );
        }
    }

    public record UserSignInResponse(
            @Schema(description = "상태 코드", example = "200") Integer status,
            @Schema(description = "메시지", example = "success") String message,
            @Schema(description = "사용자 로그인 정보") UserSignInInformation data
    ) {
        public static UserSignInResponse success(UserSignInInformation userSignInInformation) {
            return new UserSignInPayload.UserSignInResponse(
                    200,
                    "success",
                    userSignInInformation
            );
        }

        public static UserSignInResponse failure(String message) {
            return new UserSignInPayload.UserSignInResponse(
                    400,
                    message,
                    null
            );
        }
    }

    public record UserSignInInformation(
            @Schema(description = "사용자 식별자", example = "1") String userId,
            @Schema(description = "이메일", example = "email@email.com") String email,
            @Schema(description = "닉네임", example = "nickname") String nickname,
            @Schema(description = "액세스 토큰", example = "access token") String accessToken,
            @Schema(description = "리프레시 토큰", example = "refresh token") String refreshToken,
            @Schema(description = "국가", example = "Korea") String country,
            @Schema(description = "관심 국가", example = "Korea") String interestCountry,
            @Schema(description = "지역", example = "Seoul") String region,
            @Schema(description = "프로필 이미지 URL", example = "profile image url") String userProfileUrl,
            @Schema(description = "위치 일치 여부", example = "true") Boolean isLocationMatch
    ) {
    }

    @Schema(description = "토큰 갱신 응답 DTO")
    public record RefreshTokenResponse(
            @Schema(description = "상태 코드", example = "200") Integer status,
            @Schema(description = "토큰 데이터") TokenData data
    ) {
        public static RefreshTokenResponse success(
                String userId,
                String accessToken,
                String refreshToken
        ) {
            return new RefreshTokenResponse(200, new TokenData(userId, accessToken, refreshToken));
        }

        public static RefreshTokenResponse failure(String message) {
            return new RefreshTokenResponse(400, null);
        }
    }

    @Schema(description = "토큰 데이터")
    public record TokenData(
            @Schema(description = "사용자 식별자", example = "1") String userId,
            @Schema(description = "액세스 토큰", example = "access_token") String accessToken,
            @Schema(description = "리프레시 토큰", example = "refresh_token") String refreshToken
    ) {
    }
}
