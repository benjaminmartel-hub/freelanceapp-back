package com.freelanceos.freelanceappback.application.rest;

import com.freelanceos.freelanceappback.application.rest.dto.auth.ResetPasswordRequest;
import com.freelanceos.freelanceappback.domain.ports.in.auth.ResetPasswordUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {
    private final ResetPasswordUseCase resetPasswordUseCase;

    public AdminAuthController(ResetPasswordUseCase resetPasswordUseCase) {
        this.resetPasswordUseCase = resetPasswordUseCase;
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            resetPasswordUseCase.execute(request.getUsername(), request.getNewPassword());
        } catch (IllegalArgumentException ex) {
            if ("User not found".equals(ex.getMessage())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
