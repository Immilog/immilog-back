package com.backend.immilog.shared.domain.service;

import com.backend.immilog.shared.enums.ContentType;

public interface ContentValidator {
    
    boolean existsContent(String contentId, ContentType contentType);
    
    boolean canUserAccess(String userId, String contentId, ContentType contentType);
    
    boolean isPublicContent(String contentId, ContentType contentType);
    
    boolean isActiveContent(String contentId, ContentType contentType);
    
    default boolean isValidContent(String userId, String contentId, ContentType contentType) {
        return existsContent(contentId, contentType) 
            && isActiveContent(contentId, contentType)
            && (userId == null || canUserAccess(userId, contentId, contentType));
    }
}