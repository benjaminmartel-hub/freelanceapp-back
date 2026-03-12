package com.freelanceos.freelanceappback.domain.ports.out;

import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<UserEntity> findAll();

    Optional<UserEntity> findById(Long id);

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    Optional<UserEntity> findByNameIgnoreCase(String name);

    UserEntity save(UserEntity user);

    Optional<UserEntity> update(Long id, UserEntity user);

    boolean deleteById(Long id);
}
