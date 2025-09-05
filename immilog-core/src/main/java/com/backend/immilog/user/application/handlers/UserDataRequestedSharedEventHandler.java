package com.backend.immilog.user.application.handlers;

import com.backend.immilog.shared.domain.event.DomainEventHandler;
import com.backend.immilog.shared.domain.event.UserDataRequestedEvent;
import com.backend.immilog.shared.domain.model.UserData;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import com.backend.immilog.user.application.services.query.UserQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserDataRequestedSharedEventHandler implements DomainEventHandler<UserDataRequestedEvent> {

    private final UserQueryService userQueryService;
    private final EventResultStorageService eventResultStorageService;

    public UserDataRequestedSharedEventHandler(
            UserQueryService userQueryService,
            EventResultStorageService eventResultStorageService) {
        this.userQueryService = userQueryService;
        this.eventResultStorageService = eventResultStorageService;
    }

    @Override
    public void handle(UserDataRequestedEvent event) {
        log.debug("Processing shared UserDataRequestedEvent for userIds: {} from domain: {}", 
                event.getUserIds(), event.getRequestingDomain());
        
        try {
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
            
            eventResultStorageService.storeUserData(event.getRequestId(), userDataList);
            log.debug("Successfully processed and stored {} user data records with requestId: {} from domain: {}", 
                    userDataList.size(), event.getRequestId(), event.getRequestingDomain());
            
        } catch (Exception e) {
            log.error("Failed to process shared UserDataRequestedEvent from domain: {}", event.getRequestingDomain(), e);
        }
    }

    @Override
    public Class<UserDataRequestedEvent> getEventType() {
        return UserDataRequestedEvent.class;
    }
}