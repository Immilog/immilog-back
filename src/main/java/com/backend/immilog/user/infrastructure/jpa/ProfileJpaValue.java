package com.backend.immilog.user.infrastructure.jpa;

import com.backend.immilog.user.domain.model.Profile;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PROTECTED)
@Embeddable
public class ProfileJpaValue {

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "interest_country_id")
    private String interestCountryId;

    protected ProfileJpaValue() {}

    protected ProfileJpaValue(
            String nickname,
            String imageUrl,
            String interestCountryId
    ) {
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.interestCountryId = interestCountryId;
    }

    public static ProfileJpaValue of(
            String nickname,
            String imageUrl,
            String interestCountryId
    ) {
        return new ProfileJpaValue(nickname, imageUrl, interestCountryId);
    }

    public static ProfileJpaValue from(Profile profile) {
        return new ProfileJpaValue(
                profile.nickname(),
                profile.imageUrl(),
                profile.interestCountryId()
        );
    }

    public Profile toDomain() {
        if (this.nickname == null) {
            throw new UserException(UserErrorCode.ENTITY_TO_DOMAIN_ERROR);
        }
        return Profile.of(this.nickname, this.imageUrl, this.interestCountryId);
    }
}
