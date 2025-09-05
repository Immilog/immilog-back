package com.backend.immilog.shared.application.gateway;

import com.backend.immilog.shared.domain.model.InteractionData;
import com.backend.immilog.shared.domain.model.UserData;

/**
 * 도메인 간 데이터가 조합된 결과를 담는 클래스
 * 기본 데이터와 추가로 조합된 데이터들을 포함
 */
public class EnrichedResult<T> {
    
    private final T baseData;
    private final UserData userData;
    private final InteractionData interactionData;
    private final InteractionData userInteractionData;
    private final String error;
    private final boolean fallbackUsed;
    private final long enrichmentTimeMillis;
    
    private EnrichedResult(Builder<T> builder) {
        this.baseData = builder.baseData;
        this.userData = builder.userData;
        this.interactionData = builder.interactionData;
        this.userInteractionData = builder.userInteractionData;
        this.error = builder.error;
        this.fallbackUsed = builder.fallbackUsed;
        this.enrichmentTimeMillis = System.currentTimeMillis() - builder.startTime;
    }
    
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }
    
    // Getters
    public T getBaseData() { return baseData; }
    public UserData getUserData() { return userData; }
    public InteractionData getInteractionData() { return interactionData; }
    public InteractionData getUserInteractionData() { return userInteractionData; }
    public String getError() { return error; }
    public boolean isFallbackUsed() { return fallbackUsed; }
    public long getEnrichmentTimeMillis() { return enrichmentTimeMillis; }
    
    // 상태 확인 메서드들
    public boolean hasError() {
        return error != null;
    }
    
    public boolean hasUserData() {
        return userData != null;
    }
    
    public boolean hasInteractionData() {
        return interactionData != null;
    }
    
    public boolean hasUserInteractionData() {
        return userInteractionData != null;
    }
    
    public boolean isEnrichmentSuccessful() {
        return !hasError() && !fallbackUsed;
    }
    
    public boolean isPartiallyEnriched() {
        return hasError() && !fallbackUsed;
    }
    
    // 편의 메서드들
    public String getUserNickname() {
        return hasUserData() ? userData.nickname() : "Unknown";
    }
    
    public String getUserImageUrl() {
        return hasUserData() ? userData.profileImageUrl() : null;
    }
    
    public long getLikeCount() {
        // InteractionData는 개별 상호작용 데이터이므로 카운트 로직을 별도로 구현해야 함
        return 0L; // TODO: 실제 좋아요 수 계산 로직 필요
    }
    
    public boolean isLikedByCurrentUser() {
        // UserInteractionData를 통해 현재 사용자의 좋아요 여부 확인
        return hasUserInteractionData() && "LIKE".equals(userInteractionData.interactionType()) && "ACTIVE".equals(userInteractionData.interactionStatus());
    }
    
    public boolean isBookmarkedByCurrentUser() {
        // UserInteractionData를 통해 현재 사용자의 북마크 여부 확인  
        return hasUserInteractionData() && "BOOKMARK".equals(userInteractionData.interactionType()) && "ACTIVE".equals(userInteractionData.interactionStatus());
    }
    
    public static class Builder<T> {
        private T baseData;
        private UserData userData;
        private InteractionData interactionData;
        private InteractionData userInteractionData;
        private String error;
        private boolean fallbackUsed = false;
        private final long startTime = System.currentTimeMillis();
        
        public Builder<T> baseData(T baseData) {
            this.baseData = baseData;
            return this;
        }
        
        public Builder<T> userData(UserData userData) {
            this.userData = userData;
            return this;
        }
        
        public Builder<T> interactionData(InteractionData interactionData) {
            this.interactionData = interactionData;
            return this;
        }
        
        public Builder<T> userInteractionData(InteractionData userInteractionData) {
            this.userInteractionData = userInteractionData;
            return this;
        }
        
        public Builder<T> error(String error) {
            this.error = error;
            return this;
        }
        
        public Builder<T> fallbackUsed(boolean fallbackUsed) {
            this.fallbackUsed = fallbackUsed;
            return this;
        }
        
        public EnrichedResult<T> build() {
            if (baseData == null) {
                throw new IllegalArgumentException("Base data cannot be null");
            }
            return new EnrichedResult<>(this);
        }
    }
    
    @Override
    public String toString() {
        return String.format("EnrichedResult{baseData=%s, hasUserData=%s, hasInteractionData=%s, " +
                "hasUserInteractionData=%s, hasError=%s, fallbackUsed=%s, enrichmentTimeMillis=%d}",
                baseData.getClass().getSimpleName(), hasUserData(), hasInteractionData(), 
                hasUserInteractionData(), hasError(), fallbackUsed, enrichmentTimeMillis);
    }
}