package com.freelanceos.freelanceappback.domain.ports.in.auth;

import com.freelanceos.freelanceappback.domain.model.auth.AuthAccount;
import com.freelanceos.freelanceappback.domain.model.auth.AuthProvider;

public interface LoginWithOAuthUseCase {
    AuthAccount execute(AuthProvider provider, String providerUserId, String preferredUsername);
}
