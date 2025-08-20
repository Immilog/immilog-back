package com.backend.immilog.chat.websocket;

import com.backend.immilog.chat.presentation.dto.UserNotificationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserNotificationWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper;
    private final Map<String, Sinks.Many<UserNotificationDto.UnreadCountUpdateMessage>> userSinks = new ConcurrentHashMap<>();

    public UserNotificationWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String userId = extractUserId(session);
        
        if (userId == null) {
            return session.close();
        }

        var sink = userSinks.computeIfAbsent(
                userId,
                k -> Sinks.many().multicast().onBackpressureBuffer()
        );

        var input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .then();

        var output = sink.asFlux()
                .map(this::messageToJson)
                .map(session::textMessage);

        return session.send(output)
                .and(input)
                .doFinally(signalType -> cleanupSession(userId));
    }

    private String extractUserId(WebSocketSession session) {
        String path = session.getHandshakeInfo().getUri().getPath();
        // /ws/user/{userId}/notifications 형태에서 userId 추출
        String[] segments = path.split("/");
        return segments.length >= 4 ? segments[3] : null;
    }

    private String messageToJson(UserNotificationDto.UnreadCountUpdateMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            return "{}";
        }
    }

    private void cleanupSession(String userId) {
        userSinks.remove(userId);
    }

    public void sendUnreadCountUpdate(String userId, UserNotificationDto.UnreadCountUpdateMessage message) {
        var sink = userSinks.get(userId);
        if (sink != null) {
            sink.tryEmitNext(message);
        }
    }
}