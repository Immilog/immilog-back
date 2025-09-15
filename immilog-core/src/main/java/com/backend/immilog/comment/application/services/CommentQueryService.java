package com.backend.immilog.comment.application.services;

import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.comment.domain.repositories.CommentRepository;
import com.backend.immilog.comment.presentation.payload.CommentResponse;
import com.backend.immilog.post.domain.events.PostEvent;
import com.backend.immilog.shared.domain.event.DomainEvents;
import com.backend.immilog.shared.domain.model.UserData;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentQueryService {
    private final CommentRepository commentRepository;
    private final CommentHierarchyService commentHierarchyService;
    private final EventResultStorageService eventResultStorageService;

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
            
            return commentHierarchyService.buildHierarchy(comments, userData);
        }
        
        return commentHierarchyService.buildHierarchy(comments, List.of());
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
