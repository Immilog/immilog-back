package com.backend.immilog.post.infrastructure.jpa.entity.post;

import com.backend.immilog.post.domain.model.post.PostUserInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PostUserInfoValue {
    @Column(name = "user_id")
    private String userId;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "image_url")
    private String profileImage;

    protected PostUserInfoValue() {}

    public PostUserInfoValue(
            String userId,
            String nickname,
            String profileImage
    ) {
        this.userId = userId;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public static PostUserInfoValue of(
            String userId,
            String nickname,
            String profileImage
    ) {
        return new PostUserInfoValue(userId, nickname, profileImage);
    }

    public PostUserInfo toDomain() {
        return new PostUserInfo(this.userId, this.nickname, this.profileImage);
    }
}
