package com.backend.immilog.image.infrastructure.gateway

import com.backend.immilog.shared.config.properties.WebProperties
import com.backend.immilog.shared.domain.event.DomainEvents
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mockito.*
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.nio.file.Path

@DisplayName("LocalFileStorageHandler 테스트")
class LocalFileStorageHandlerTest {

    private lateinit var webProperties: WebProperties
    private lateinit var fileStorageHandler: LocalFileStorageHandler
    
    @TempDir
    lateinit var tempDir: Path

    @BeforeEach
    fun setUp() {
        // DomainEvents 초기화 (테스트 격리)
        DomainEvents.clearEvents()
        
        webProperties = mock(WebProperties::class.java)
        val fileStorageConfig = mock(WebProperties.FileStorage::class.java)
        `when`(webProperties.fileStorage).thenReturn(fileStorageConfig)
        `when`(fileStorageConfig.directory).thenReturn(tempDir.toString())
        fileStorageHandler = LocalFileStorageHandler(webProperties)
    }

    @Test
    @DisplayName("파일 업로드 성공 시 URL 반환")
    fun `파일 업로드 성공 시 URL 반환`() {
        // given
        val file = mock(MultipartFile::class.java)
        `when`(file.originalFilename).thenReturn("test.jpg")
        `when`(file.inputStream).thenReturn(ByteArrayInputStream("test content".toByteArray()))
        val imagePath = "test-path"

        // when
        val result = fileStorageHandler.uploadFile(file, imagePath)

        // then
        assertThat(result).startsWith("/images/$imagePath/")
        assertThat(result).endsWith(".jpg")
        // 성공 케이스에서는 이벤트가 발생하지 않아야 함
        assertThat(DomainEvents.hasEvents()).isFalse()
    }

    @Test
    @DisplayName("null 파일명은 기본 확장자 사용")
    fun `null 파일명은 기본 확장자 사용`() {
        // given
        val file = mock(MultipartFile::class.java)
        `when`(file.originalFilename).thenReturn(null)
        `when`(file.inputStream).thenReturn(ByteArrayInputStream("test content".toByteArray()))
        val imagePath = "test-path"

        // when
        val result = fileStorageHandler.uploadFile(file, imagePath)

        // then
        assertThat(result).endsWith(".png") // 기본 확장자
        assertThat(DomainEvents.hasEvents()).isFalse()
    }

    @Test
    @DisplayName("null 이미지 경로로 삭제 시 아무 작업 안함")
    fun `null 이미지 경로로 삭제 시 아무 작업 안함`() {
        // when & then
        fileStorageHandler.deleteFile(null)
        fileStorageHandler.deleteFile("")
        fileStorageHandler.deleteFile("   ")

        // 이벤트 발행되지 않아야 함
        assertThat(DomainEvents.hasEvents()).isFalse()
    }

    @Test
    @DisplayName("파일 업로드 실패 시 이벤트 발행")
    fun `파일 업로드 실패 시 이벤트 발행`() {
        // given
        val file = mock(MultipartFile::class.java)
        `when`(file.originalFilename).thenReturn("test.jpg")
        `when`(file.inputStream).thenThrow(java.io.IOException("Upload failed"))
        val imagePath = "test-path"

        // when & then
        try {
            fileStorageHandler.uploadFile(file, imagePath)
        } catch (e: Exception) {
            // 예외 발생 예상
        }

        // 실패 시 이벤트가 발행되어야 함
        assertThat(DomainEvents.hasEvents()).isTrue()
        val events = DomainEvents.getEvents()
        assertThat(events).hasSize(1)
        assertThat(events[0]).isInstanceOf(com.backend.immilog.image.domain.events.ImageEvent.ImageUploadFailed::class.java)
    }
}