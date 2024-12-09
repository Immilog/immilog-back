package com.backend.immilog.post.application.result;

import com.backend.immilog.post.domain.model.Comment;
import com.backend.immilog.post.domain.model.enums.PostStatus;
import com.backend.immilog.user.application.result.UserInfoResult;
import com.backend.immilog.user.domain.model.user.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Builder
public record CommentResult(
        Long seq,
        UserInfoResult user,
        String content,
        List<CommentResult> replies,
        int upVotes,
        int downVotes,
        int replyCount,
        List<Long> likeUsers,
        PostStatus status,
        LocalDateTime createdAt
) {
    public static CommentResult of(
            Comment comment,
            User user
    ) {
        return CommentResult.builder()
                .seq(comment.seq())
                .user(UserInfoResult.from(user))
                .content(comment.content())
                .replies(new ArrayList<>())
                .upVotes(comment.likeCount())
                .replyCount(comment.replyCount())
                .likeUsers(comment.likeUsers())
                .status(comment.status())
                .createdAt(comment.createdAt())
                .build();
    }

    private List<CommentResult> combineReplies(
            List<Comment> replies,
            List<User> replyUsers
    ) {
        if (replies.isEmpty() || replyUsers.isEmpty()) {
            return List.of();
        }
        return replies
                .stream()
                .map(reply -> {
                    return replyUsers.stream()
                            .filter(Objects::nonNull)
                            .filter(u -> u.getSeq().equals(reply.userSeq()))
                            .findFirst()
                            .map(replyUser -> CommentResult.of(reply, replyUser))
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();
    }

}
