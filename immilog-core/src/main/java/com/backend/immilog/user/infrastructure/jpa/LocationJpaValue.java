package com.backend.immilog.user.infrastructure.jpa;

import com.backend.immilog.user.domain.model.Location;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PROTECTED)
@Embeddable
public class LocationJpaValue {

    @Column(name = "country_id")
    private String countryId;

    @Column(name = "region")
    private String region;

    protected LocationJpaValue() {}

    protected LocationJpaValue(
            String countryId,
            String region
    ) {
        this.countryId = countryId;
        this.region = region;
    }

    public static LocationJpaValue of(
            String countryId,
            String region
    ) {
        return new LocationJpaValue(countryId, region);
    }

    public static LocationJpaValue from(Location location) {
        return new LocationJpaValue(
                location.countryId(),
                location.region()
        );
    }

    public Location toDomain() {
        if (this.countryId == null && this.region == null) {
            throw new UserException(UserErrorCode.ENTITY_TO_DOMAIN_ERROR);
        }
        return Location.of(this.countryId, this.region);
    }
}