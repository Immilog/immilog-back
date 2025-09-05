package com.backend.immilog.shared.application.gateway;

import com.backend.immilog.shared.enums.ContentType;

/**
 * 데이터 조합 요청 정보를 담는 클래스
 * 어떤 데이터를 조합할지 결정하는 설정
 */
public class DataEnrichmentRequest {
    
    private final String userId;
    private final String contentId;
    private final ContentType contentType;
    private final boolean includeUserData;
    private final boolean includeInteractionData;
    private final boolean includeUserInteraction;
    private final boolean useCache;
    private final long timeoutMillis;
    
    private DataEnrichmentRequest(Builder builder) {
        this.userId = builder.userId;
        this.contentId = builder.contentId;
        this.contentType = builder.contentType;
        this.includeUserData = builder.includeUserData;
        this.includeInteractionData = builder.includeInteractionData;
        this.includeUserInteraction = builder.includeUserInteraction;
        this.useCache = builder.useCache;
        this.timeoutMillis = builder.timeoutMillis;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters
    public String getUserId() { return userId; }
    public String getContentId() { return contentId; }
    public ContentType getContentType() { return contentType; }
    public boolean includeUserData() { return includeUserData; }
    public boolean includeInteractionData() { return includeInteractionData; }
    public boolean includeUserInteraction() { return includeUserInteraction; }
    public boolean useCache() { return useCache; }
    public long getTimeoutMillis() { return timeoutMillis; }
    
    // 편의 메서드들
    public static DataEnrichmentRequest userDataOnly(String userId) {
        return builder().userId(userId).includeUserData().build();
    }
    
    public static DataEnrichmentRequest interactionDataOnly(String contentId, ContentType contentType) {
        return builder()
                .contentId(contentId)
                .contentType(contentType)
                .includeInteractionData()
                .build();
    }
    
    public static DataEnrichmentRequest fullEnrichment(String userId, String contentId, ContentType contentType) {
        return builder()
                .userId(userId)
                .contentId(contentId)
                .contentType(contentType)
                .includeUserData()
                .includeInteractionData()
                .includeUserInteraction()
                .build();
    }
    
    public static class Builder {
        private String userId;
        private String contentId;
        private ContentType contentType;
        private boolean includeUserData = false;
        private boolean includeInteractionData = false;
        private boolean includeUserInteraction = false;
        private boolean useCache = true;
        private long timeoutMillis = 5000; // 5초 기본 타임아웃
        
        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder contentId(String contentId) {
            this.contentId = contentId;
            return this;
        }
        
        public Builder contentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }
        
        public Builder includeUserData() {
            this.includeUserData = true;
            return this;
        }
        
        public Builder includeInteractionData() {
            this.includeInteractionData = true;
            return this;
        }
        
        public Builder includeUserInteraction() {
            this.includeUserInteraction = true;
            return this;
        }
        
        public Builder useCache(boolean useCache) {
            this.useCache = useCache;
            return this;
        }
        
        public Builder timeout(long timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
            return this;
        }
        
        public DataEnrichmentRequest build() {
            return new DataEnrichmentRequest(this);
        }
    }
    
    @Override
    public String toString() {
        return String.format("DataEnrichmentRequest{userId='%s', contentId='%s', contentType=%s, " +
                "includeUserData=%s, includeInteractionData=%s, includeUserInteraction=%s, " +
                "useCache=%s, timeoutMillis=%d}",
                userId, contentId, contentType, includeUserData, 
                includeInteractionData, includeUserInteraction, useCache, timeoutMillis);
    }
}