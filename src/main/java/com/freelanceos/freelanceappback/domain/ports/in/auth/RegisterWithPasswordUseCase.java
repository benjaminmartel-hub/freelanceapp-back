package com.freelanceos.freelanceappback.domain.ports.in.auth;

import com.freelanceos.freelanceappback.domain.model.auth.AuthAccount;

public interface RegisterWithPasswordUseCase {
    AuthAccount execute(String username, String password);
}
