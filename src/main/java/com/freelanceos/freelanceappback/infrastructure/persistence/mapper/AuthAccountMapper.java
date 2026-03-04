package com.freelanceos.freelanceappback.infrastructure.persistence.mapper;

import com.freelanceos.freelanceappback.domain.model.AuthAccount;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.AuthAccountEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthAccountMapper {
    public AuthAccount toDomain(AuthAccountEntity entity) {
        return new AuthAccount(
                entity.getId(),
                entity.getUsername(),
                entity.getPasswordHash(),
                entity.getProvider(),
                entity.getProviderUserId()
        );
    }

    public AuthAccountEntity toEntity(AuthAccount account) {
        return new AuthAccountEntity(
                account.getId(),
                account.getUsername(),
                account.getPasswordHash(),
                account.getProvider(),
                account.getProviderUserId()
        );
    }
}
