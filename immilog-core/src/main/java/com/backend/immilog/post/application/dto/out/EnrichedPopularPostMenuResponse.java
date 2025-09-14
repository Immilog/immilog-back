package com.backend.immilog.post.application.dto.out;

import com.backend.immilog.shared.application.gateway.EnrichedResult;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class EnrichedPopularPostMenuResponse {

    private final List<EnrichedResult<PostResult>> hotPosts;
    private final List<EnrichedResult<PostResult>> weeklyBest;
    private final int totalHotPosts;
    private final int totalWeeklyBest;
    private final boolean enrichmentIncluded;
    private final String currentUserId;
    private final LocalDateTime responseTime;
    private final String error;
    private final boolean fallbackUsed;
    
    private EnrichedPopularPostMenuResponse(Builder builder) {
        this.hotPosts = builder.hotPosts != null ? List.copyOf(builder.hotPosts) : List.of();
        this.weeklyBest = builder.weeklyBest != null ? List.copyOf(builder.weeklyBest) : List.of();
        this.totalHotPosts = builder.totalHotPosts;
        this.totalWeeklyBest = builder.totalWeeklyBest;
        this.enrichmentIncluded = builder.enrichmentIncluded;
        this.currentUserId = builder.currentUserId;
        this.responseTime = LocalDateTime.now();
        this.error = builder.error;
        this.fallbackUsed = builder.fallbackUsed;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static EnrichedPopularPostMenuResponse success(
            List<EnrichedResult<PostResult>> hotPosts, 
            List<EnrichedResult<PostResult>> weeklyBest) {
        return builder()
                .hotPosts(hotPosts)
                .weeklyBest(weeklyBest)
                .totalHotPosts(hotPosts.size())
                .totalWeeklyBest(weeklyBest.size())
                .enrichmentIncluded(true)
                .build();
    }
    
    public static EnrichedPopularPostMenuResponse fallback(
            List<PostResult> hotPosts, 
            List<PostResult> weeklyBest, 
            String error) {
        // PostResult를 EnrichedResult로 변환 (기본 데이터만)
        var hotEnriched = hotPosts.stream()
                .map(post -> EnrichedResult.<PostResult>builder().baseData(post).build())
                .toList();
        var weeklyEnriched = weeklyBest.stream()
                .map(post -> EnrichedResult.<PostResult>builder().baseData(post).build())
                .toList();
        
        return builder()
                .hotPosts(hotEnriched)
                .weeklyBest(weeklyEnriched)
                .totalHotPosts(hotPosts.size())
                .totalWeeklyBest(weeklyBest.size())
                .enrichmentIncluded(false)
                .error(error)
                .fallbackUsed(true)
                .build();
    }

    // Status methods
    public boolean hasError() {
        return error != null;
    }
    
    public boolean isSuccessful() {
        return !hasError() && !fallbackUsed;
    }
    
    public boolean hasUserSpecificData() {
        return currentUserId != null;
    }
    
    public int getTotalPosts() {
        return totalHotPosts + totalWeeklyBest;
    }
    
    public long getAverageEnrichmentTime() {
        var allResults = List.<EnrichedResult<PostResult>>of();
        allResults.addAll(hotPosts);
        allResults.addAll(weeklyBest);
        
        return (long) allResults.stream()
                .mapToLong(EnrichedResult::getEnrichmentTimeMillis)
                .average()
                .orElse(0.0);
    }
    
    public long getSuccessfulEnrichmentCount() {
        var allResults = List.<EnrichedResult<PostResult>>of();
        allResults.addAll(hotPosts);
        allResults.addAll(weeklyBest);
        
        return allResults.stream()
                .mapToLong(result -> result.isEnrichmentSuccessful() ? 1 : 0)
                .sum();
    }
    
    public static class Builder {
        private List<EnrichedResult<PostResult>> hotPosts;
        private List<EnrichedResult<PostResult>> weeklyBest;
        private int totalHotPosts;
        private int totalWeeklyBest;
        private boolean enrichmentIncluded = false;
        private String currentUserId;
        private String error;
        private boolean fallbackUsed = false;
        
        public Builder hotPosts(List<EnrichedResult<PostResult>> hotPosts) {
            this.hotPosts = hotPosts;
            return this;
        }
        
        public Builder weeklyBest(List<EnrichedResult<PostResult>> weeklyBest) {
            this.weeklyBest = weeklyBest;
            return this;
        }
        
        public Builder totalHotPosts(int totalHotPosts) {
            this.totalHotPosts = totalHotPosts;
            return this;
        }
        
        public Builder totalWeeklyBest(int totalWeeklyBest) {
            this.totalWeeklyBest = totalWeeklyBest;
            return this;
        }
        
        public Builder enrichmentIncluded(boolean enrichmentIncluded) {
            this.enrichmentIncluded = enrichmentIncluded;
            return this;
        }
        
        public Builder currentUserId(String currentUserId) {
            this.currentUserId = currentUserId;
            return this;
        }
        
        public Builder error(String error) {
            this.error = error;
            return this;
        }
        
        public Builder fallbackUsed(boolean fallbackUsed) {
            this.fallbackUsed = fallbackUsed;
            return this;
        }
        
        public EnrichedPopularPostMenuResponse build() {
            return new EnrichedPopularPostMenuResponse(this);
        }
    }
    
    @Override
    public String toString() {
        return String.format("EnrichedPopularPostMenuResponse{totalHotPosts=%d, totalWeeklyBest=%d, " +
                "enrichmentIncluded=%s, currentUserId='%s', hasError=%s, fallbackUsed=%s, responseTime=%s}",
                totalHotPosts, totalWeeklyBest, enrichmentIncluded, currentUserId, 
                hasError(), fallbackUsed, responseTime);
    }
}