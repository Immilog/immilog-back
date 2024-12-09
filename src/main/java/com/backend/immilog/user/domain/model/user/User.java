package com.backend.immilog.user.domain.model.user;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.user.application.command.UserSignUpCommand;
import com.backend.immilog.user.domain.enums.UserCountry;
import com.backend.immilog.user.domain.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class User {
    private Long seq;
    private String nickName;
    private String email;
    private String password;
    private String imageUrl;
    private UserStatus userStatus;
    private UserRole userRole;
    private UserCountry interestCountry;
    private Location location;
    private ReportInfo reportInfo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    private User(
            Long seq,
            String nickName,
            String email,
            String password,
            String imageUrl,
            UserStatus userStatus,
            UserRole userRole,
            UserCountry interestCountry,
            Location location,
            ReportInfo reportInfo,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.seq = seq;
        this.nickName = nickName;
        this.email = email;
        this.password = password;
        this.imageUrl = imageUrl;
        this.userStatus = userStatus;
        this.userRole = userRole;
        this.interestCountry = interestCountry;
        this.location = location;
        this.reportInfo = reportInfo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // 정적 팩토리 메서드
    public static User of(
            UserSignUpCommand command,
            String encodedPassword
    ) {
        UserCountry interestCountry = command.interestCountry() != null
                ? UserCountry.getCountry(command.interestCountry())
                : null;
        UserCountry country = UserCountry.getCountry(command.country());

        return new User(
                null,
                command.nickName(),
                command.email(),
                encodedPassword,
                command.profileImage(),
                UserStatus.PENDING,
                UserRole.ROLE_USER,
                interestCountry,
                Location.of(country, command.region()),
                new ReportInfo(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public void changePassword(String encodedPassword) {
        if (Objects.isNull(encodedPassword)) {
            return;
        }
        this.password = encodedPassword;
        updateTimestamp();
    }

    public void changeUserStatus(UserStatus userStatus) {
        if (Objects.isNull(userStatus)) {
            return;
        }
        this.userStatus = userStatus;
        updateTimestamp();
    }

    public void changeNickName(String nickName) {
        if (Objects.isNull(nickName)) {
            return;
        }
        this.nickName = nickName;
        updateTimestamp();
    }

    public void changeInterestCountry(UserCountry interestCountry) {
        if (Objects.isNull(interestCountry)) {
            return;
        }
        this.interestCountry = interestCountry;
        updateTimestamp();
    }

    public void changeImageUrl(String imageUrl) {
        if (Objects.isNull(imageUrl)) {
            return;
        }
        this.imageUrl = imageUrl;
        updateTimestamp();
    }

    public void changeRegion(String second) {
        if (Objects.isNull(second)) {
            return;
        }
        this.location = Location.of(this.location.getCountry(), second);
        updateTimestamp();
    }

    public void changeCountry(UserCountry country) {
        if (Objects.isNull(country)) {
            return;
        }
        this.location = Location.of(country, this.location.getRegion());
        updateTimestamp();
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getReportedCount() {
        return this.reportInfo.getReportedCount();
    }

    public Date getReportedDate() {
        return this.reportInfo.getReportedDate();
    }

    public UserCountry getCountry() {
        return this.location.getCountry();
    }

    public String getRegion() {
        return this.location.getRegion();
    }

    public void increaseReportedCount() {
        this.reportInfo.setReportedCount(this.reportInfo.getReportedCount() + 1);
        this.reportInfo.setReportedDate(Date.valueOf(LocalDateTime.now().toLocalDate()));
    }
}
