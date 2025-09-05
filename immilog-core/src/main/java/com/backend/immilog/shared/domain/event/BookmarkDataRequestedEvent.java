package com.backend.immilog.shared.domain.event;

import static com.backend.immilog.shared.domain.event.DomainEventTypes.INTERACTION_DATA_REQUESTED;

public class BookmarkDataRequestedEvent extends StandardDomainEvent {

    private final String requestId;
    private final String userId;
    private final String contentType;
    private final String requestingDomain;

    public BookmarkDataRequestedEvent(
            String requestId, 
            String userId,
            String contentType,
            String requestingDomain
    ) {
        super(INTERACTION_DATA_REQUESTED, userId);
        this.requestId = requestId;
        this.userId = userId;
        this.contentType = contentType;
        this.requestingDomain = requestingDomain;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getUserId() {
        return userId;
    }

    public String getContentType() {
        return contentType;
    }

    public String getRequestingDomain() {
        return requestingDomain;
    }

    @Override
    public String toString() {
        return String.format("BookmarkDataRequestedEvent{requestId='%s', userId='%s', contentType='%s', requestingDomain='%s'}",
                requestId, userId, contentType, requestingDomain);
    }
}