package com.backend.immilog.chat.domain.service;

import com.backend.immilog.chat.domain.service.usecase.ChatRoomReadStatusInitializeUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ChatRoomReadStatusDomainService extends ChatRoomReadStatusInitializeUseCase {
    Logger log = LoggerFactory.getLogger(ChatRoomReadStatusDomainService.class);
}
