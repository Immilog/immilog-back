package com.backend.immilog.shared.infrastructure.event;

import com.backend.immilog.shared.domain.model.CommentData;
import com.backend.immilog.shared.domain.model.InteractionData;
import com.backend.immilog.shared.domain.model.UserData;
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
    private static final String USER_DATA_KEY_PREFIX = "event:user:";
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
            var key = INTERACTION_DATA_KEY_PREFIX + requestId;
            var jsonValue = objectMapper.writeValueAsString(interactionDataList);
            eventRedisTemplate.opsForValue().set(key, jsonValue, TTL.toSeconds(), TimeUnit.SECONDS);
            log.info("Stored {} interaction data items with key: {}", interactionDataList.size(), key);
            
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
            var key = BOOKMARK_DATA_KEY_PREFIX + requestId;
            var jsonValue = objectMapper.writeValueAsString(postIds);
            eventRedisTemplate.opsForValue().set(key, jsonValue, TTL.toSeconds(), TimeUnit.SECONDS);
            log.info("Stored {} bookmark post IDs with key: {}", postIds.size(), key);
            
            // 이벤트 처리 완료 신호
            completeEventProcessing(requestId);
        } catch (Exception e) {
            log.error("Failed to store bookmark data for requestId: {}", requestId, e);
            // 실패 시 예외로 완료 신호
            failEventProcessing(requestId, e);
        }
    }

    public void storeUserData(
            String requestId,
            List<UserData> userDataList
    ) {
        try {
            var key = USER_DATA_KEY_PREFIX + requestId;
            var jsonValue = objectMapper.writeValueAsString(userDataList);
            eventRedisTemplate.opsForValue().set(key, jsonValue, TTL.toSeconds(), TimeUnit.SECONDS);
            log.info("Stored {} user data items with key: {}", userDataList.size(), key);
            
            // 이벤트 처리 완료 신호
            completeEventProcessing(requestId);
        } catch (Exception e) {
            log.error("Failed to store user data for requestId: {}", requestId, e);
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
            var key = BOOKMARK_DATA_KEY_PREFIX + requestId;
            var result = eventRedisTemplate.opsForValue().get(key);
            
            if (result instanceof String jsonString) {
                return objectMapper.readValue(
                        jsonString,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
                );
            }
            
            log.warn("No bookmark data found for requestId: {}", requestId);
            return List.of();
        } catch (Exception e) {
            log.error("Failed to retrieve bookmark data for requestId: {}", requestId, e);
            return List.of();
        }
    }

    public List<UserData> getUserData(String requestId) {
        try {
            var key = USER_DATA_KEY_PREFIX + requestId;
            var result = eventRedisTemplate.opsForValue().get(key);
            
            if (result instanceof String jsonString) {
                return objectMapper.readValue(
                        jsonString,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, UserData.class)
                );
            }
            
            log.warn("No user data found for requestId: {}", requestId);
            return List.of();
        } catch (Exception e) {
            log.error("Failed to retrieve user data for requestId: {}", requestId, e);
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
        var future = new CompletableFuture<Void>();
        pendingEvents.put(requestId, future);
        log.info("Registered event processing future for requestId: {}", requestId);
        return future;
    }

    /**
     * 이벤트 처리 완료 신호를 보냅니다
     */
    private void completeEventProcessing(String requestId) {
        var future = pendingEvents.remove(requestId);
        if (future != null && !future.isDone()) {
            future.complete(null);
            log.info("Completed event processing for requestId: {}", requestId);
        }
    }

    /**
     * 이벤트 처리 실패 신호를 보냅니다
     */
    private void failEventProcessing(String requestId, Exception e) {
        var future = pendingEvents.remove(requestId);
        if (future != null && !future.isDone()) {
            future.completeExceptionally(e);
            log.info("Failed event processing for requestId: {}", requestId);
        }
    }

    /**
     * 이벤트 처리 완료를 기다리고 결과를 조회합니다
     */
    public List<InteractionData> waitForInteractionData(String requestId, Duration timeout) {
        try {
            var future = pendingEvents.get(requestId);
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
            var future = pendingEvents.get(requestId);
            if (future != null) {
                future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
            }
            return getBookmarkData(requestId);
        } catch (Exception e) {
            log.error("Failed to wait for bookmark data processing: {}", requestId, e);
            return List.of();
        }
    }

    /**
     * 이벤트 처리 완료를 기다리고 유저 데이터를 조회합니다
     */
    public List<UserData> waitForUserData(String requestId, Duration timeout) {
        try {
            var future = pendingEvents.get(requestId);
            if (future != null) {
                future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
            }
            return getUserData(requestId);
        } catch (Exception e) {
            log.error("Failed to wait for user data processing: {}", requestId, e);
            return List.of();
        }
    }
    
    // 일반적인 결과 저장 메소드 (다양한 타입 지원)
    public <T> void storeResult(String key, T result) {
        try {
            log.info("Storing result for key: {}, type: {}", key, result.getClass().getSimpleName());
            
            var jsonValue = objectMapper.writeValueAsString(result);
            eventRedisTemplate.opsForValue().set(key, jsonValue, TTL.toSeconds(), TimeUnit.SECONDS);
            
            log.info("Successfully stored result for key: {}", key);
            
            // 키에서 requestId 추출 (comment_data_, interaction_data_, user_validation_ 등의 접두사 제거)
            String requestId = extractRequestIdFromKey(key);
            var future = pendingEvents.remove(requestId);
            if (future != null) {
                future.complete(null);
                log.info("Completed event processing future for requestId: {}", requestId);
            } else {
                log.warn("No pending future found for requestId: {} (key: {})", requestId, key);
            }
        } catch (Exception e) {
            log.error("Failed to store result for key: {}", key, e);
            
            // 실패한 경우 Future를 예외적으로 완료
            String requestId = extractRequestIdFromKey(key);
            var future = pendingEvents.remove(requestId);
            if (future != null) {
                future.completeExceptionally(e);
                log.info("Failed event processing future for requestId: {}", requestId);
            }
        }
    }
    
    /**
     * 응답 키에서 requestId를 추출합니다
     */
    private String extractRequestIdFromKey(String key) {
        if (key.startsWith("comment_data_")) {
            return key.substring("comment_data_".length());
        } else if (key.startsWith("interaction_data_")) {
            return key.substring("interaction_data_".length());
        } else if (key.startsWith("user_validation_")) {
            return key.substring("user_validation_".length());
        } else if (key.startsWith(INTERACTION_DATA_KEY_PREFIX)) {
            return key.substring(INTERACTION_DATA_KEY_PREFIX.length());
        } else if (key.startsWith(BOOKMARK_DATA_KEY_PREFIX)) {
            return key.substring(BOOKMARK_DATA_KEY_PREFIX.length());
        } else if (key.startsWith(USER_DATA_KEY_PREFIX)) {
            return key.substring(USER_DATA_KEY_PREFIX.length());
        }
        
        // 접두사가 없으면 키 자체가 requestId일 가능성이 있음
        return key;
    }
    
    // 일반적인 결과 조회 메소드 (타입 안전성 보장)
    public <T> T getResult(String key, Class<T> type) {
        try {
            var result = eventRedisTemplate.opsForValue().get(key);
            if (result != null) {
                if (result instanceof String jsonString) {
                    if (type == Object.class) {
                        try {
                            return (T) objectMapper.readValue(
                                    jsonString,
                                    objectMapper.getTypeFactory().constructCollectionType(List.class, CommentData.class)
                            );
                        } catch (Exception e) {
                            log.warn("Failed to deserialize as List<CommentData>, trying as generic List for key: {}", key);
                            try {
                                return (T) objectMapper.readValue(
                                        jsonString,
                                        objectMapper.getTypeFactory().constructCollectionType(List.class, Object.class)
                                );
                            } catch (Exception e2) {
                                log.warn("Failed to deserialize as List<Object>, trying as generic object for key: {}", key);
                                return (T) objectMapper.readValue(jsonString, Object.class);
                            }
                        }
                    } else {
                        T deserializedResult = objectMapper.readValue(jsonString, type);
                        log.info("Retrieved and deserialized result for key: {}, type: {}", key, type.getSimpleName());
                        return deserializedResult;
                    }
                } else if (type.isAssignableFrom(result.getClass())) {
                    log.info("Retrieved result for key: {}, type: {}", key, type.getSimpleName());
                    return type.cast(result);
                }
            }
            log.warn("No result found for key: {} or type mismatch", key);
            return null;
        } catch (Exception e) {
            log.error("Failed to get result for key: {}", key, e);
            return null;
        }
    }
}