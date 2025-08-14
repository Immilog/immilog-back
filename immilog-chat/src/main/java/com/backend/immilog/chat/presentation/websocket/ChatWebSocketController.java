package com.backend.immilog.chat.presentation.websocket;

import com.backend.immilog.chat.application.service.ChatMessageService;
import com.backend.immilog.chat.application.service.ChatRoomService;
import com.backend.immilog.chat.domain.model.ChatMessage;
import com.backend.immilog.chat.presentation.dto.ChatMessageDto;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatWebSocketController {
    
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    
    public ChatWebSocketController(
            ChatMessageService chatMessageService,
            ChatRoomService chatRoomService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.chatMessageService = chatMessageService;
        this.chatRoomService = chatRoomService;
        this.messagingTemplate = messagingTemplate;
    }
    
    @MessageMapping("/chat/{chatRoomId}/send")
    public void sendMessage(
            @DestinationVariable String chatRoomId,
            @Payload ChatMessageDto messageDto,
            Principal principal
    ) {
        var userId = principal.getName();
        
        chatMessageService.sendMessage(
                        chatRoomId,
                        userId,
                        messageDto.senderNickname(),
                        messageDto.content()
                )
                .subscribe(message -> {
                    // 해당 채팅방 구독자들에게 메시지 브로드캐스트
                    messagingTemplate.convertAndSend(
                            "/topic/chat/" + chatRoomId,
                            ChatMessageDto.from(message)
                    );
                });
    }
    
    @MessageMapping("/chat/{chatRoomId}/join")
    public void joinChatRoom(
            @DestinationVariable String chatRoomId,
            @Payload ChatMessageDto joinMessage,
            Principal principal
    ) {
        String userId = principal.getName();
        
        // 채팅방에 사용자 추가
        chatRoomService.joinChatRoom(chatRoomId, userId)
                .then(chatMessageService.sendSystemMessage(
                        chatRoomId,
                        userId,
                        joinMessage.senderNickname(),
                        ChatMessage.MessageType.SYSTEM_JOIN
                ))
                .subscribe(message -> {
                    // 참여 메시지 브로드캐스트
                    messagingTemplate.convertAndSend(
                            "/topic/chat/" + chatRoomId,
                            ChatMessageDto.from(message)
                    );
                });
    }
    
    @MessageMapping("/chat/{chatRoomId}/leave")
    public void leaveChatRoom(
            @DestinationVariable String chatRoomId,
            @Payload ChatMessageDto leaveMessage,
            Principal principal
    ) {
        var userId = principal.getName();
        
        // 시스템 메시지 먼저 전송
        chatMessageService.sendSystemMessage(
                        chatRoomId,
                        userId,
                        leaveMessage.senderNickname(),
                        ChatMessage.MessageType.SYSTEM_LEAVE
                )
                .subscribe(message -> {
                    // 퇴장 메시지 브로드캐스트
                    messagingTemplate.convertAndSend(
                            "/topic/chat/" + chatRoomId,
                            ChatMessageDto.from(message)
                    );
                });
        
        // 채팅방에서 사용자 제거
        chatRoomService.leaveChatRoom(chatRoomId, userId).subscribe();
    }
}