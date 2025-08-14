package com.backend.immilog.shared.infrastructure.event.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RedisEventMessageTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testDeserialization() throws Exception {
        String json = "{\"messageId\":\"7226d7e6-260b-45c7-9e9f-afdedf77cefc\",\"eventType\":\"com.backend.immilog.post.domain.events.PostEvent$InteractionDataRequested\",\"payload\":\"{\\\"requestId\\\":\\\"interaction_1754364571653_1041388030\\\",\\\"postIds\\\":[\\\"iQ8uo06Od0HD6Ucak6aXk\\\"],\\\"contentType\\\":\\\"POST\\\"}\",\"publishedAt\":\"2025-08-05T12:29:31\"}";
        
        RedisEventMessage message = objectMapper.readValue(json, RedisEventMessage.class);
        
        assertNotNull(message);
        assertEquals("7226d7e6-260b-45c7-9e9f-afdedf77cefc", message.messageId());
        assertEquals("com.backend.immilog.post.domain.events.PostEvent$InteractionDataRequested", message.eventType());
        assertEquals(LocalDateTime.of(2025, 8, 5, 12, 29, 31), message.publishedAt());
    }

    @Test
    void testSerialization() throws Exception {
        RedisEventMessage message = new RedisEventMessage(
            "test-id",
            "test.Event",
            "{\"test\":\"payload\"}",
            LocalDateTime.of(2025, 8, 5, 12, 0, 0)
        );
        
        String json = objectMapper.writeValueAsString(message);
        System.out.println("Serialized: " + json);
        
        RedisEventMessage deserialized = objectMapper.readValue(json, RedisEventMessage.class);
        assertEquals(message.messageId(), deserialized.messageId());
        assertEquals(message.eventType(), deserialized.eventType());
        assertEquals(message.payload(), deserialized.payload());
        assertEquals(message.publishedAt(), deserialized.publishedAt());
    }
}