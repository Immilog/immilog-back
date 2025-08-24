package com.backend.immilog.post.infrastructure.jpa.entity.post;

import com.backend.immilog.post.domain.model.post.PostUserInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PostUserInfoValue {
    @Column(name = "user_id")
    private String userId;

    protected PostUserInfoValue() {}

    public PostUserInfoValue(String userId) {
        this.userId = userId;
    }

    public static PostUserInfoValue of(String userId) {
        return new PostUserInfoValue(userId);
    }

    public PostUserInfo toDomain() {
        return new PostUserInfo(this.userId);
    }
}
