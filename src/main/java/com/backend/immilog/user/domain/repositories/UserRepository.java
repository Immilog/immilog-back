package com.backend.immilog.user.domain.repositories;

import com.backend.immilog.user.domain.model.user.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> getByEmail(String email);

    Optional<User> getByUserNickname(String nickname);

    Optional<User> getById(Long id);

    User save(User user);
}
