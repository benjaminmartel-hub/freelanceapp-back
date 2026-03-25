package com.freelanceos.freelanceappback.domain.service.client;

import com.freelanceos.freelanceappback.domain.model.client.Client;
import com.freelanceos.freelanceappback.domain.ports.out.ClientRepository;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.ClientEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.ClientMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientService clientService;

    @Test
    void getAllShouldReturnClients() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        ClientEntity entity = new ClientEntity(1L, user, "Maison Beldi", "contact@maisonbeldi.com");
        Client client = new Client(1L, 1L, "Maison Beldi", "contact@maisonbeldi.com");

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(clientRepository.findByUserId(1L)).thenReturn(List.of(entity));
        when(clientMapper.toDomain(entity)).thenReturn(client);

        List<Client> result = clientService.execute("demo");

        assertThat(result).containsExactly(client);
        verify(clientRepository).findByUserId(1L);
    }

    @Test
    void getByIdShouldReturnClient() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        ClientEntity entity = new ClientEntity(1L, user, "Maison Beldi", "contact@maisonbeldi.com");
        Client client = new Client(1L, 1L, "Maison Beldi", "contact@maisonbeldi.com");

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(clientRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(entity));
        when(clientMapper.toDomain(entity)).thenReturn(client);

        Optional<Client> result = clientService.execute("demo", 1L);

        assertThat(result).contains(client);
    }

    @Test
    void createShouldRejectWhenNameExists() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        Client toCreate = new Client(null, null, "Maison Beldi", "contact@maisonbeldi.com");

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(clientRepository.existsByUserIdAndNameIgnoreCase(1L, "Maison Beldi")).thenReturn(true);

        assertThatThrownBy(() -> clientService.execute("demo", toCreate))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Client name already exists");
        verify(clientRepository, never()).save(any(ClientEntity.class));
    }

    @Test
    void createShouldReturnSavedClient() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        Client toCreate = new Client(null, null, "Maison Beldi", "contact@maisonbeldi.com");
        ClientEntity entity = new ClientEntity(1L, user, "Maison Beldi", "contact@maisonbeldi.com");
        Client saved = new Client(1L, 1L, "Maison Beldi", "contact@maisonbeldi.com");

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(clientRepository.existsByUserIdAndNameIgnoreCase(1L, "Maison Beldi")).thenReturn(false);
        when(clientMapper.toEntity(toCreate, user)).thenReturn(entity);
        when(clientRepository.save(entity)).thenReturn(entity);
        when(clientMapper.toDomain(entity)).thenReturn(saved);

        Client result = clientService.execute("demo", toCreate);

        assertThat(result).isEqualTo(saved);
    }

    @Test
    void updateShouldReturnEmptyWhenMissing() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        Client toUpdate = new Client(1L, null, "Maison Beldi", "contact@maisonbeldi.com");
        ClientEntity entity = new ClientEntity(1L, user, "Maison Beldi", "contact@maisonbeldi.com");

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(clientRepository.existsByUserIdAndNameIgnoreCaseAndIdNot(1L, "Maison Beldi", 1L)).thenReturn(false);
        when(clientMapper.toEntity(toUpdate, user)).thenReturn(entity);
        when(clientRepository.update(1L, entity)).thenReturn(Optional.empty());

        Optional<Client> result = clientService.execute("demo", 1L, toUpdate);

        assertThat(result).isEmpty();
    }

    @Test
    void updateShouldRejectWhenNameExists() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        Client toUpdate = new Client(1L, null, "Maison Beldi", "contact@maisonbeldi.com");

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(clientRepository.existsByUserIdAndNameIgnoreCaseAndIdNot(1L, "Maison Beldi", 1L)).thenReturn(true);

        assertThatThrownBy(() -> clientService.execute("demo", 1L, toUpdate))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Client name already exists");
        verify(clientRepository, never()).update(eq(1L), any(ClientEntity.class));
    }

    @Test
    void deleteShouldReturnTrueWhenDeleted() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(clientRepository.delete(1L, 1L)).thenReturn(true);

        boolean result = clientService.delete("demo", 1L);

        assertThat(result).isTrue();
    }
}
