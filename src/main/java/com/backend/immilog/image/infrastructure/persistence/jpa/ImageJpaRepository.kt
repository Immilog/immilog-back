package com.backend.immilog.image.infrastructure.persistence.jpa

import com.backend.immilog.image.domain.ImageType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ImageJpaRepository : JpaRepository<ImageJpaEntity, Long> {
    fun findByPath(path: String): ImageJpaEntity?
    fun findByImageType(imageType: ImageType): List<ImageJpaEntity>
    fun existsByPath(path: String): Boolean
}
