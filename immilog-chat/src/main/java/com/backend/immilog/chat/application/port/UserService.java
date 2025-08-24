package com.backend.immilog.chat.application.port;

import com.backend.immilog.chat.presentation.dto.ChatRoomDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface UserService {
    
    /**
     * 사용자 ID로 사용자 정보 조회
     */
    Mono<ChatRoomDto.ParticipantInfo> getParticipantInfo(String userId);
    
    /**
     * 여러 사용자 ID로 사용자 정보 일괄 조회
     */
    Mono<Map<String, ChatRoomDto.ParticipantInfo>> getParticipantsInfo(List<String> userIds);
}