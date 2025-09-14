package com.backend.immilog.comment.application.services;

import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.comment.domain.repositories.CommentRepository;
import com.backend.immilog.comment.presentation.payload.CommentResponse;
import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import com.backend.immilog.post.domain.events.PostEvent;
import com.backend.immilog.shared.domain.event.DomainEvents;
import com.backend.immilog.shared.domain.model.UserData;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentQueryService {
    private final CommentRepository commentRepository;
    private final InteractionUserRepository interactionUserRepository;
    private final EventResultStorageService eventResultStorageService;

    public CommentQueryService(
            CommentRepository commentRepository,
            InteractionUserRepository interactionUserRepository,
            EventResultStorageService eventResultStorageService
    ) {
        this.commentRepository = commentRepository;
        this.interactionUserRepository = interactionUserRepository;
        this.eventResultStorageService = eventResultStorageService;
    }

    public List<CommentResult> getCommentsByPostId(String postId) {
        return commentRepository.findCommentsByPostId(postId);
    }

    public List<CommentResponse.CommentInformation> getHierarchicalCommentsByPostId(String postId) {
        var comments = commentRepository.findCommentsByPostId(postId);
        
        // 유저 정보 요청을 위해 고유한 userId 목록 추출
        var userIds = comments.stream()
                .map(CommentResult::userId)
                .distinct()
                .toList();
        
        if (!userIds.isEmpty()) {
            // 이벤트를 통해 유저 데이터 요청
            String userRequestId = eventResultStorageService.generateRequestId("user");
            log.info("Requesting user data for {} comment users with requestId: {}, userIds: {}", userIds.size(), userRequestId, userIds);
            eventResultStorageService.registerEventProcessing(userRequestId);
            DomainEvents.raise(new PostEvent.UserDataRequested(userRequestId, userIds));
            var userData = eventResultStorageService.waitForUserData(userRequestId, java.time.Duration.ofSeconds(2));
            log.info("Retrieved {} user data items via event for requestId: {}", userData.size(), userRequestId);
            
            return buildCommentHierarchy(comments, userData);
        }
        
        return buildCommentHierarchy(comments, List.of());
    }

    private List<CommentResponse.CommentInformation> buildCommentHierarchy(List<CommentResult> comments) {
        return buildCommentHierarchy(comments, List.of());
    }
    
    private List<CommentResponse.CommentInformation> buildCommentHierarchy(List<CommentResult> comments, List<UserData> userData) {
        // parentId별로 그룹핑
        Map<String, List<CommentResult>> commentsByParentId = comments.stream()
                .collect(Collectors.groupingBy(comment -> 
                    comment.parentId() != null ? comment.parentId() : "ROOT"));

        // userId로 UserData 맵 생성
        Map<String, UserData> userDataMap = userData.stream()
                .collect(Collectors.toMap(UserData::userId, user -> user, (existing, replacement) -> existing));

        // 최상위 댓글들 (parentId가 null인 것들)
        List<CommentResult> rootComments = commentsByParentId.getOrDefault("ROOT", new ArrayList<>());

        // 계층구조 빌드
        return rootComments.stream()
                .map(rootComment -> buildCommentWithReplies(rootComment, commentsByParentId, userDataMap))
                .toList();
    }

    private CommentResponse.CommentInformation buildCommentWithReplies(
            CommentResult comment, 
            Map<String, List<CommentResult>> commentsByParentId
    ) {
        return buildCommentWithReplies(comment, commentsByParentId, Map.of());
    }
    
    private CommentResponse.CommentInformation buildCommentWithReplies(
            CommentResult comment, 
            Map<String, List<CommentResult>> commentsByParentId,
            Map<String, UserData> userDataMap
    ) {
        // 현재 댓글의 대댓글들 조회
        List<CommentResult> replies = commentsByParentId.getOrDefault(comment.id(), new ArrayList<>());
        
        // 대댓글들을 재귀적으로 처리
        List<CommentResponse.CommentInformation> replyInfos = replies.stream()
                .map(reply -> buildCommentWithReplies(reply, commentsByParentId, userDataMap))
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

        // 이벤트로 받은 유저 데이터 사용, 없으면 기존 데이터 사용
        var userData = userDataMap.get(comment.userId());
        String nickname = userData != null ? userData.nickname() : comment.nickname();
        String profileImageUrl = userData != null ? userData.profileImageUrl() : comment.userProfileUrl();
        
        // CommentInformation 생성
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

    public CommentResult getCommentByCommentId(String commentId){
        return commentRepository.getCommentById(commentId);
    }

    public long getCommentCountByPostId(String postId) {
        return commentRepository.findCommentsByPostId(postId).size();
    }

    public Map<String, Long> getCommentCountsByPostIds(List<String> postIds) {
        return postIds.stream()
                .distinct()
                .collect(Collectors.toMap(
                        postId -> postId,
                        this::getCommentCountByPostId
                ));
    }
}
