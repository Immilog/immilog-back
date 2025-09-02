package com.backend.immilog.shared.domain.service;

import com.backend.immilog.shared.domain.model.InteractionData;
import com.backend.immilog.shared.enums.ContentType;

import java.util.List;

public interface InteractionDataProvider {
    
    InteractionData getInteractionData(String contentId, ContentType contentType);
    
    List<InteractionData> getInteractionDataBatch(List<String> contentIds, ContentType contentType);
    
    InteractionData getUserInteractionData(String userId, String contentId, ContentType contentType);
}