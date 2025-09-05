package com.backend.immilog.shared.domain.event;

import com.backend.immilog.shared.enums.ContentType;

import static com.backend.immilog.shared.domain.event.DomainEventTypes.POST_CREATED;

/**
 * 게시물 검증 요청 이벤트
 * Comment 도메인에서 Post 존재 여부 검증을 위한 이벤트
 */
public class PostValidationRequestedEvent extends StandardDomainEvent {
    
    private final String requestId;
    private final String postId;
    private final ContentType contentType;
    private final String requestingDomain;
    
    public PostValidationRequestedEvent(String requestId, String postId, ContentType contentType, String requestingDomain) {
        super(POST_CREATED, postId);
        this.requestId = requestId;
        this.postId = postId;
        this.contentType = contentType;
        this.requestingDomain = requestingDomain;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public String getPostId() {
        return postId;
    }
    
    public ContentType getContentType() {
        return contentType;
    }
    
    public String getRequestingDomain() {
        return requestingDomain;
    }
    
    @Override
    public String toString() {
        return String.format("PostValidationRequestedEvent{requestId='%s', postId='%s', contentType=%s, requestingDomain='%s'}",
                requestId, postId, contentType, requestingDomain);
    }
}