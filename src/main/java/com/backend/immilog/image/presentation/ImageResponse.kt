package com.backend.immilog.image.presentation

import com.backend.immilog.image.domain.ImageStatus
import com.backend.immilog.image.domain.ImageType
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

@Schema(description = "이미지 응답 DTO")
data class ImageResponse(
    @Schema(description = "상태 코드", example = "200")
    val status: Int,

    @Schema(description = "메시지", example = "success")
    val message: String,

    @Schema(description = "이미지 경로 목록", example = "[\"imagePath1\", \"imagePath2\"]")
    val data: List<String>
) {
    companion object {
        fun of(data: List<String>) = ImageResponse(
            status = HttpStatus.OK.value(),
            message = "success",
            data = data
        )

        fun success() = ImageResponse(
            status = HttpStatus.NO_CONTENT.value(),
            message = "success",
            data = emptyList()
        )
    }

    @Schema(description = "이미지 정보 DTO")
    data class ImageInfo(
        @Schema(description = "이미지 ID")
        val id: Long?,

        @Schema(description = "이미지 경로")
        val path: String,

        @Schema(description = "이미지 타입")
        val imageType: ImageType,

        @Schema(description = "이미지 상태")
        val status: ImageStatus,

        @Schema(description = "생성일시")
        val createdAt: LocalDateTime
    )
}
