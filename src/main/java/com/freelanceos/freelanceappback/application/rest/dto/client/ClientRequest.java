package com.freelanceos.freelanceappback.application.rest.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClientRequest(
        @NotBlank String name,
        @Email String contactEmail
) {
}
