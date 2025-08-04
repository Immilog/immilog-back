package com.backend.immilog.post.presentation.payload;

import com.backend.immilog.post.domain.model.post.Categories;
import com.backend.immilog.shared.enums.ContentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PostInformation(
        @Schema(description = "게시글 ID", example = "abce") String postId,
        @Schema(description = "작성자 ID", example = "user123") String userId,
        @Schema(description = "작성자 프로필 URL", example = "https://example.com/profile.jpg") String userProfileUrl,
        @Schema(description = "작성자 닉네임", example = "JohnDoe") String userNickName,
        @Schema(description = "댓글 수", example = "10") Long commentCount,
        @Schema(description = "조회 수", example = "100") Long viewCount,
        @Schema(description = "좋아요 수", example = "50") Long likeCount,
        @Schema(description = "북마크 수", example = "20") List<String> tags,
        @Schema(description = "첨부파일 URL 리스트", example = "[\"https://example.com/file1.jpg\"]") List<String> attachments,
        @Schema(description = "좋아요 사용자 ID 리스트", example = "[\"user1\", \"user2\"]") List<String> likeUsers,
        @Schema(description = "북마크 사용자 ID 리스트", example = "[\"user1\", \"user2\"]") List<String> bookmarkUsers,
        @Schema(description = "공개 여부", example = "true") String isPublic,
        @Schema(description = "국가", example = "Korea") String country,
        @Schema(description = "지역", example = "Seoul") String region,
        @Schema(description = "카테고리", example = "TECH") Categories category,
        @Schema(description = "콘텐츠 상태", example = "ACTIVE") ContentStatus status,
        @Schema(description = "생성 날짜", example = "2023-10-01T12:00:00") String createdAt,
        @Schema(description = "수정 날짜", example = "2023-10-02T12:00:00") String updatedAt,
        @Schema(description = "게시글 제목", example = "My First Post") String title,
        @Schema(description = "게시글 내용", example = "This is the content of my first post.") String content,
        @Schema(description = "검색 키워드", example = "example") String keyword
) {
}
