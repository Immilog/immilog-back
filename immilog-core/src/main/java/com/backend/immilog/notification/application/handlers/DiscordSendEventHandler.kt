package com.backend.immilog.notification.application.handlers

import com.backend.immilog.image.domain.events.ImageEvent
import com.backend.immilog.notification.application.DiscordSendingService
import com.backend.immilog.shared.domain.event.DomainEventHandler
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DiscordSendEventHandler(
    private val discordSendingService: DiscordSendingService
) : DomainEventHandler<ImageEvent.ImageDeleteFailed> {

    override fun getEventType(): Class<ImageEvent.ImageDeleteFailed> {
        return ImageEvent.ImageDeleteFailed::class.java
    }

    override fun handle(event: ImageEvent.ImageDeleteFailed) {
        val message = buildErrorMessage(
            title = "이미지 삭제 실패",
            errorMessage = event.errorMessage,
            imagePath = event.imagePath,
            exception = event.exception
        )
        event.exception?.let { ex ->
            if (ex is Exception) {
                discordSendingService.send(message, ex)
            }
        }
    }

    private fun buildErrorMessage(
        title: String,
        errorMessage: String,
        imagePath: String?,
        exception: Throwable?
    ): String {
        return buildString {
            appendLine("**$title**")
            appendLine("**메시지:** $errorMessage")
            if (!imagePath.isNullOrBlank()) {
                appendLine("**이미지 경로:** $imagePath")
            }
            if (exception != null) {
                appendLine("**예외 타입:** ${exception::class.simpleName}")
                appendLine("**예외 메시지:** ${exception.message}")
            }
            appendLine("**발생 시간:** ${LocalDateTime.now()}")
        }
    }
}