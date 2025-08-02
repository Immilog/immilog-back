package com.backend.immilog.interaction.application.handlers;

import com.backend.immilog.interaction.application.services.InteractionUserQueryService;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.post.domain.events.PostEvent;
import com.backend.immilog.shared.enums.ContentType;
import com.backend.immilog.shared.domain.model.InteractionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PostEventHandler {

    private final InteractionUserQueryService interactionUserQueryService;

    public PostEventHandler(InteractionUserQueryService interactionUserQueryService) {
        this.interactionUserQueryService = interactionUserQueryService;
    }

    @EventListener
    public void handleInteractionDataRequested(PostEvent.InteractionDataRequested event) {
        log.debug("Processing InteractionDataRequested event for postIds: {}", event.getPostIds());
        
        try {
            // Interaction 도메인 서비스를 통해 데이터 조회
            List<InteractionUser> interactionUsers = interactionUserQueryService
                    .getInteractionUsersByPostIdList(event.getPostIds(), ContentType.valueOf(event.getContentType()));
            
            // InteractionUser를 InteractionData로 변환
            List<InteractionData> interactionDataList = interactionUsers.stream()
                    .map(this::convertToInteractionData)
                    .toList();
            
            // TODO: 실제 구현에서는 결과를 어딘가에 저장해야 함 (Redis, ThreadLocal, 등)
            log.debug("Successfully processed {} interaction records", interactionDataList.size());
            
        } catch (Exception e) {
            log.error("Failed to process InteractionDataRequested event", e);
        }
    }

    @EventListener
    public void handleBookmarkPostsRequested(PostEvent.BookmarkPostsRequested event) {
        log.debug("Processing BookmarkPostsRequested event for userId: {}", event.getUserId());
        
        try {
            // Interaction 도메인 서비스를 통해 북마크 데이터 조회
            List<InteractionUser> bookmarkInteractions = interactionUserQueryService
                    .getBookmarkInteractions(event.getUserId(), ContentType.valueOf(event.getContentType()));
            
            List<String> postIds = bookmarkInteractions.stream()
                    .map(InteractionUser::postId)
                    .toList();
            
            // TODO: 실제 구현에서는 결과를 어딘가에 저장해야 함 (Redis, ThreadLocal, 등)
            log.debug("Successfully processed {} bookmark records", postIds.size());
            
        } catch (Exception e) {
            log.error("Failed to process BookmarkPostsRequested event", e);
        }
    }
    
    private InteractionData convertToInteractionData(InteractionUser interactionUser) {
        return new InteractionData(
                interactionUser.id(),
                interactionUser.postId(),
                interactionUser.userId(),
                interactionUser.interactionType().name(),
                interactionUser.contentType().name()
        );
    }
}