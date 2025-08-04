package com.backend.immilog.notification.application.handlers

import com.backend.immilog.image.domain.events.ImageEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.IOException

@DisplayName("ImageErrorEventHandler 테스트")
class DiscordSendEventHandlerTest {

    @Test
    @DisplayName("이미지 이벤트가 올바르게 생성되는지 확인")
    fun `이미지 이벤트가 올바르게 생성되는지 확인`() {
        // given
        val exception = IOException("Upload failed")
        
        // when
        val event = ImageEvent.ImageUploadFailed(
            errorMessage = "파일 업로드 실패",
            imagePath = "/test/image.jpg",
            exception = exception
        )
        
        // then
        assertThat(event.errorMessage).isEqualTo("파일 업로드 실패")
        assertThat(event.imagePath).isEqualTo("/test/image.jpg")
        assertThat(event.exception).isEqualTo(exception)
        assertThat(event.occurredAt()).isNotNull
    }

    @Test
    @DisplayName("이미지 삭제 실패 이벤트 생성")
    fun `이미지 삭제 실패 이벤트 생성`() {
        // given
        val exception = IOException("Delete failed")
        
        // when
        val event = ImageEvent.ImageDeleteFailed(
            imagePath = "/test/image.jpg",
            errorMessage = "파일 삭제 실패",
            exception = exception
        )
        
        // then
        assertThat(event.imagePath).isEqualTo("/test/image.jpg")
        assertThat(event.errorMessage).isEqualTo("파일 삭제 실패")
        assertThat(event.exception).isEqualTo(exception)
        assertThat(event.occurredAt()).isNotNull
    }

    @Test
    @DisplayName("이미지 검증 실패 이벤트 생성")
    fun `이미지 검증 실패 이벤트 생성`() {
        // when
        val event = ImageEvent.ImageValidationFailed(
            fileName = "test.jpg",
            errorMessage = "이미지 검증 실패",
            validationErrors = listOf("파일 크기 초과", "지원하지 않는 형식")
        )
        
        // then
        assertThat(event.fileName).isEqualTo("test.jpg")
        assertThat(event.errorMessage).isEqualTo("이미지 검증 실패")
        assertThat(event.validationErrors).containsExactly("파일 크기 초과", "지원하지 않는 형식")
        assertThat(event.occurredAt()).isNotNull
    }
}