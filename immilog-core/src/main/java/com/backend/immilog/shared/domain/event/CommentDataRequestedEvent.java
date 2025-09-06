package com.backend.immilog.shared.domain.event;

import lombok.Getter;

import java.util.List;

import static com.backend.immilog.shared.domain.event.DomainEventTypes.COMMENT_DATA_REQUESTED;

@Getter
public class CommentDataRequestedEvent extends StandardDomainEvent {

    private String requestId;
    private List<String> postIds;
    private String requestingDomain;

    private CommentDataRequestedEvent() {
        super();
    }

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

    @Override
    public String toString() {
        return String.format("CommentDataRequestedEvent{requestId='%s', postIds=%s, requestingDomain='%s'}",
                requestId, postIds, requestingDomain);
    }
}