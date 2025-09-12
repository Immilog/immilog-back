package com.backend.immilog.country.infrastructure;

import com.backend.immilog.country.domain.Country;
import com.backend.immilog.country.domain.CountryId;
import com.backend.immilog.country.domain.CountryInfo;
import com.backend.immilog.country.domain.CountryStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "country")
public class CountryEntity {
    @Id
    @Getter
    @Column(name = "id", length = 10)
    private String id;

    @Column(name = "name_ko", length = 100, nullable = false)
    private String nameKo;

    @Column(name = "name_en", length = 100, nullable = false)
    private String nameEn;

    @Column(name = "continent", length = 50, nullable = false)
    private String continent;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private CountryStatus status;

    @Getter
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Getter
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected CountryEntity() {}

    @Builder
    public CountryEntity(
            String id,
            String nameKo,
            String nameEn,
            String continent,
            CountryStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
        this.continent = continent;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CountryEntity from(Country country) {
        return CountryEntity.builder()
                .id(country.id().value())
                .nameKo(country.nameKo())
                .nameEn(country.nameEn())
                .continent(country.continent())
                .status(country.status())
                .createdAt(country.createdAt())
                .updatedAt(country.updatedAt())
                .build();
    }

    public Country toDomain() {
        return Country.builder()
                .id(new CountryId(this.id))
                .info(new CountryInfo(this.nameKo,this.nameEn,this.continent))
                .status(this.status)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}