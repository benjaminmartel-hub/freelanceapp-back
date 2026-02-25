package com.freelanceos.freelanceappback.infrastructure.persistence.adapter;

import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.repository.SpringDataUserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final SpringDataUserJpaRepository userJpaRepository;

    public InMemoryUserRepository(SpringDataUserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public List<UserEntity> findAll() {
        return userJpaRepository.findAll();
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public UserEntity save(UserEntity user) {
        user.setId(null);
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<UserEntity> update(Long id, UserEntity user) {
        if (!userJpaRepository.existsById(id)) {
            return Optional.empty();
        }
        user.setId(id);
        return Optional.of(userJpaRepository.save(user));
    }

    @Override
    public boolean deleteById(Long id) {
        if (!userJpaRepository.existsById(id)) {
            return false;
        }
        userJpaRepository.deleteById(id);
        return true;
    }
}
