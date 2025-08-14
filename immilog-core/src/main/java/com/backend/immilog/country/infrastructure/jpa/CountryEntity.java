package com.backend.immilog.country.infrastructure.jpa;

import com.backend.immilog.country.domain.model.Country;
import com.backend.immilog.country.domain.model.CountryStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "country")
public class CountryEntity {
    @Id
    @Column(name = "id", length = 10)
    private String id;

    @Column(name = "name_ko", length = 100, nullable = false)
    private String nameKo;

    @Column(name = "name_en", length = 100, nullable = false)
    private String nameEn;

    @Column(name = "continent", length = 50, nullable = false)
    private String continent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private CountryStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected CountryEntity() {}

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
        return new CountryEntity(
                country.id(),
                country.nameKo(),
                country.nameEn(),
                country.continent(),
                country.status(),
                country.createdAt(),
                country.updatedAt()
        );
    }

    public Country toDomain() {
        return new Country(
                this.id,
                this.nameKo,
                this.nameEn,
                this.continent,
                this.status,
                this.createdAt,
                this.updatedAt
        );
    }

    public String getId() {
        return id;
    }

    public String getNameKo() {
        return nameKo;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getContinent() {
        return continent;
    }

    public CountryStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}