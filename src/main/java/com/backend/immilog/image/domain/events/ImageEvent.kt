package com.backend.immilog.image.domain.events

import com.backend.immilog.shared.domain.event.DomainEvent
import java.time.LocalDateTime

sealed class ImageEvent : DomainEvent {

    data class ImageUploadFailed(
        val errorMessage: String,
        val imagePath: String? = null,
        val exception: Throwable? = null,
        private val occurredAt: LocalDateTime = LocalDateTime.now()
    ) : ImageEvent() {
        override fun occurredAt(): LocalDateTime = occurredAt
    }

    data class ImageDeleteFailed(
        val imagePath: String,
        val errorMessage: String,
        val exception: Throwable? = null,
        private val occurredAt: LocalDateTime = LocalDateTime.now()
    ) : ImageEvent() {
        override fun occurredAt(): LocalDateTime = occurredAt
    }

    data class ImageProcessingFailed(
        val imagePath: String,
        val errorMessage: String,
        val exception: Throwable? = null,
        private val occurredAt: LocalDateTime = LocalDateTime.now()
    ) : ImageEvent() {
        override fun occurredAt(): LocalDateTime = occurredAt
    }

    data class ImageValidationFailed(
        val fileName: String,
        val errorMessage: String,
        val validationErrors: List<String> = emptyList(),
        private val occurredAt: LocalDateTime = LocalDateTime.now()
    ) : ImageEvent() {
        override fun occurredAt(): LocalDateTime = occurredAt
    }
}