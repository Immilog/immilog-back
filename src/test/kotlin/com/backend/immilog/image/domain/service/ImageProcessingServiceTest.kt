package com.backend.immilog.image.domain.service

import com.backend.immilog.image.domain.enums.ImageType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@DisplayName("ImageProcessingService 테스트")
class ImageProcessingServiceTest {

    private val imageProcessingService = ImageProcessingService()

    @Test
    @DisplayName("파일로부터 이미지를 생성한다")
    fun `파일로부터 이미지를 생성한다`() {
        // given
        val file = MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test content".toByteArray()
        )
        val storedPath = "/images/stored/test.jpg"
        val imageType = ImageType.PROFILE

        // when
        val image = imageProcessingService.createImageFromFile(file, storedPath, imageType)

        // then
        assertThat(image.id).isNull()
        assertThat(image.path.value).isEqualTo(storedPath)
        assertThat(image.metadata.imageType).isEqualTo(imageType)
        assertThat(image.metadata.originalFileName).isEqualTo("test.jpg")
        assertThat(image.metadata.fileSize).isEqualTo(file.size)
        assertThat(image.metadata.contentType).isEqualTo("image/jpeg")
    }

    @Test
    @DisplayName("상대 경로로부터 전체 이미지 URL을 생성한다")
    fun `상대 경로로부터 전체 이미지 URL을 생성한다`() {
        // given
        val relativePath = "/images/test.jpg"

        val mockRequest = MockHttpServletRequest().apply {
            scheme = "http"
            serverName = "localhost"
            serverPort = 8080
            contextPath = ""
        }
        val requestAttributes = ServletRequestAttributes(mockRequest)
        RequestContextHolder.setRequestAttributes(requestAttributes)

        try {
            // when
            val fullUrl = imageProcessingService.buildFullImageUrl(relativePath)

            // then
            assertThat(fullUrl).contains(relativePath)
            assertThat(fullUrl).startsWith("http://localhost:8080")
        } finally {
            RequestContextHolder.resetRequestAttributes()
        }
    }

    @Test
    @DisplayName("PROFILE 타입에 대한 이미지 경로 접두사를 생성한다")
    fun `PROFILE 타입에 대한 이미지 경로 접두사를 생성한다`() {
        // given
        val imageType = ImageType.PROFILE

        // when
        val pathPrefix = imageProcessingService.generateImagePathPrefix(imageType)

        // then
        assertThat(pathPrefix).isEqualTo("/images/profiles")
    }

    @Test
    @DisplayName("POST 타입에 대한 이미지 경로 접두사를 생성한다")
    fun `POST 타입에 대한 이미지 경로 접두사를 생성한다`() {
        // given
        val imageType = ImageType.POST

        // when
        val pathPrefix = imageProcessingService.generateImagePathPrefix(imageType)

        // then
        assertThat(pathPrefix).isEqualTo("/images/posts")
    }

    @Test
    @DisplayName("JOB_POST 타입에 대한 이미지 경로 접두사를 생성한다")
    fun `JOB_POST 타입에 대한 이미지 경로 접두사를 생성한다`() {
        // given
        val imageType = ImageType.JOB_POST

        // when
        val pathPrefix = imageProcessingService.generateImagePathPrefix(imageType)

        // then
        assertThat(pathPrefix).isEqualTo("/images/job-posts")
    }

    @Test
    @DisplayName("모든 ImageType에 대해 경로 접두사를 생성할 수 있다")
    fun `모든 ImageType에 대해 경로 접두사를 생성할 수 있다`() {
        // given & when & then
        ImageType.values().forEach { imageType ->
            val pathPrefix = imageProcessingService.generateImagePathPrefix(imageType)
            assertThat(pathPrefix).isNotBlank()
            assertThat(pathPrefix).startsWith("/images/")
        }
    }
}