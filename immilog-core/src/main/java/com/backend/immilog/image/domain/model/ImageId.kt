package com.backend.immilog.image.domain.model

data class ImageId(val value: String) {
    companion object {
        fun of(value: String?): ImageId {
            require(value != null && !value.isEmpty()) { "ImageId value must be not null or empty" }
            return ImageId(value)
        }

        fun generate(): ImageId? = null
    }
}