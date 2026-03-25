package com.freelanceos.freelanceappback.infrastructure.persistence.adapter;

import com.freelanceos.freelanceappback.domain.ports.out.ClientRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.ClientEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.repository.SpringDataClientJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaClientRepositoryAdapter implements ClientRepository {
    private final SpringDataClientJpaRepository clientJpaRepository;

    public JpaClientRepositoryAdapter(SpringDataClientJpaRepository clientJpaRepository) {
        this.clientJpaRepository = clientJpaRepository;
    }

    @Override
    public List<ClientEntity> findByUserId(Long userId) {
        return clientJpaRepository.findByUserIdOrderByNameAsc(userId);
    }

    @Override
    public Optional<ClientEntity> findByIdAndUserId(Long id, Long userId) {
        return clientJpaRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public boolean existsByUserIdAndNameIgnoreCase(Long userId, String name) {
        return clientJpaRepository.existsByUserIdAndNameIgnoreCase(userId, name);
    }

    @Override
    public boolean existsByUserIdAndNameIgnoreCaseAndIdNot(Long userId, String name, Long id) {
        return clientJpaRepository.existsByUserIdAndNameIgnoreCaseAndIdNot(userId, name, id);
    }

    @Override
    public ClientEntity save(ClientEntity client) {
        return clientJpaRepository.save(client);
    }

    @Override
    public Optional<ClientEntity> update(Long id, ClientEntity client) {
        return clientJpaRepository.findByIdAndUserId(id, client.getUser().getId())
                .map(existing -> {
                    existing.setName(client.getName());
                    existing.setContactEmail(client.getContactEmail());
                    return clientJpaRepository.save(existing);
                });
    }

    @Override
    public boolean delete(Long id, Long userId) {
        return clientJpaRepository.deleteByIdAndUserId(id, userId) > 0;
    }
}
