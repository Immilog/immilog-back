package com.backend.immilog.shared.infrastructure.event;

import com.backend.immilog.shared.domain.model.InteractionData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EventResultStorageService {

    private static final String INTERACTION_DATA_KEY_PREFIX = "event:interaction:";
    private static final String BOOKMARK_DATA_KEY_PREFIX = "event:bookmark:";
    private static final Duration TTL = Duration.ofMinutes(5); // 5분 TTL

    private final RedisTemplate<String, Object> eventRedisTemplate;
    private final ObjectMapper objectMapper;

    public EventResultStorageService(
            RedisTemplate<String, Object> eventRedisTemplate,
            ObjectMapper objectMapper
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
            eventRedisTemplate.opsForValue().set(key, interactionDataList, TTL.toSeconds(), TimeUnit.SECONDS);
            log.debug("Stored {} interaction data items with key: {}", interactionDataList.size(), key);
        } catch (Exception e) {
            log.error("Failed to store interaction data for requestId: {}", requestId, e);
        }
    }

    public void storeBookmarkData(
            String requestId,
            List<String> postIds
    ) {
        try {
            String key = BOOKMARK_DATA_KEY_PREFIX + requestId;
            eventRedisTemplate.opsForValue().set(key, postIds, TTL.toSeconds(), TimeUnit.SECONDS);
            log.debug("Stored {} bookmark post IDs with key: {}", postIds.size(), key);
        } catch (Exception e) {
            log.error("Failed to store bookmark data for requestId: {}", requestId, e);
        }
    }

    public List<InteractionData> getInteractionData(String requestId) {
        try {
            String key = INTERACTION_DATA_KEY_PREFIX + requestId;
            Object result = eventRedisTemplate.opsForValue().get(key);
            
            if (result instanceof List<?> list) {
                // Redis에서 가져온 데이터가 LinkedHashMap 형태일 수 있으므로 변환
                return list.stream()
                        .map(item -> {
                            if (item instanceof InteractionData interactionData) {
                                return interactionData;
                            } else {
                                // LinkedHashMap 형태의 데이터를 InteractionData로 변환
                                return objectMapper.convertValue(item, InteractionData.class);
                            }
                        })
                        .toList();
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
            
            if (result instanceof List<?> list) {
                return list.stream()
                        .map(String::valueOf)
                        .toList();
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
}