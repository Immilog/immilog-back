package com.backend.immilog.user.infrastructure.jpa.entity;

import com.backend.immilog.user.domain.model.user.User;
import com.backend.immilog.user.domain.enums.UserCountry;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.user.domain.model.user.Location;
import com.backend.immilog.user.domain.model.user.ReportInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.assertj.core.api.Assertions.*;

@DisplayName("UserEntity 테스트")
class UserEntityTest {
    @Test
    @DisplayName("UserEntity from User - valid User object")
    void userEntityFromUser_validUser() {
        User user = User.builder()
                .seq(1L)
                .nickName("TestUser")
                .email("test@user.com")
                .password("password")
                .imageUrl("image.png")
                .userStatus(UserStatus.ACTIVE)
                .userRole(UserRole.ROLE_USER)
                .interestCountry(UserCountry.SOUTH_KOREA)
                .location(new Location(UserCountry.SOUTH_KOREA, "Country"))
                .reportInfo(new ReportInfo(1L, Date.valueOf("2024-12-12")))
                .build();

        UserEntity userEntity = UserEntity.from(user);

        assertThat(userEntity.getNickName()).isEqualTo(user.nickName());
        assertThat(userEntity.getEmail()).isEqualTo(user.email());
        assertThat(userEntity.getPassword()).isEqualTo(user.password());
        assertThat(userEntity.getImageUrl()).isEqualTo(user.imageUrl());
        assertThat(userEntity.getUserStatus()).isEqualTo(user.userStatus());
        assertThat(userEntity.getUserRole()).isEqualTo(user.userRole());
        assertThat(userEntity.getInterestCountry()).isEqualTo(user.interestCountry());
        assertThat(userEntity.getLocation()).isEqualTo(user.location());
        assertThat(userEntity.getReportInfo()).isEqualTo(user.reportInfo());
    }

    @Test
    @DisplayName("UserEntity toDomain - valid UserEntity object")
    void userEntityToDomain_validUserEntity() {
        UserEntity userEntity = UserEntity.builder()
                .seq(1L)
                .nickName("TestUser")
                .email("test@user.com")
                .password("password")
                .imageUrl("image.png")
                .userStatus(UserStatus.ACTIVE)
                .userRole(UserRole.ROLE_USER)
                .interestCountry(UserCountry.SOUTH_KOREA)
                .location(new Location(UserCountry.SOUTH_KOREA, "Country"))
                .reportInfo(new ReportInfo(1L, Date.valueOf("2024-12-12")))
                .build();
        User user = userEntity.toDomain();

        assertThat(user.nickName()).isEqualTo(userEntity.getNickName());
        assertThat(user.email()).isEqualTo(userEntity.getEmail());
        assertThat(user.password()).isEqualTo(userEntity.getPassword());
        assertThat(user.imageUrl()).isEqualTo(userEntity.getImageUrl());
        assertThat(user.userStatus()).isEqualTo(userEntity.getUserStatus());
        assertThat(user.userRole()).isEqualTo(userEntity.getUserRole());
        assertThat(user.interestCountry()).isEqualTo(userEntity.getInterestCountry());
        assertThat(user.location()).isEqualTo(userEntity.getLocation());
        assertThat(user.reportInfo()).isEqualTo(userEntity.getReportInfo());
    }

    @Test
    @DisplayName("UserEntity from User - null User")
    void userEntityFromUser_nullUser() {
        User user = null;

        assertThatThrownBy(() -> UserEntity.from(user))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("UserEntity toDomain - null")
    void userEntityToDomain_nullFields() {
        UserEntity userEntity = UserEntity.builder().build();

        User user = userEntity.toDomain();

        assertThat(user.seq()).isNull();
        assertThat(user.nickName()).isNull();
        assertThat(user.email()).isNull();
        assertThat(user.password()).isNull();
        assertThat(user.imageUrl()).isNull();
        assertThat(user.userStatus()).isNull();
        assertThat(user.userRole()).isNull();
        assertThat(user.interestCountry()).isNull();
        assertThat(user.location()).isNull();
        assertThat(user.reportInfo()).isNull();
    }

    @Test
    @DisplayName("UserEntity setter 테스트")
    void userEntitySetters() {
        UserEntity userEntity = UserEntity.builder().build();

        userEntity.setNickName("NewNickName");
        userEntity.setPassword("NewPassword");
        userEntity.setImageUrl("newImage.png");
        userEntity.setUserStatus(UserStatus.INACTIVE);
        userEntity.setInterestCountry(UserCountry.SOUTH_KOREA);

        assertThat(userEntity.getNickName()).isEqualTo("NewNickName");
        assertThat(userEntity.getPassword()).isEqualTo("NewPassword");
        assertThat(userEntity.getImageUrl()).isEqualTo("newImage.png");
        assertThat(userEntity.getUserStatus()).isEqualTo(UserStatus.INACTIVE);
        assertThat(userEntity.getInterestCountry()).isEqualTo(UserCountry.SOUTH_KOREA);
    }
}