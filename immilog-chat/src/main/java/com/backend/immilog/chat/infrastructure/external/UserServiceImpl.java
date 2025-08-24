package com.backend.immilog.chat.infrastructure.external;

import com.backend.immilog.chat.application.port.UserService;
import com.backend.immilog.chat.presentation.dto.ChatRoomDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 사용자 서비스 구현체 - User API 호출 (RestClient)
 */
@Service
public class UserServiceImpl implements UserService {
    
    private final RestClient restClient;
    private final String userApiBaseUrl;
    
    public UserServiceImpl(
            @Value("${app.user-api.base-url:http://localhost:8080}") String userApiBaseUrl
    ) {
        this.restClient = RestClient.builder().build();
        this.userApiBaseUrl = userApiBaseUrl;
    }
    
    @Override
    public Mono<ChatRoomDto.ParticipantInfo> getParticipantInfo(String userId) {
        return Mono.fromCallable(() -> {
            try {
                System.out.println("Calling User API for userId: " + userId);
                UserApiResponse response = restClient
                        .get()
                        .uri(userApiBaseUrl + "/api/v1/users/{userId}", userId)
                        .retrieve()
                        .body(UserApiResponse.class);
                
                if (response != null) {
                    ChatRoomDto.ParticipantInfo participant = new ChatRoomDto.ParticipantInfo(
                            response.userId(),
                            response.userNickname(),
                            response.userProfileUrl()
                    );
                    System.out.println("Loaded user: " + participant);
                    return participant;
                }
            } catch (Exception e) {
                System.out.println("Error loading user " + userId + ": " + e.getMessage());
            }
            return new ChatRoomDto.ParticipantInfo(userId, null, null);
        }).subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Mono<Map<String, ChatRoomDto.ParticipantInfo>> getParticipantsInfo(List<String> userIds) {
        System.out.println("Loading participants for userIds: " + userIds);
        return Flux.fromIterable(userIds)
                .flatMap(this::getParticipantInfo)
                .collectMap(
                        ChatRoomDto.ParticipantInfo::userId,
                        Function.identity()
                )
                .doOnNext(participants -> System.out.println("Loaded participants: " + participants));
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record UserApiResponse(
            String userId,
            String email,
            String userNickname,
            String userProfileUrl,
            String region,
            String country
    ) {}
}