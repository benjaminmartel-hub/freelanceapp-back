package com.freelanceos.freelanceappback.domain.ports.in.auth;

import com.freelanceos.freelanceappback.domain.model.AuthAccount;
import com.freelanceos.freelanceappback.domain.model.AuthProvider;

public interface LoginWithOAuthUseCase {
    AuthAccount execute(AuthProvider provider, String providerUserId, String preferredUsername);
}
