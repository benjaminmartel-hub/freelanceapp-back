package com.freelanceos.freelanceappback.domain.ports.in.auth;

import com.freelanceos.freelanceappback.domain.model.auth.AuthAccount;

public interface LoginWithPasswordUseCase {
    AuthAccount execute(String username, String password);
}
