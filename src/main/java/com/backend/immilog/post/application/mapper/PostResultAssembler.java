package com.backend.immilog.post.application.mapper;

import com.backend.immilog.post.application.dto.PostResult;
import com.backend.immilog.shared.domain.model.InteractionData;
import com.backend.immilog.shared.domain.model.Resource;
import com.backend.immilog.shared.domain.model.ResourceType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostResultAssembler {

    public PostResult assembleKeywords(
            PostResult postResult,
            String keyword
    ) {
        if (keyword == null) {
            return postResult;
        }
        var updatedTags = new ArrayList<>(postResult.tags());
        updatedTags.addAll(extractTags(updatedTags, keyword));
        return new PostResult(
                postResult.postId(),
                postResult.userId(),
                postResult.userProfileUrl(),
                postResult.userNickname(),
                postResult.commentCount(),
                postResult.viewCount(),
                postResult.likeCount(),
                updatedTags,
                new ArrayList<>(postResult.attachments()),
                new ArrayList<>(postResult.likeUsers()),
                new ArrayList<>(postResult.bookmarkUsers()),
                postResult.isPublic(),
                postResult.country(),
                postResult.region(),
                postResult.category(),
                postResult.status(),
                postResult.createdAt(),
                postResult.updatedAt(),
                extractKeyword(postResult.title(), keyword, 20, 5),
                extractKeyword(postResult.content(), keyword, 50, 5),
                keyword
        );
    }

    public PostResult assembleInteractionData(
            PostResult postResult,
            List<InteractionData> interactionData
    ) {
        if (interactionData == null || interactionData.isEmpty()) {
            return postResult;
        }
        var newLikeUsers = new ArrayList<>(postResult.likeUsers());
        var newBookmarkUsers = new ArrayList<>(postResult.bookmarkUsers());
        newLikeUsers.addAll(interactionData.stream()
                .filter(u -> "LIKE".equals(u.interactionType()) && "ACTIVE".equals(u.interactionStatus()))
                .map(InteractionData::userId)
                .toList());
        newBookmarkUsers.addAll(interactionData.stream()
                .filter(u -> "BOOKMARK".equals(u.interactionType()) && "ACTIVE".equals(u.interactionStatus()))
                .map(InteractionData::userId)
                .toList());
        return new PostResult(
                postResult.postId(),
                postResult.userId(),
                postResult.userProfileUrl(),
                postResult.userNickname(),
                postResult.commentCount(),
                postResult.viewCount(),
                postResult.likeCount(),
                postResult.tags(),
                postResult.attachments(),
                newLikeUsers,
                newBookmarkUsers,
                postResult.isPublic(),
                postResult.country(),
                postResult.region(),
                postResult.category(),
                postResult.status(),
                postResult.createdAt(),
                postResult.updatedAt(),
                postResult.title(),
                postResult.content(),
                postResult.keyword()
        );
    }

    public PostResult assembleResources(
            PostResult postResult,
            List<Resource> resources
    ) {
        if (resources == null || resources.isEmpty()) {
            return postResult;
        }

        var updatedTags = new ArrayList<>(postResult.tags());
        updatedTags.addAll(resources.stream()
                .filter(r -> r.resourceType() == ResourceType.TAG)
                .map(Resource::content)
                .toList());

        var updatedAttachments = new ArrayList<>(postResult.attachments());
        updatedAttachments.addAll(resources.stream()
                .filter(r -> r.resourceType() == ResourceType.ATTACHMENT)
                .map(Resource::content)
                .toList());
        return new PostResult(
                postResult.postId(),
                postResult.userId(),
                postResult.userProfileUrl(),
                postResult.userNickname(),
                postResult.commentCount(),
                postResult.viewCount(),
                postResult.likeCount(),
                updatedTags,
                updatedAttachments,
                postResult.likeUsers(),
                postResult.bookmarkUsers(),
                postResult.isPublic(),
                postResult.country(),
                postResult.region(),
                postResult.category(),
                postResult.status(),
                postResult.createdAt(),
                postResult.updatedAt(),
                postResult.title(),
                postResult.content(),
                postResult.keyword()
        );
    }

    public PostResult assembleLikeCount(
            PostResult postResult,
            long likeCount
    ) {
        return new PostResult(
                postResult.postId(),
                postResult.userId(),
                postResult.userProfileUrl(),
                postResult.userNickname(),
                postResult.commentCount(),
                postResult.viewCount(),
                likeCount,
                postResult.tags(),
                postResult.attachments(),
                postResult.likeUsers(),
                postResult.bookmarkUsers(),
                postResult.isPublic(),
                postResult.country(),
                postResult.region(),
                postResult.category(),
                postResult.status(),
                postResult.createdAt(),
                postResult.updatedAt(),
                postResult.title(),
                postResult.content(),
                postResult.keyword()
        );
    }


    private static String extractKeyword(
            String text,
            String keyword,
            int after,
            int before
    ) {
        if (text == null || keyword == null) {
            return "";
        }
        int keywordIndex = text.indexOf(keyword);
        if (keywordIndex == -1) {
            return text.substring(0, Math.min(text.length(), after));
        }
        int start = Math.max(keywordIndex - before, 0);
        int end = Math.min(keywordIndex + keyword.length() + after, text.length());
        return text.substring(start, end);
    }

    private static List<String> extractTags(
            List<String> tags,
            String keyword
    ) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList();
        }

        var keywordTags = tags.stream()
                .filter(tag -> tag.contains(keyword))
                .limit(1)
                .toList();

        var shuffledTags = tags.stream()
                .filter(tag -> !keywordTags.contains(tag))
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    Collections.shuffle(list);
                    return list.stream().limit(keywordTags.isEmpty() ? 3 : 2).toList();
                }));

        return new ArrayList<>(keywordTags) {{
            addAll(shuffledTags);
        }};
    }
}
