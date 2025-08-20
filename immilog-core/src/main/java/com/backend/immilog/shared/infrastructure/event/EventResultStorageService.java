package com.backend.immilog.shared.infrastructure.event;

import com.backend.immilog.shared.domain.model.InteractionData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EventResultStorageService {

    private static final String INTERACTION_DATA_KEY_PREFIX = "event:interaction:";
    private static final String BOOKMARK_DATA_KEY_PREFIX = "event:bookmark:";
    private static final Duration TTL = Duration.ofMinutes(5); // 5분 TTL

    private final RedisTemplate<String, Object> eventRedisTemplate;
    private final ObjectMapper objectMapper;
    
    // 이벤트 처리 완료를 기다리는 Future 맵
    private final ConcurrentHashMap<String, CompletableFuture<Void>> pendingEvents = new ConcurrentHashMap<>();

    public EventResultStorageService(
            RedisTemplate<String, Object> eventRedisTemplate,
            @Qualifier("eventObjectMapper") ObjectMapper objectMapper
    ) {
        this.eventRedisTemplate = eventRedisTemplate;
        this.objectMapper = objectMapper;
    }

    public void storeInteractionData(
            String requestId,
            List<InteractionData> interactionDataList
    ) {
        try {
            String key = INTERACTION_DATA_KEY_PREFIX + requestId;
            String jsonValue = objectMapper.writeValueAsString(interactionDataList);
            eventRedisTemplate.opsForValue().set(key, jsonValue, TTL.toSeconds(), TimeUnit.SECONDS);
            log.debug("Stored {} interaction data items with key: {}", interactionDataList.size(), key);
            
            // 이벤트 처리 완료 신호
            completeEventProcessing(requestId);
        } catch (Exception e) {
            log.error("Failed to store interaction data for requestId: {}", requestId, e);
            // 실패 시 예외로 완료 신호
            failEventProcessing(requestId, e);
        }
    }

    public void storeBookmarkData(
            String requestId,
            List<String> postIds
    ) {
        try {
            String key = BOOKMARK_DATA_KEY_PREFIX + requestId;
            String jsonValue = objectMapper.writeValueAsString(postIds);
            eventRedisTemplate.opsForValue().set(key, jsonValue, TTL.toSeconds(), TimeUnit.SECONDS);
            log.debug("Stored {} bookmark post IDs with key: {}", postIds.size(), key);
            
            // 이벤트 처리 완료 신호
            completeEventProcessing(requestId);
        } catch (Exception e) {
            log.error("Failed to store bookmark data for requestId: {}", requestId, e);
            // 실패 시 예외로 완료 신호
            failEventProcessing(requestId, e);
        }
    }

    public List<InteractionData> getInteractionData(String requestId) {
        try {
            String key = INTERACTION_DATA_KEY_PREFIX + requestId;
            Object result = eventRedisTemplate.opsForValue().get(key);
            
            if (result instanceof String jsonString) {
                return objectMapper.readValue(jsonString, 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, InteractionData.class));
            }
            
            log.warn("No interaction data found for requestId: {}", requestId);
            return List.of();
        } catch (Exception e) {
            log.error("Failed to retrieve interaction data for requestId: {}", requestId, e);
            return List.of();
        }
    }

    public List<String> getBookmarkData(String requestId) {
        try {
            String key = BOOKMARK_DATA_KEY_PREFIX + requestId;
            Object result = eventRedisTemplate.opsForValue().get(key);
            
            if (result instanceof String jsonString) {
                return objectMapper.readValue(jsonString, 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            }
            
            log.warn("No bookmark data found for requestId: {}", requestId);
            return List.of();
        } catch (Exception e) {
            log.error("Failed to retrieve bookmark data for requestId: {}", requestId, e);
            return List.of();
        }
    }

    public String generateRequestId(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "_" + Thread.currentThread().hashCode();
    }

    /**
     * 이벤트 처리 대기용 Future를 등록합니다
     */
    public CompletableFuture<Void> registerEventProcessing(String requestId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        pendingEvents.put(requestId, future);
        log.debug("Registered event processing future for requestId: {}", requestId);
        return future;
    }

    /**
     * 이벤트 처리 완료 신호를 보냅니다
     */
    private void completeEventProcessing(String requestId) {
        CompletableFuture<Void> future = pendingEvents.remove(requestId);
        if (future != null && !future.isDone()) {
            future.complete(null);
            log.debug("Completed event processing for requestId: {}", requestId);
        }
    }

    /**
     * 이벤트 처리 실패 신호를 보냅니다
     */
    private void failEventProcessing(String requestId, Exception e) {
        CompletableFuture<Void> future = pendingEvents.remove(requestId);
        if (future != null && !future.isDone()) {
            future.completeExceptionally(e);
            log.debug("Failed event processing for requestId: {}", requestId);
        }
    }

    /**
     * 이벤트 처리 완료를 기다리고 결과를 조회합니다
     */
    public List<InteractionData> waitForInteractionData(String requestId, Duration timeout) {
        try {
            CompletableFuture<Void> future = pendingEvents.get(requestId);
            if (future != null) {
                future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
            }
            return getInteractionData(requestId);
        } catch (Exception e) {
            log.error("Failed to wait for interaction data processing: {}", requestId, e);
            return List.of();
        }
    }

    /**
     * 이벤트 처리 완료를 기다리고 북마크 데이터를 조회합니다
     */
    public List<String> waitForBookmarkData(String requestId, Duration timeout) {
        try {
            CompletableFuture<Void> future = pendingEvents.get(requestId);
            if (future != null) {
                future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
            }
            return getBookmarkData(requestId);
        } catch (Exception e) {
            log.error("Failed to wait for bookmark data processing: {}", requestId, e);
            return List.of();
        }
    }
}