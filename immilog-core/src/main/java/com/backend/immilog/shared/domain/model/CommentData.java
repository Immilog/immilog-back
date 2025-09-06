package com.backend.immilog.shared.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommentData(
    @JsonProperty("commentId") String commentId,
    @JsonProperty("postId") String postId,
    @JsonProperty("userId") String userId,
    @JsonProperty("content") String content,
    @JsonProperty("replyCount") int replyCount,
    @JsonProperty("status") String status
) {
}