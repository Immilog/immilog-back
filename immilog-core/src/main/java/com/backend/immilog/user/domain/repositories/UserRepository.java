package com.backend.immilog.user.domain.repositories;

import com.backend.immilog.user.domain.model.User;
import com.backend.immilog.user.domain.model.UserId;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    Optional<User> findById(UserId userId);

    Optional<User> findById(String userId);

    User save(User user);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
