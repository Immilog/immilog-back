package com.backend.immilog.user.infrastructure.repositories;

import com.backend.immilog.user.domain.model.User;
import com.backend.immilog.user.domain.model.UserId;
import com.backend.immilog.user.domain.repositories.UserRepository;
import com.backend.immilog.user.infrastructure.jpa.UserJpaEntity;
import com.backend.immilog.user.infrastructure.jpa.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByAuth_Email(email).map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        return jpaRepository.findByProfile_Nickname(nickname).map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return jpaRepository.findById(userId.value()).map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return jpaRepository.findById(userId).map(UserJpaEntity::toDomain);
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity;
        if (user.getUserId() != null) {
            var existingEntity = jpaRepository.findById(user.getUserId().value());
            if (existingEntity.isPresent()) {
                entity = existingEntity.get();
                entity.updateFromDomain(user);
            } else {
                entity = UserJpaEntity.from(user);
            }
        } else {
            entity = UserJpaEntity.from(user);
        }

        UserJpaEntity savedEntity = jpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByAuth_Email(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return jpaRepository.existsByProfile_Nickname(nickname);
    }
}
