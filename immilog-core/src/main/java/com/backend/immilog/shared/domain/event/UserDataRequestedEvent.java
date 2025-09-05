package com.backend.immilog.shared.domain.event;

import java.time.LocalDateTime;
import java.util.List;

import static com.backend.immilog.shared.domain.event.DomainEventTypes.USER_DATA_REQUESTED;

public class UserDataRequestedEvent extends StandardDomainEvent {

    private final String requestId;
    private final List<String> userIds;
    private final String requestingDomain;

    public UserDataRequestedEvent(
            String requestId, 
            List<String> userIds, 
            String requestingDomain
    ) {
        super(USER_DATA_REQUESTED, userIds.isEmpty() ? null : userIds.get(0));
        this.requestId = requestId;
        this.userIds = userIds;
        this.requestingDomain = requestingDomain;
    }

    public String getRequestId() {
        return requestId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public String getRequestingDomain() {
        return requestingDomain;
    }

    @Override
    public String toString() {
        return String.format("UserDataRequestedEvent{requestId='%s', userIds=%s, requestingDomain='%s'}",
                requestId, userIds, requestingDomain);
    }
}