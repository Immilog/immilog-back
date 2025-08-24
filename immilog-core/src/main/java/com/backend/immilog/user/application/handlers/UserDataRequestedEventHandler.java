package com.backend.immilog.user.application.handlers;

import com.backend.immilog.post.domain.events.PostEvent;
import com.backend.immilog.shared.domain.event.DomainEventHandler;
import com.backend.immilog.shared.domain.model.UserData;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import com.backend.immilog.user.application.services.query.UserQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserDataRequestedEventHandler implements DomainEventHandler<PostEvent.UserDataRequested> {

    private final UserQueryService userQueryService;
    private final EventResultStorageService eventResultStorageService;

    public UserDataRequestedEventHandler(
            UserQueryService userQueryService,
            EventResultStorageService eventResultStorageService) {
        this.userQueryService = userQueryService;
        this.eventResultStorageService = eventResultStorageService;
    }

    @Override
    public void handle(PostEvent.UserDataRequested event) {
        log.debug("Processing UserDataRequested event for userIds: {}", event.getUserIds());
        
        try {
            // User 도메인 서비스를 통해 데이터 조회
            List<UserData> userDataList = event.getUserIds().stream()
                    .map(userId -> {
                        try {
                            var user = userQueryService.getUserById(userId);
                            return new UserData(
                                    user.getUserId().value(),
                                    user.getNickname(),
                                    user.getImageUrl()
                            );
                        } catch (Exception e) {
                            log.warn("Failed to get user data for userId: {}", userId, e);
                            return new UserData(userId, "Unknown", null);
                        }
                    })
                    .toList();
            
            // Redis에 결과 저장
            eventResultStorageService.storeUserData(event.getRequestId(), userDataList);
            log.debug("Successfully processed and stored {} user data records with requestId: {}", 
                    userDataList.size(), event.getRequestId());
            
        } catch (Exception e) {
            log.error("Failed to process UserDataRequested event", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<PostEvent.UserDataRequested> getEventType() {
        return PostEvent.UserDataRequested.class;
    }
}