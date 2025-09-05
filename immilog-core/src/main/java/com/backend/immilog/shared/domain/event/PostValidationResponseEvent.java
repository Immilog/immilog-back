package com.backend.immilog.shared.domain.event;

import static com.backend.immilog.shared.domain.event.DomainEventTypes.POST_CREATED;

/**
 * 게시물 검증 응답 이벤트
 * Post 도메인에서 게시물 검증 결과를 응답하는 이벤트
 */
public class PostValidationResponseEvent extends StandardDomainEvent {
    
    private final String requestId;
    private final String postId;
    private final boolean isValid;
    private final boolean isPublic;
    private final boolean isActive;
    private final String errorMessage;
    
    public PostValidationResponseEvent(String requestId, String postId, boolean isValid, 
                                     boolean isPublic, boolean isActive) {
        this(requestId, postId, isValid, isPublic, isActive, null);
    }
    
    public PostValidationResponseEvent(String requestId, String postId, boolean isValid, 
                                     boolean isPublic, boolean isActive, String errorMessage) {
        super(POST_CREATED, postId);
        this.requestId = requestId;
        this.postId = postId;
        this.isValid = isValid;
        this.isPublic = isPublic;
        this.isActive = isActive;
        this.errorMessage = errorMessage;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public String getPostId() {
        return postId;
    }
    
    public boolean isValid() {
        return isValid;
    }
    
    public boolean isPublic() {
        return isPublic;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public boolean hasError() {
        return errorMessage != null;
    }
    
    @Override
    public String toString() {
        return String.format("PostValidationResponseEvent{requestId='%s', postId='%s', isValid=%s, isPublic=%s, isActive=%s, errorMessage='%s'}",
                requestId, postId, isValid, isPublic, isActive, errorMessage);
    }
}