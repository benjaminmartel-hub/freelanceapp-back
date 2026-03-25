package com.freelanceos.freelanceappback.infrastructure.persistence.adapter;

import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.repository.SpringDataUserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {
    private final SpringDataUserJpaRepository userJpaRepository;

    public JpaUserRepositoryAdapter(SpringDataUserJpaRepository userJpaRepository) {
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
    public Optional<UserEntity> findByEmailIgnoreCase(String email) {
        return userJpaRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public Optional<UserEntity> findByNameIgnoreCase(String name) {
        return userJpaRepository.findByNameIgnoreCase(name);
    }

    @Override
    public UserEntity save(UserEntity user) {
        user.setId(null);
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<UserEntity> update(Long id, UserEntity user) {
        return userJpaRepository.findById(id)
                .map(existing -> {
                    existing.setName(user.getName());
                    existing.setEmail(user.getEmail());
                    if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                        existing.setRoles(user.getRoles());
                    }
                    return userJpaRepository.save(existing);
                });
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
