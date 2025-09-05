package com.backend.immilog.shared.domain.event;

import java.util.List;

import static com.backend.immilog.shared.domain.event.DomainEventTypes.INTERACTION_DATA_REQUESTED;

public class InteractionDataRequestedEvent extends StandardDomainEvent {

    private final String requestId;
    private final List<String> contentIds;
    private final String contentType;
    private final String requestingDomain;

    public InteractionDataRequestedEvent(
            String requestId, 
            List<String> contentIds,
            String contentType,
            String requestingDomain
    ) {
        super(INTERACTION_DATA_REQUESTED, contentIds.isEmpty() ? null : contentIds.get(0));
        this.requestId = requestId;
        this.contentIds = contentIds;
        this.contentType = contentType;
        this.requestingDomain = requestingDomain;
    }

    public String getRequestId() {
        return requestId;
    }

    public List<String> getContentIds() {
        return contentIds;
    }

    public String getContentType() {
        return contentType;
    }

    public String getRequestingDomain() {
        return requestingDomain;
    }

    @Override
    public String toString() {
        return String.format("InteractionDataRequestedEvent{requestId='%s', contentIds=%s, contentType='%s', requestingDomain='%s'}",
                requestId, contentIds, contentType, requestingDomain);
    }
}