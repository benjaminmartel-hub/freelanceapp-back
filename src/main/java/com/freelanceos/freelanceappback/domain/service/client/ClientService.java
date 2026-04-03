package com.freelanceos.freelanceappback.domain.service.client;

import com.freelanceos.freelanceappback.domain.exception.BadRequestException;
import com.freelanceos.freelanceappback.domain.exception.ConflictException;
import com.freelanceos.freelanceappback.domain.exception.NotFoundException;
import com.freelanceos.freelanceappback.domain.model.client.Client;
import com.freelanceos.freelanceappback.domain.ports.in.client.CreateClientUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.client.DeleteClientUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.client.GetAllClientsUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.client.GetClientByIdUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.client.UpdateClientUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.ClientRepository;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.ClientMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService implements CreateClientUseCase,
        UpdateClientUseCase,
        GetAllClientsUseCase,
        GetClientByIdUseCase,
        DeleteClientUseCase {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientMapper clientMapper;

    public ClientService(ClientRepository clientRepository,
                         UserRepository userRepository,
                         ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.clientMapper = clientMapper;
    }

    @Override
    public List<Client> execute(String username) {
        Long userId = resolveUserId(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return clientRepository.findByUserId(userId).stream()
                .map(clientMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Client> execute(String username, Long id) {
        Long userId = resolveUserId(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return clientRepository.findByIdAndUserId(id, userId)
                .map(clientMapper::toDomain);
    }

    @Override
    public Client execute(String username, Client clientToCreate) {
        UserEntity user = resolveUser(username);
        if (clientRepository.existsByUserIdAndNameIgnoreCase(user.getId(), clientToCreate.name())) {
            throw new ConflictException("Client name already exists");
        }
        return clientMapper.toDomain(clientRepository.save(clientMapper.toEntity(clientToCreate, user)));
    }

    @Override
    public Optional<Client> execute(String username, Long id, Client clientToUpdate) {
        Long userId = resolveUserId(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (clientRepository.existsByUserIdAndNameIgnoreCaseAndIdNot(userId, clientToUpdate.name(), id)) {
            throw new ConflictException("Client name already exists");
        }
        UserEntity user = resolveUser(username);
        return clientRepository.update(id, clientMapper.toEntity(clientToUpdate, user))
                .map(clientMapper::toDomain);
    }

    @Override
    public boolean delete(String username, Long id) {
        Long userId = resolveUserId(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return clientRepository.delete(id, userId);
    }

    private UserEntity resolveUser(String username) {
        return userRepository.findByNameIgnoreCase(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Optional<Long> resolveUserId(String username) {
        if (username == null || username.isBlank()) {
            throw new BadRequestException("Username is required");
        }

        return userRepository.findByNameIgnoreCase(username)
                .map(UserEntity::getId);
    }
}
