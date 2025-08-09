package com.backend.immilog.comment.presentation.payload;

import com.backend.immilog.comment.domain.model.ReferenceType;
import com.backend.immilog.shared.enums.ContentStatus;
import com.backend.immilog.shared.enums.Country;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(
        @Schema(description = "상태 코드", example = "200") int status,
        @Schema(description = "응답 메시지", example = "success") String message,
        @Schema(description = "댓글 데이터", oneOf = {CommentInformation.class, List.class}) Object data
) {
    public static CommentResponse success(CommentInformation data) {
        return new CommentResponse(200, "success", data);
    }

    public static CommentResponse success(List<CommentInformation> data) {
        return new CommentResponse(200, "success", data);
    }

    public static CommentResponse success(String message) {
        return new CommentResponse(200, message, null);
    }

    public record CommentInformation(
            @Schema(description = "댓글 ID", example = "comment123") String commentId,
            @Schema(description = "사용자 ID", example = "user123") String userId,
            @Schema(description = "사용자 닉네임", example = "nickname") String nickname,
            @Schema(description = "사용자 프로필 URL", example = "https://example.com/profile/user123") String userProfileUrl,
            @Schema(description = "사용자 국가", example = "KOREA") Country country,
            @Schema(description = "사용자 지역", example = "Seoul") String region,
            @Schema(description = "댓글 내용", example = "이것은 댓글입니다.") String content,
            @Schema(description = "게시물 ID", example = "post123") String postId,
            @Schema(description = "부모 댓글 ID", example = "parentComment123") String parentId,
            @Schema(description = "참조 타입", example = "POST") ReferenceType referenceType,
            @Schema(description = "답글 수", example = "5") int replyCount,
            @Schema(description = "좋아요 수", example = "10") Integer likeCount,
            @Schema(description = "좋아요 누른 사용자 목록") List<String> likeUsers,
            @Schema(description = "북마크한 사용자 목록") List<String> bookmarkUsers,
            @Schema(description = "콘텐츠 상태", example = "ACTIVE") ContentStatus status,
            @Schema(description = "생성 시간", example = "2023-10-01T12:00:00") LocalDateTime createdAt,
            @Schema(description = "업데이트 시간", example = "2023-10-01T12:00:00") LocalDateTime updatedAt,
            @Schema(description = "대댓글 목록") List<CommentInformation> replies
    ) {
    }
}