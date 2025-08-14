package com.backend.immilog.comment.application.services;

import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.comment.domain.repositories.CommentRepository;
import com.backend.immilog.comment.presentation.payload.CommentResponse;
import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentQueryService {
    private final CommentRepository commentRepository;
    private final InteractionUserRepository interactionUserRepository;

    public CommentQueryService(CommentRepository commentRepository, InteractionUserRepository interactionUserRepository) {
        this.commentRepository = commentRepository;
        this.interactionUserRepository = interactionUserRepository;
    }

    public List<CommentResult> getCommentsByPostId(String postId) {
        return commentRepository.findCommentsByPostId(postId);
    }

    public List<CommentResponse.CommentInformation> getHierarchicalCommentsByPostId(String postId) {
        var comments = commentRepository.findCommentsByPostId(postId);
        return buildCommentHierarchy(comments);
    }

    private List<CommentResponse.CommentInformation> buildCommentHierarchy(List<CommentResult> comments) {
        // parentId별로 그룹핑
        Map<String, List<CommentResult>> commentsByParentId = comments.stream()
                .collect(Collectors.groupingBy(comment -> 
                    comment.parentId() != null ? comment.parentId() : "ROOT"));

        // 최상위 댓글들 (parentId가 null인 것들)
        List<CommentResult> rootComments = commentsByParentId.getOrDefault("ROOT", new ArrayList<>());

        // 계층구조 빌드
        return rootComments.stream()
                .map(rootComment -> buildCommentWithReplies(rootComment, commentsByParentId))
                .toList();
    }

    private CommentResponse.CommentInformation buildCommentWithReplies(
            CommentResult comment, 
            Map<String, List<CommentResult>> commentsByParentId
    ) {
        // 현재 댓글의 대댓글들 조회
        List<CommentResult> replies = commentsByParentId.getOrDefault(comment.id(), new ArrayList<>());
        
        // 대댓글들을 재귀적으로 처리
        List<CommentResponse.CommentInformation> replyInfos = replies.stream()
                .map(reply -> buildCommentWithReplies(reply, commentsByParentId))
                .toList();

        // 댓글 좋아요 수 실시간 계산
        Long likeCount = interactionUserRepository.countByCommentIdAndInteractionTypeAndInteractionStatus(
                comment.id(),
                InteractionType.LIKE,
                InteractionStatus.ACTIVE
        );

        // 댓글 좋아요 사용자 목록 조회
        var likeUsers = interactionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                List.of(comment.id()),
                com.backend.immilog.shared.enums.ContentType.COMMENT,
                InteractionStatus.ACTIVE
        ).stream()
                .filter(interaction -> InteractionType.LIKE.equals(interaction.interactionType()))
                .map(interaction -> interaction.userId())
                .toList();

        // 댓글 북마크 사용자 목록 조회
        var bookmarkUsers = interactionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                List.of(comment.id()),
                com.backend.immilog.shared.enums.ContentType.COMMENT,
                InteractionStatus.ACTIVE
        ).stream()
                .filter(interaction -> InteractionType.BOOKMARK.equals(interaction.interactionType()))
                .map(interaction -> interaction.userId())
                .toList();

        // CommentInformation 생성
        return new CommentResponse.CommentInformation(
                comment.id(),
                comment.userId(),
                comment.nickname(),
                comment.userProfileUrl(),
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

    public CommentResult getCommentByCommentId(String commentId){
        return commentRepository.getCommentById(commentId);
    }
}
