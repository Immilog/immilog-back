package com.backend.immilog.user.presentation.payload;

import com.backend.immilog.user.application.dto.in.UserInfoUpdateCommand;
import com.backend.immilog.user.application.dto.in.UserPasswordChangeCommand;
import com.backend.immilog.user.domain.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserInformationPayload() {
    @Schema(description = "사용자 정보 수정 요청 DTO")
    public record UserInfoUpdateRequest(
            @Schema(description = "닉네임", example = "닉네임") String nickname,
            @Schema(description = "프로필 이미지", example = "프로필 이미지") String profileImage,
            @Schema(description = "국가", example = "SOUTH_KOREA") String countryId,
            @Schema(description = "관심 국가", example = "MALAYSIA") String interestCountryId,
            @Schema(description = "위도", example = "37.123456") Double latitude,
            @Schema(description = "경도", example = "127.123456") Double longitude,
            @Schema(description = "사용자 상태", example = "ACTIVE") UserStatus status
    ) {
        public UserInfoUpdateCommand toCommand() {
            return new UserInfoUpdateCommand(
                    this.nickname,
                    this.profileImage,
                    this.countryId,
                    this.interestCountryId,
                    this.latitude,
                    this.longitude,
                    this.status
            );
        }
    }

    public record userNicknameResponse(
            @Schema(description = "상태 코드", example = "200") Integer status,
            @Schema(description = "메시지", example = "success") String message,
            @Schema(description = "닉네임 중복 여부", example = "true") Boolean data
    ) {
        public userNicknameResponse(Boolean nicknameExist) {
            this(
                    200,
                    nicknameExist ? "success" : "fail",
                    nicknameExist
            );
        }
    }

    @Schema(description = "사용자 비밀번호 변경 요청 DTO")
    public record UserPasswordChangeRequest(
            @Schema(description = "기존 비밀번호", example = "password")
            @NotBlank(message = "비밀번호를 입력해주세요.")
            @Size(min = 8, max = 15, message = "비밀번호는 8자에서 15자여야 합니다.")
            String existingPassword,

            @Schema(description = "새로운 비밀번호", example = "newPassword")
            @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
            @Size(min = 8, max = 15, message = "비밀번호는 8자에서 15자여야 합니다.")
            String newPassword
    ) {
        public UserPasswordChangeCommand toCommand() {
            return new UserPasswordChangeCommand(existingPassword, newPassword);
        }
    }

    @Schema(description = "사용자 정보 조회 응답 DTO")
    public record UserInfoResponse(
            @Schema(description = "사용자 ID", example = "QztYeFAly_Py2jPgmKcha")
            String userId,
            @Schema(description = "이메일", example = "user@example.com")
            String email,
            @Schema(description = "닉네임", example = "닉네임")
            String userNickname,
            @Schema(description = "프로필 이미지", example = "profile-image-url")
            String userProfileUrl,
            @Schema(description = "지역", example = "Seoul")
            String region,
            @Schema(description = "국가", example = "KR")
            String country
    ) {}


}
