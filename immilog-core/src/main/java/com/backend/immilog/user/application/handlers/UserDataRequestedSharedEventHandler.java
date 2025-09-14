package com.backend.immilog.user.application.handlers;

import com.backend.immilog.shared.domain.event.DomainEventHandler;
import com.backend.immilog.shared.domain.event.UserDataRequestedEvent;
import com.backend.immilog.shared.domain.model.UserData;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import com.backend.immilog.user.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDataRequestedSharedEventHandler implements DomainEventHandler<UserDataRequestedEvent> {

    private final UserRepository userRepository;
    private final EventResultStorageService eventResultStorageService;

    @Override
    public void handle(UserDataRequestedEvent event) {
        log.debug("Processing shared UserDataRequestedEvent for userIds: {} from domain: {}", 
                event.getUserIds(), event.getRequestingDomain());
        
        try {
            var userDataList = event.getUserIds().stream()
                    .map(userId -> {
                        try {
                            var user = userRepository.findById(userId);
                            return new UserData(user.getUserId().value(), user.getNickname(), user.getImageUrl(), user.getCountryId(), user.getRegion());
                        } catch (Exception e) {
                            log.warn("Failed to get user data for userId: {}", userId, e);
                            return new UserData(userId, "Unknown", null, null, null);
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