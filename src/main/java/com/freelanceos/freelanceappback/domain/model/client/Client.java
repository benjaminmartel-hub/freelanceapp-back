package com.freelanceos.freelanceappback.domain.model.client;

public record Client(Long id,
                     Long userId,
                     String name,
                     String contactEmail) {
}
