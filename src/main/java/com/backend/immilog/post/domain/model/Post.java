package com.backend.immilog.post.domain.model;

import com.backend.immilog.post.application.command.PostUploadCommand;
import com.backend.immilog.post.domain.model.enums.Categories;
import com.backend.immilog.post.domain.vo.PostMetaData;
import com.backend.immilog.post.domain.vo.PostUserData;
import com.backend.immilog.user.domain.model.user.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Post(
        Long seq,
        PostUserData postUserData,
        PostMetaData postMetaData,
        Categories category,
        String isPublic,
        Long commentCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static Post of(
            PostUploadCommand postUploadCommand,
            User user
    ) {
        PostMetaData postMetaData = PostMetaData.of(
                postUploadCommand.title(),
                postUploadCommand.content(),
                user.getCountry(),
                user.getRegion()
        );

        PostUserData postUserData = PostUserData.builder()
                .userSeq(user.getSeq())
                .profileImage(user.getImageUrl())
                .nickname(user.getNickName())
                .build();

        return Post.builder()
                .postUserData(postUserData)
                .postMetaData(postMetaData)
                .category(postUploadCommand.category())
                .isPublic(postUploadCommand.isPublic() ? "Y" : "N")
                .commentCount(0L)
                .build();
    }

    public Post copyWithNewCommentCount(
            long commentCount
    ) {
        return buildPost(commentCount, PostColumn.COMMENT_COUNT);
    }

    public Post copyWithNewIsPublic(
            String isPublic
    ) {
        return buildPost(isPublic, PostColumn.IS_PUBLIC);
    }

    public Post copyWithNewContent(
            String content
    ) {
        return buildPost(content, PostColumn.CONTENT);
    }

    public Post copyWithNewTitle(
            String title
    ) {
        return buildPost(title, PostColumn.TITLE);
    }

    private <T> Post buildPost(
            T item,
            PostColumn postColumn
    ) {
        PostMetaData metaData = this.postMetaData();
        String isPublic = this.isPublic();
        Long commentCount = this.commentCount;
        switch (postColumn) {
            case TITLE -> metaData.setTitle((String) item);
            case CONTENT -> metaData.setContent((String) item);
            case IS_PUBLIC -> isPublic = (String) item;
            case COMMENT_COUNT -> commentCount = (Long) item;
        }
        return Post.builder()
                .seq(this.seq())
                .postUserData(this.postUserData())
                .postMetaData(metaData)
                .category(this.category())
                .isPublic(isPublic)
                .commentCount(commentCount)
                .build();

    }

    private enum PostColumn {
        TITLE,
        CONTENT,
        COMMENT_COUNT, IS_PUBLIC
    }
}
