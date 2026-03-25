package com.freelanceos.freelanceappback.infrastructure.persistence.mapper;

import com.freelanceos.freelanceappback.domain.model.client.Client;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.ClientEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {
    public ClientEntity toEntity(Client client, UserEntity user) {
        return new ClientEntity(
                client.id(),
                user,
                client.name(),
                client.contactEmail()
        );
    }

    public Client toDomain(ClientEntity clientEntity) {
        return new Client(
                clientEntity.getId(),
                clientEntity.getUser().getId(),
                clientEntity.getName(),
                clientEntity.getContactEmail()
        );
    }
}
