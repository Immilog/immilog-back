package com.backend.immilog.shared.infrastructure.event;

import com.backend.immilog.shared.infrastructure.event.dto.RedisEventMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class RedisEventMessageDeserializationTest {

    @Test
    void testRedisEventMessageDeserialization() throws Exception {
        // ObjectMapper 설정 (실제 eventObjectMapper와 동일하게)
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // 테스트용 RedisEventMessage 생성
        RedisEventMessage originalMessage = new RedisEventMessage(
                "test-message-id",
                "com.backend.immilog.post.domain.events.PostEvent$InteractionDataRequested",
                "{\"requestId\":\"test-request\",\"postIds\":[\"post1\",\"post2\"],\"contentType\":\"POST\"}",
                LocalDateTime.of(2025, 8, 20, 22, 47, 6)
        );
        
        // JSON 문자열로 직렬화
        String json = mapper.writeValueAsString(originalMessage);
        System.out.println("Serialized JSON: " + json);
        
        // 다시 역직렬화
        RedisEventMessage deserializedMessage = mapper.readValue(json, RedisEventMessage.class);
        
        // 검증
        assertThat(deserializedMessage.messageId()).isEqualTo(originalMessage.messageId());
        assertThat(deserializedMessage.eventType()).isEqualTo(originalMessage.eventType());
        assertThat(deserializedMessage.payload()).isEqualTo(originalMessage.payload());
        assertThat(deserializedMessage.publishedAt()).isEqualTo(originalMessage.publishedAt());
    }
    
    @Test 
    void testActualErrorCaseDeserialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // 실제 에러에서 나타난 JSON 문자열
        String errorJson = "{\"messageId\":\"00dd5d53-a906-4738-b1b2-1858df2bfc9f\",\"eventType\":\"com.backend.immilog.post.domain.events.PostEvent$InteractionDataRequested\",\"payload\":\"{\\\"requestId\\\":\\\"interaction_1755697626865_1010941893\\\",\\\"postIds\\\":[\\\"EFYM0ByRDNr9-4bgpW7kD\\\",\\\"2aVqxJyz_pvCGdAGcBsOu\\\",\\\"Z30r8BbOs-wpjHDyGy6ss\\\",\\\"iltgZ4JcU1iAIauA000zv\\\",\\\"FkWadXO_VQpb0ocFu6PgN\\\",\\\"aoZQEvRlIcbDXXveXOtuD\\\",\\\"NjIHLcfuhzNrQCwoD3PaQ\\\",\\\"iQ8uo06Od0HD6Ucak6aXk\\\",\\\"mISIBvpqHDGs0lKBm_yXB\\\",\\\"mGbCS92WurDAxi3ZE509m\\\"],\\\"contentType\\\":\\\"POST\\\"}\",\"publishedAt\":\"2025-08-20T22:47:06\"}";
        
        System.out.println("Trying to deserialize error JSON: " + errorJson);
        
        // 이것이 실제로 역직렬화되는지 확인
        RedisEventMessage message = mapper.readValue(errorJson, RedisEventMessage.class);
        
        assertThat(message).isNotNull();
        assertThat(message.messageId()).isEqualTo("00dd5d53-a906-4738-b1b2-1858df2bfc9f");
    }
}