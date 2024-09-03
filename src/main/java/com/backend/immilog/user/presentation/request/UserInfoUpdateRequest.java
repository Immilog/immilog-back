package com.backend.immilog.user.presentation.request;

import com.backend.immilog.user.application.dto.UserInfoUpdateCommand;
import com.backend.immilog.user.model.enums.UserCountry;
import com.backend.immilog.user.model.enums.UserStatus;
import io.swagger.annotations.ApiModel;
import lombok.Builder;

@Builder
@ApiModel(value = "UserInfoUpdateRequest", description = "사용자 정보 수정 요청 DTO")
public record UserInfoUpdateRequest(
        String nickName,
        String profileImage,
        UserCountry country,
        UserCountry interestCountry,
        Double latitude,
        Double longitude,
        UserStatus status
) {
    public UserInfoUpdateCommand toCommand() {
        return UserInfoUpdateCommand.builder()
                .nickName(nickName)
                .profileImage(profileImage)
                .country(country)
                .interestCountry(interestCountry)
                .latitude(latitude)
                .longitude(longitude)
                .status(status)
                .build();
    }
}
