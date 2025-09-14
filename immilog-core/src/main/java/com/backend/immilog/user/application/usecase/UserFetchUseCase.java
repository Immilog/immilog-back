package com.backend.immilog.user.application.usecase;

import com.backend.immilog.user.application.dto.out.UserResult;
import com.backend.immilog.user.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFetchUseCase {

    private final UserRepository userQueryService;

    public UserResult getUserById(String userId) {
        var user = userQueryService.findById(userId);
        return UserResult.from(user);
    }
}