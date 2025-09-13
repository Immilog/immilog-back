package com.backend.immilog.user.application.handlers;

import com.backend.immilog.post.domain.events.PostEvent;
import com.backend.immilog.shared.domain.event.DomainEventHandler;
import com.backend.immilog.shared.domain.model.UserData;
import com.backend.immilog.shared.infrastructure.event.EventResultStorageService;
import com.backend.immilog.user.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDataRequestedEventHandler implements DomainEventHandler<PostEvent.UserDataRequested> {

    private final UserRepository userRepository;
    private final EventResultStorageService eventResultStorageService;

    @Override
    public void handle(PostEvent.UserDataRequested event) {
        log.info("Processing UserDataRequested event for requestId: {}, userIds: {}", event.getRequestId(), event.getUserIds());
        
        try {
            var userDataList = event.getUserIds().stream()
                    .map(userId -> {
                        try {
                            var user = userRepository.findById(userId);
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
            log.info("Successfully processed and stored {} user data records with requestId: {}", userDataList.size(), event.getRequestId());
            
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