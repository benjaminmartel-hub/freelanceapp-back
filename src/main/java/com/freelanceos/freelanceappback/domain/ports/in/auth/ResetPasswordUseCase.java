package com.freelanceos.freelanceappback.domain.ports.in.auth;

public interface ResetPasswordUseCase {
    void execute(String username, String newPassword);
}
