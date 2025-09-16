package com.backend.immilog.comment.application.services;

import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.comment.presentation.payload.CommentResponse;
import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import com.backend.immilog.shared.domain.model.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentHierarchyService {
    
    private final InteractionUserRepository interactionUserRepository;
    
    public List<CommentResponse.CommentInformation> buildHierarchy(List<CommentResult> comments, List<UserData> userData) {
        var commentsByParentId = comments.stream()
                .collect(Collectors.groupingBy(comment -> 
                    comment.parentId() != null ? comment.parentId() : "ROOT"));

        var userDataMap = userData.stream()
                .collect(Collectors.toMap(UserData::userId, user -> user, (existing, replacement) -> existing));

        var rootComments = commentsByParentId.getOrDefault("ROOT", new ArrayList<>());

        return rootComments.stream()
                .map(rootComment -> buildCommentWithReplies(rootComment, commentsByParentId, userDataMap))
                .toList();
    }
    
    private CommentResponse.CommentInformation buildCommentWithReplies(
            CommentResult comment, 
            Map<String, List<CommentResult>> commentsByParentId,
            Map<String, UserData> userDataMap
    ) {
        var replies = commentsByParentId.getOrDefault(comment.id(), new ArrayList<>());
        
        var replyInfos = replies.stream()
                .map(reply -> buildCommentWithReplies(reply, commentsByParentId, userDataMap))
                .toList();

        var interactions = interactionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                List.of(comment.id()),
                com.backend.immilog.shared.enums.ContentType.COMMENT,
                InteractionStatus.ACTIVE
        );

        var likeUsers = interactions.stream()
                .filter(interaction -> InteractionType.LIKE.equals(interaction.interactionType()))
                .map(interaction -> interaction.userId())
                .toList();

        var bookmarkUsers = interactions.stream()
                .filter(interaction -> InteractionType.BOOKMARK.equals(interaction.interactionType()))
                .map(interaction -> interaction.userId())
                .toList();

        Long likeCount = (long) likeUsers.size();

        var userData = userDataMap.get(comment.userId());
        String nickname = userData != null ? userData.nickname() : comment.nickname();
        String profileImageUrl = userData != null ? userData.profileImageUrl() : comment.userProfileUrl();
        
        return new CommentResponse.CommentInformation(
                comment.id(),
                comment.userId(),
                nickname,
                profileImageUrl,
                comment.countryId(),
                comment.region(),
                comment.content(),
                comment.postId(),
                comment.parentId(),
                comment.referenceType(),
                comment.replyCount(),
                likeCount.intValue(),
                likeUsers,
                bookmarkUsers,
                comment.status(),
                comment.createdAt(),
                comment.updatedAt(),
                replyInfos
        );
    }
}