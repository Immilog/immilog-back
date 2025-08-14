package com.backend.immilog.image.domain.model

import com.backend.immilog.image.exception.ImageErrorCode
import com.backend.immilog.image.exception.ImageException

data class ImagePath(val value: String) {
    companion object {
        fun of(value: String): ImagePath {
            require(value.isNotBlank()) {
                throw ImageException(ImageErrorCode.INVALID_IMAGE_PATH)
            }
            require(isValidImagePath(value)) {
                throw ImageException(ImageErrorCode.INVALID_IMAGE_PATH_FORMAT)
            }
            return ImagePath(value)
        }

        private fun isValidImagePath(path: String): Boolean {
            val allowedExtensions = listOf(".jpg", ".jpeg", ".png", ".gif", ".webp")
            return allowedExtensions.any { ext ->
                path.lowercase().endsWith(ext)
            }
        }
    }
}