package com.freelanceos.freelanceappback.infrastructure.persistence.repository;

import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUserJpaRepository extends JpaRepository<UserEntity, Long> {
    java.util.Optional<UserEntity> findByEmailIgnoreCase(String email);

    java.util.Optional<UserEntity> findByNameIgnoreCase(String name);
}
