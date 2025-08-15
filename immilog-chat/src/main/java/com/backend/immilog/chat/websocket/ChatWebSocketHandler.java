package com.backend.immilog.chat.websocket;

import com.backend.immilog.chat.application.service.ChatMessageService;
import com.backend.immilog.chat.application.service.ChatRoomService;
import com.backend.immilog.chat.presentation.dto.ChatMessageDto;
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
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final ObjectMapper objectMapper;

    private final Map<String, Sinks.Many<ChatMessageDto>> chatRoomSinks = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(
            ChatMessageService chatMessageService,
            ChatRoomService chatRoomService,
            ObjectMapper objectMapper
    ) {
        this.chatMessageService = chatMessageService;
        this.chatRoomService = chatRoomService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // URL에서 채팅방 ID 추출
        var chatRoomId = this.extractChatRoomId(session);

        if (chatRoomId == null) {
            return session.close();
        }

        // 해당 채팅방의 Sink를 가져오거나 생성
        var sink = chatRoomSinks.computeIfAbsent(
                chatRoomId,
                k -> Sinks.many().multicast().onBackpressureBuffer()
        );

        var input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(payload -> handleIncomingMessage(payload, chatRoomId))
                .doOnNext(sink::tryEmitNext)
                .then();

        var output = sink.asFlux()
                .map(this::messageToJson)
                .map(session::textMessage);

        return session.send(output)
                .and(input)
                .doFinally(signalType -> cleanupSession(chatRoomId));
    }

    private String extractChatRoomId(WebSocketSession session) {
        String path = session.getHandshakeInfo().getUri().getPath();
        // /ws/chat/{chatRoomId} 형태에서 chatRoomId 추출
        String[] segments = path.split("/");
        return segments.length >= 3 ? segments[3] : null;
    }

    private Mono<ChatMessageDto> handleIncomingMessage(
            String payload,
            String chatRoomId
    ) {
        try {
            ChatMessageRequest request = objectMapper.readValue(payload, ChatMessageRequest.class);

            switch (request.type()) {
                case "MESSAGE":
                    return chatMessageService.sendMessage(
                            chatRoomId,
                            request.senderId(),
                            request.senderNickname(),
                            request.content()
                    ).map(ChatMessageDto::from);

                case "JOIN":
                    return chatRoomService.joinChatRoom(chatRoomId, request.senderId())
                            .then(chatMessageService.sendSystemMessage(
                                    chatRoomId,
                                    request.senderId(),
                                    request.senderNickname(),
                                    com.backend.immilog.chat.domain.model.ChatMessage.MessageType.SYSTEM_JOIN
                            ))
                            .map(ChatMessageDto::from);

                case "LEAVE":
                    return chatMessageService.sendSystemMessage(
                                    chatRoomId,
                                    request.senderId(),
                                    request.senderNickname(),
                                    com.backend.immilog.chat.domain.model.ChatMessage.MessageType.SYSTEM_LEAVE
                            )
                            .map(ChatMessageDto::from)
                            .doOnNext(msg -> chatRoomService.leaveChatRoom(chatRoomId, request.senderId()).subscribe());

                default:
                    return Mono.empty();
            }
        } catch (Exception e) {
            return Mono.empty();
        }
    }

    private String messageToJson(ChatMessageDto message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            return "{}";
        }
    }

    private void cleanupSession(String chatRoomId) {
    }

    public record ChatMessageRequest(
            String type,
            String senderId,
            String senderNickname,
            String content
    ) {}
}