package com.backend.immilog.shared.domain.event;

import java.util.List;

import static com.backend.immilog.shared.domain.event.DomainEventTypes.COMMENT_DATA_REQUESTED;

public class CommentDataRequestedEvent extends StandardDomainEvent {

    private final String requestId;
    private final List<String> postIds;
    private final String requestingDomain;

    public CommentDataRequestedEvent(
            String requestId, 
            List<String> postIds, 
            String requestingDomain
    ) {
        super(COMMENT_DATA_REQUESTED, postIds.isEmpty() ? null : postIds.get(0));
        this.requestId = requestId;
        this.postIds = postIds;
        this.requestingDomain = requestingDomain;
    }

    public String getRequestId() {
        return requestId;
    }

    public List<String> getPostIds() {
        return postIds;
    }

    public String getRequestingDomain() {
        return requestingDomain;
    }

    @Override
    public String toString() {
        return String.format("CommentDataRequestedEvent{requestId='%s', postIds=%s, requestingDomain='%s'}",
                requestId, postIds, requestingDomain);
    }
}