package com.backend.immilog.image.infrastructure.persistence.jpa

import com.backend.immilog.image.domain.enums.ImageType
import org.springframework.data.jpa.repository.JpaRepository

interface ImageJpaRepository : JpaRepository<ImageJpaEntity, String> {
    fun findByPath(path: String): ImageJpaEntity?
    fun findByImageType(imageType: ImageType): List<ImageJpaEntity>
    fun existsByPath(path: String): Boolean
}
